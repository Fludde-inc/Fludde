#!/usr/bin/env bash
set -Eeuo pipefail

# ------------------------------------------------------------
# Fludde: build + install the debug APK onto an Android emulator
# then launch the app deterministically with Activity Manager.
# Falls back to a *forced* Monkey app-switch if needed.
# ------------------------------------------------------------
# Usage:
#   ./scripts/android-run.sh                 # uses default APK path & best AVD
#   FLUDDE_AVD=Pixel_7_API_35 ./scripts/android-run.sh
#   ./scripts/android-run.sh path/to/your.apk
# ------------------------------------------------------------

APP_ID="com.example.fludde"
MAIN_ACTIVITY=".MainActivity"
APK="${1:-app/build/outputs/apk/debug/app-debug.apk}"

log() { printf "👉 %s\n" "$*"; }
ok()  { printf "✅ %s\n" "$*"; }
warn(){ printf "⚠️  %s\n" "$*" >&2; }
err() { printf "❌ %s\n" "$*" >&2; }

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    err "Missing command: $1 (check your Android SDK install and PATH)"
    exit 1
  fi
}

require_cmd adb
require_cmd emulator

if [[ ! -f "$APK" ]]; then
  err "APK not found at: $APK"
  log "Try: ./gradlew :app:assembleDebug"
  exit 1
fi

pick_avd() {
  local preferred="${FLUDDE_AVD:-}"
  local list
  if ! list="$(emulator -list-avds)"; then
    err "Failed to list AVDs. Create one in Android Studio or with avdmanager."
    exit 1
  fi

  if [[ -n "$preferred" ]]; then
    if grep -Fxq "$preferred" <<<"$list"; then
      echo "$preferred"; return
    else
      err "Requested AVD '$preferred' not found. Available:"
      echo "$list"
      exit 1
    fi
  fi

  if grep -Fxq "Pixel_7_API_35" <<<"$list"; then echo "Pixel_7_API_35"; return; fi
  if grep -Fxq "fluddeApi35"   <<<"$list"; then echo "fluddeApi35";   return; fi

  local first
  first="$(head -n 1 <<<"$list" || true)"
  if [[ -z "$first" ]]; then
    err "No AVDs exist yet."
    cat <<'EOF'
Create an AVD, e.g.:
  sdkmanager --install "platform-tools" "emulator" "platforms;android-35" "system-images;android-35;google_apis;x86_64"
  echo "no" | avdmanager create avd -n fluddeApi35 -k "system-images;android-35;google_apis;x86_64" --device "pixel_7"
EOF
    exit 1
  fi
  echo "$first"
}

avd_name="$(pick_avd)"
ok "Using AVD: $avd_name"

adb_start() {
  adb start-server >/dev/null 2>&1 || true
}

any_device_online() {
  adb devices | awk 'NR>1 && $2=="device"{print $1}' | head -n1
}

start_emulator_if_needed() {
  local dev
  dev="$(any_device_online || true)"
  if [[ -n "$dev" ]]; then
    ok "Device already online: $dev"
    return
  fi

  log "Starting emulator: $avd_name"
  nohup emulator -avd "$avd_name" -no-boot-anim -netdelay none -netspeed full >/dev/null 2>&1 &
  adb wait-for-device
}

wait_for_boot_complete() {
  log "Waiting for Android to finish booting…"
  local boot=""
  until boot="$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')"; [[ "$boot" == "1" ]]; do
    sleep 1
  done
  until adb shell pm list packages >/dev/null 2>&1; do sleep 1; done
  adb shell input keyevent 82 || true
  ok "Boot complete."
}

install_apk() {
  log "Installing APK: $APK"
  adb install -r -t "$APK" >/dev/null
  ok "Installed."
}

# Deterministic launch with Activity Manager (recommended)
launch_with_am() {
  log "Launching with am start: $APP_ID/$MAIN_ACTIVITY"
  if adb shell am start -n "$APP_ID/$MAIN_ACTIVITY" >/dev/null 2>&1; then
    ok "App launched via am."
    return 0
  fi
  return 1
}

# Fallback: force Monkey to do an app-switch as the *first* event
launch_with_monkey_forced() {
  log "Fallback: launching with Monkey (forced app-switch)…"
  # Force 100% app-switch so the very first event is a launch.
  # Monkey often returns non-zero; treat that as a warning, not a hard fail.
  if adb shell monkey -p "$APP_ID" -c android.intent.category.LAUNCHER --pct-appswitch 100 -v 1 >/dev/null 2>&1; then
    ok "Monkey sent app-switch event."
  else
    warn "Monkey returned non-zero (common). If am launch worked, you can ignore this."
  fi
}

launch_app() {
  if launch_with_am; then
    return
  fi
  launch_with_monkey_forced
}

# ---- Run it all ----
adb_start
start_emulator_if_needed
wait_for_boot_complete
install_apk
launch_app

ok "All done 🎉"
