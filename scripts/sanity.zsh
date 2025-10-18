#!/usr/bin/env zsh
# Sanity checks for Fludde build + ADB readiness (zsh-safe)
# Usage:
#   chmod +x scripts/sanity.zsh
#   zsh ./scripts/sanity.zsh
#
# What this does now:
#   • Verifies Java 17 on PATH
#   • Verifies Gradle wrapper/version
#   • Ensures Android SDK tools on PATH
#   • If no device is online:
#       - picks an AVD (ANDROID_AVD_NAME or first from `emulator -list-avds`)
#       - launches it with sensible flags
#       - waits until sys.boot_completed=1
#   • Prints one-time commands to create an AVD if none exist

# ---------- strict, zsh-friendly ----------
emulate -L zsh
set -o pipefail
setopt err_return
setopt no_unset
setopt no_bg_nice
setopt no_clobber
setopt extended_glob
setopt nonomatch

# ---------- tiny helpers ----------
has_cmd() { command -v -- "$1" >/dev/null 2>&1 }
red()    { print -P "%F{1}$*%f" }
green()  { print -P "%F{2}$*%f" }
yellow() { print -P "%F{3}$*%f" }
gray()   { print -P "%F{8}$*%f" }

print "\n🔎 Running environment sanity checks…\n"

# ---------- Android SDK paths ----------
ensure_android_paths() {
  # If neither ANDROID_SDK_ROOT nor ANDROID_HOME is set, try macOS default
  if ! [[ -v ANDROID_SDK_ROOT || -v ANDROID_HOME ]]; then
    local guess="$HOME/Library/Android/sdk"
    if [[ -d "$guess" ]]; then
      export ANDROID_SDK_ROOT="$guess"
      print "   Using Android SDK at: $ANDROID_SDK_ROOT"
    fi
  fi

  # Pick whichever exists
  local sdk=""
  if [[ -v ANDROID_SDK_ROOT ]]; then
    sdk="$ANDROID_SDK_ROOT"
  elif [[ -v ANDROID_HOME ]]; then
    sdk="$ANDROID_HOME"
  fi

  # Prepend platform-tools & emulator to PATH (avoid duplicates)
  if [[ -n "$sdk" ]]; then
    for p in "$sdk/platform-tools" "$sdk/emulator" "$sdk/tools/bin"; do
      if [[ -d "$p" && ":$PATH:" != *":$p:"* ]]; then
        export PATH="$p:$PATH"
      fi
    done
  fi
}
ensure_android_paths

# ---------- Java 17 check ----------
if has_cmd java; then
  jver="$(java -version 2>&1 | head -n1)"
  print "java -version → $jver"
  if [[ "$jver" == *\"17*\"* ]]; then
    print "$(green "✅ Java 17 is active on PATH")"
  else
    print "$(red "❌ Java 17 not active on PATH")"
    if has_cmd /usr/libexec/java_home; then
      local mac_jdk17=""
      mac_jdk17="$(/usr/libexec/java_home -v 17 2>/dev/null || true)"
      if [[ -n "$mac_jdk17" ]]; then
        print "   Using macOS JDK 17 at: $mac_jdk17"
        print "   Tip: export JAVA_HOME=\"$mac_jdk17\""
      else
        print "   Tip (macOS): brew install openjdk@17 && export JAVA_HOME=\$(/usr/libexec/java_home -v 17)"
      fi
    else
      print "   Tip: install JDK 17 and set JAVA_HOME."
    fi
  fi
else
  print "$(red "❌ 'java' not found on PATH")"
fi
print ""

# ---------- Gradle wrapper check ----------
if [[ -x "./gradlew" ]]; then
  print "$(green "✅ Gradle wrapper exists")"
else
  print "$(red "❌ ./gradlew missing")"
  print "   Run: gradle wrapper --gradle-version 8.9"
fi

if [[ -x "./gradlew" ]]; then
  if ./gradlew --version | grep -q "Gradle 8\.9"; then
    print "$(green "✅ Gradle is 8.9 via wrapper")"
  else
    print "$(yellow "⚠️ Gradle version differs (see gradle/wrapper/gradle-wrapper.properties)")"
  fi
  print "ℹ️ Gradle JVM details:"
  ./gradlew -q --version | sed -n 's/^\(JVM:\|Java home:\)/   \1/p'
fi
print ""

# ---------- adb + device readiness ----------
if ! has_cmd adb; then
  print "$(red "❌ 'adb' not found on PATH")"
  print "   Tip: ensure \$ANDROID_SDK_ROOT/platform-tools is on your PATH."
fi

print "adb path → $(command -v adb 2>/dev/null)"
adb --version | head -n1 | sed 's/^/adb version → /'

# Start or restart server
adb start-server >/dev/null 2>&1 || true

# List devices with state
print "\nConnected Android targets:"
adb devices | awk 'NR==1 || NF==2 {print "  "$0}'

# ---------- helpers ----------
pick_avd() {
  # Priority: ANDROID_AVD_NAME env, else first listed AVD
  if [[ -n "${ANDROID_AVD_NAME:-}" ]]; then
    print -- "$ANDROID_AVD_NAME"
    return 0
  fi
  has_cmd emulator || return 1
  local list; list="$(emulator -list-avds 2>/dev/null | sed '/^[[:space:]]*$/d')"
  [[ -z "$list" ]] && return 1
  # Prefer a Pixel_7_API_* if present; else first
  local p7; p7="$(print -- "$list" | awk '/Pixel_7_API_/ {print; found=1} END{if(!found)exit 1}')"
  if [[ -n "$p7" ]]; then
    print -- "$p7"
  else
    print -- "$list" | head -n1
  fi
}

wait_for_boot() {
  local timeout="${1:-300}"   # seconds
  local start=$SECONDS
  local val=""
  adb wait-for-device >/dev/null 2>&1 || return 1
  adb shell 'echo -n' >/dev/null 2>&1 || true
  while true; do
    val="$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')"
    if [[ "$val" == "1" ]]; then
      return 0
    fi
    if (( SECONDS - start >= timeout )); then
      return 2
    fi
    sleep 1
  done
}

ensure_device() {
  # Is there already a device in 'device' state?
  local target
  target="$(adb devices | awk 'NR>1 && $2=="device"{print $1; exit}')"
  if [[ -n "$target" ]]; then
    print "Using target: $target"
    export ANDROID_SERIAL="$target"
    return 0
  fi

  # None online → try to launch an emulator automatically
  if ! has_cmd emulator; then
    print "$(red "No device currently in 'device' state and 'emulator' tool not found.")"
    print "   Ensure \$ANDROID_SDK_ROOT/emulator is on PATH. See Google docs on running the emulator from the command line."
    print "   Docs: https://developer.android.com/studio/run/emulator-commandline"
    return 1
  fi

  local avd; avd="$(pick_avd || true)"
  if [[ -z "$avd" ]]; then
    print "$(red "No AVDs found."))"
    print "   You can install a system image and create a Pixel 7 AVD via:"
    print "     yes | sdkmanager --licenses"
    print "     sdkmanager --install 'platform-tools' 'emulator' 'system-images;android-35;google_apis;arm64-v8a'"
    print "     avdmanager create avd -n Pixel_7_API_35 -k 'system-images;android-35;google_apis;arm64-v8a' --device 'pixel_7'"
    print "   Docs:"
    print "     sdkmanager: https://developer.android.com/tools/sdkmanager"
    print "     avdmanager: https://developer.android.com/tools/avdmanager"
    return 1
  fi

  print "🚀 Launching Android emulator: $avd"
  # Common flags:
  #   -no-boot-anim : faster boot
  #   -netdelay none -netspeed full : stable/fast network
  #   -no-snapshot-load : avoid stale state
  #   -feature AllowSnapshotSave=off : also avoid saving snapshot on exit
  #   -gpu auto : let emulator pick a sane default
  nohup emulator -avd "$avd" -no-boot-anim -netdelay none -netspeed full \
        -no-snapshot-load -feature AllowSnapshotSave=off -gpu auto \
        >/dev/null 2>&1 &

  print -n "⏳ Waiting for emulator to boot (this can take a couple of minutes)… "
  local rc
  if wait_for_boot 420; then
    print "$(green "OK")"
  else
    rc=$?
    if [[ "$rc" -eq 2 ]]; then
      print "$(red "TIMEOUT")"
    else
      print "$(red "FAILED")"
    fi
    print "   Troubleshooting:"
    print "   • Update platform-tools & emulator: yes | sdkmanager --licenses && sdkmanager --install 'platform-tools' 'emulator'"
    print "   • Kill server: adb kill-server && adb start-server"
    print "   • Cold-boot or wipe AVD data."
    return 1
  fi

  # Re-resolve the now-online target
  target="$(adb devices | awk 'NR>1 && $2=="device"{print $1; exit}')"
  if [[ -n "$target" ]]; then
    print "Using target: $target"
    export ANDROID_SERIAL="$target"
    return 0
  else
    print "$(red "Emulator started but not visible to adb."))"
    return 1
  fi
}

# Try to ensure a device is available (auto-launches emulator if needed)
if ! ensure_device; then
  print "$(yellow "Exiting early (no target).")"
  exit 1
fi

# Quick OS version probe
os="$(adb shell getprop ro.build.version.release 2>/dev/null | tr -d '\r')"
sdk="$(adb shell getprop ro.build.version.sdk 2>/dev/null | tr -d '\r')"

if [[ -n "$os" ]]; then
  print "$(green "✅ adb shell works") — Android $os (API $sdk)"
else
  print "$(yellow "⚠️ Could not read OS version despite boot complete")"
fi

print "\n✅ Sanity checks complete.\n"
