# Fludde — Zero‑to‑Working App (macOS Terminal + VS Code)

**Scope:** Start from the current repo state (based on the Repomix/XML + OUTLINE) and get to a **debug‑installable Android app** on an **emulator or device** using **macOS Terminal** + **VS Code**. No application source code is shown; you’ll get **commands**, **file responsibilities**, decision checklists, and best‑practice notes.

> **You asked for commands but no code.** All blocks below are **terminal commands**, configuration checklists, or prose—not app source.

---

## 0) TL;DR (15‑minute quick path)

1. **Install toolchain** (JDK 17, Android CLI, licenses).

```bash
brew install openjdk@17 && export JAVA_HOME=$(/usr/libexec/java_home -v 17)
brew install --cask android-commandlinetools
yes | sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-35" "build-tools;35.0.0" "emulator" "cmdline-tools;latest"
```

2. **Create & run emulator (Apple Silicon)**

```bash
sdkmanager "system-images;android-35;google_apis;arm64-v8a"
avdmanager create avd -n Pixel_7_API_35 -k "system-images;android-35;google_apis;arm64-v8a" -d pixel_7
emulator -avd Pixel_7_API_35 &
```

3. **Clone + wrapper + secrets**

```bash
cd ~/dev && git clone <your-repo-url> fludde && cd fludde
gradle wrapper --gradle-version 8.9
: > apikey.properties && open -a "Visual Studio Code" apikey.properties
```

4. **Add keys in **``** (names only):** `NY_TIMES_API_KEY`, `SPOTIFY_KEY`, `TMDB_API_KEY`.
5. **Build + install**

```bash
./gradlew clean :app:assembleDebug :app:installDebug
adb shell monkey -p com.example.fludde -c android.intent.category.LAUNCHER 1
```

If it fails, jump to **Section H** to reconcile IDs/adapters and re‑run Step 5.

---

## A. Snapshot of the current repo & quick triage

**What you have (high‑level):**

* Android app module `app/` in Java, Fragments + RecyclerViews, Parse (Back4App) integration.
* Network via **LoopJ AsyncHttpClient** wrapped by `ApiUtils`.
* XML UI (no Compose yet), Glide, BottomNavigation, fragments for Home, Post, Profile, Search, and compose pickers (movies/books/music).
* API integrations intended for **TMDb**, **NYTimes Books**, **Spotify**. Keys are partially routed through `BuildConfig` but also **hard‑coded in **`` (must be removed/rotated).
* **Two top‑level Gradle files** (`/build.gradle` and `app/build.gradle`) and a `/config/` folder (potential drift/duplication).

**Build blockers to resolve first (prose only, do in order):**

1. **Gradle wrapper** absent/ignored → create local wrapper; always build with `./gradlew`.
2. **AGP/Gradle/JDK alignment** → use **JDK 17**, modern AGP 8.x pair, Gradle 8.9 wrapper.
3. **Secrets in VCS** → rotate Back4App keys; move all real secrets to `apikey.properties` and generate BuildConfig fields.
4. **Adapters vs Fragments mismatch** → pick one adapter contract (constructor‑data **or** setter methods) and make all child fragments follow it.
5. **Navigation IDs mismatch** → realign `MainActivity` IDs with `activity_main.xml` and `menu_bottom_navigation.xml`.
6. **Deprecated storage permissions** → note for pre‑release; don’t block v0 build.
7. **BuildConfig fields** → ensure the Gradle file actually emits `BuildConfig.NY_TIMES_API_KEY`, `BuildConfig.SPOTIFY_KEY`, `BuildConfig.TMDB_API_KEY`.

> **Strategy:** First achieve a **clean debug build + install** (Sections B→G). Next, fix structural mismatches (Section H). Then stabilize network/auth (Section I). Finally harden, document, and modernize (Sections K→Q).

---

## B. macOS prerequisites (Apple Silicon or Intel)

**1) Homebrew** (skip if installed)

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

**2) Java 17 (Temurin/OpenJDK)**

```bash
brew install openjdk@17
/usr/libexec/java_home -V
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
java -version
```

**3) Android command‑line tools**

```bash
brew install --cask android-commandlinetools
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools:$PATH"
```

**4) Accept licenses & install packages**

```bash
yes | sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-35" "build-tools;35.0.0" "emulator" \
           "cmdline-tools;latest" "extras;google;google_play_services"
```

**5) Create an emulator**

```bash
sdkmanager "system-images;android-35;google_apis;arm64-v8a"
avdmanager create avd -n Pixel_7_API_35 -k "system-images;android-35;google_apis;arm64-v8a" -d pixel_7
emulator -avd Pixel_7_API_35 &
```

*(On Intel, use the **`x86_64`** system image.)*

**6) VS Code setup**

* Extensions: **Gradle for Java**, **Extension Pack for Java**, Android community extension, **XML**, **YAML**.
* Settings: enable format‑on‑save; set Java 17 runtime for the workspace.

**7) Optional productivity aliases (add to **``**)**

```bash
alias adevice='adb devices'
alias alog='adb logcat | grep -i fludde'
alias abuild='./gradlew :app:assembleDebug'
alias ainstall='./gradlew :app:installDebug'
```

Reload shell:

```bash
source ~/.zshrc
```

---

## C. Clone, open, and repo hygiene

```bash
cd ~/dev
git clone <your-repo-url> fludde
code fludde
```

**Hygiene checklist before first build:**

* Keep **one** `settings.gradle` (root). Remove or merge `/config/settings.gradle`.
* Keep **one** `gradle.properties` (root). Merge AndroidX flags and JVM args from `/config/gradle.properties`.
* Ensure `settings.gradle` includes `:app` and uses `google()` + `mavenCentral()`.
* Confirm `local.properties` is **git‑ignored** and points to your SDK if present.

**Branching model (recommended):**

```bash
git checkout -b chore/boot-build
```

Work on this branch until you achieve a green build; merge to `main` via PR.

---

## D. Create a local Gradle Wrapper & pin toolchain

**1) Install Gradle (one‑time) and generate wrapper**

```bash
brew install gradle
cd fludde
gradle wrapper --gradle-version 8.9
./gradlew -v
```

**2) Verify Java & AGP alignment**

* JDK = 17 (`java -version`).
* AGP = 8.x (set via build file plugins block later).
* Gradle = 8.9 (wrapper).

**3) Clean caches if you hit weird sync issues**

```bash
rm -rf ~/.gradle/caches/build-cache-*
```

---

## E. Secrets: remove from VCS; set up `apikey.properties`

**1) Create local secrets file**

```bash
: > apikey.properties
open -a "Visual Studio Code" apikey.properties
```

**2) Add the keys (names only here):**

* `NY_TIMES_API_KEY`
* `SPOTIFY_KEY`
* `TMDB_API_KEY` **3) Rotate & scrub committed secrets**
* In Back4App, **rotate** Application ID/Client Key if they were exposed.
* Replace `strings.xml` values with placeholders (later), and rely on **BuildConfig** + `apikey.properties`. **4) Confirm **``

```bash
grep -n "apikey.properties" .gitignore
```

If missing, add a line for it and for any `*.bak` key files.

**5) Sanity‑check BuildConfig emission** (later during build): ensure `BuildConfig.NY_TIMES_API_KEY`, etc., are present in debug builds.

---

## F. Build file reconciliation (one‑time tidy‑up)

> You’ll edit files in VS Code (no source pasted here). Use this checklist to know **what belongs where**.

**Root **``

* Repositories: `google()`, `mavenCentral()` (and `maven { url "https://jitpack.io" }` only if needed).
* Project name should be `Fludde` and `include(":app")`.

**Root **``

* Plugins block should apply the Android Gradle Plugin **8.x**.
* No legacy `buildscript`/`allprojects` blocks; rely on pluginManagement in settings if needed.

**Module **``

* `compileSdk` and `targetSdk` = **35**. `minSdk` can remain **21** for now.
* Java source/target = 1.8 compatibility; JDK 17 toolchain is fine.
* Dependencies: keep appcompat/material/constraintlayout/Glide/Parse, etc. Update versions **after** you get a green build.
* **BuildConfig** fields: read from `apikey.properties` so `BuildConfig.<KEY>` exists.

``

* Keep `android.useAndroidX=true`, `android.enableJetifier=true`, `android.nonTransitiveRClass=true`.
* Set `org.gradle.jvmargs=-Xmx3g -Dfile.encoding=UTF-8` if builds run out of memory.

**After edits:** reload Gradle in VS Code and proceed.

---

## G. Emulator/device bring‑up and first build

**1) Verify emulator**

```bash
adb devices
```

**2) Clean + assemble**

```bash
./gradlew clean :app:assembleDebug
```

**3) Install**

```bash
./gradlew :app:installDebug
# or
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

**4) Launch & logs**

```bash
adb shell monkey -p com.example.fludde -c android.intent.category.LAUNCHER 1
adb logcat | grep -i fludde
```

**5) If install fails with signature conflicts (older debug APK on device):**

```bash
adb uninstall com.example.fludde || true
./gradlew :app:installDebug
```

**6) Basic runtime smoke**

* Open app, move through bottom tabs, trigger networked lists (Movies/Books/Music) and the post feed.
* Watch `adb logcat` for crashes, `NetworkOnMainThreadException`, or HTTP 401/404.

**Emulator performance tips (optional):**

* Increase RAM in AVD settings to 2–3 GB.
* Enable hardware acceleration (default on Apple Silicon).
* Disable animations for faster UI traversal:

```bash
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0
```

---

## H. Fix compile‑time mismatches (make the project consistent)

> Edit, rebuild, repeat. Do **not** paste source here—use the responsibilities below as your target state.

### 1) Navigation & Activity vs Layout

* Ensure `MainActivity` references IDs that exist in `activity_main.xml` (bottom navigation view + fragment container). Use **the same IDs** across both files.
* Ensure `menu_bottom_navigation.xml` item IDs match what `MainActivity` handles (Home, Feed/Post, Compose, Search, Profile). Titles/icons can remain.

### 2) Adapter–Fragment contract (choose one approach)

* **Approach A (constructor data):** Adapters accept `(Context, List<...>, Listener)`; fragments own the list and call `notify...` after changing the list.
* **Approach B (setter API):** Adapters expose `setItems(...)` (or type‑specific setters); fragments pass in new lists only through setters. Apply the **same** approach to `MovieChildAdapter`, `BookChildAdapter`, `MusicChildAdapter`. Remove any stray calls that don’t match the chosen contract.

### 3) `ApiUtils` role

* Centralize headers (e.g., `Authorization: Bearer ...`), user agent, timeouts, and error logging.
* Keep it as a thin façade for v0; plan Retrofit/OkHttp migration post‑bring‑up.

### 4) Parse initialization

* Initialize **once** in the `Application` class.
* Ensure Parse subclasses (`Post`, `User`) are registered here only.
* Move real keys **out** of `strings.xml`; leave placeholders only.

### 5) Search users

* Fragment handles user input, fires a Parse query, and hands results to the adapter.
* Adapter binds `ParseUser` fields (username, optional image) only.

### 6) Home & Post feed

* `PostFragment` queries Parse (`include user`, sensible limit) and forwards `List<Post>` to `PostAdapter`.
* `HomeFragment` should not reference non‑existent adapter methods; either remove it for v0 or align it to serve a curated/suggested view later.

### 7) Permissions & FileProvider

* Keep only permissions you actually use now (e.g., `INTERNET`, `ACCESS_NETWORK_STATE`).
* Retain `FileProvider` only if you capture/attach photos; otherwise trim it later.

### 8) Resource hygiene

* Every Java/Kotlin ID must exist in XML.
* Remove duplicate, unused, or conflicting resources to prevent R‑class issues.

**Rebuild:**

```bash
./gradlew :app:assembleDebug :app:installDebug
```

---

## I. Network/API flows (v0 best practices, no source)

### 1) TMDb (Movies)

* Auth with your **API key** via `BuildConfig` (query param or header as TMDb requires).
* Images: compose URLs using the base URL + size (e.g., `w342`) + poster/backdrop path. Use placeholders if a path is missing.
* Handle errors: show cached data or empty state; log status codes and body size (not full payloads) for privacy.

### 2) NYTimes Books (Books)

* Use the **Books** endpoints with your key from `BuildConfig`.
* Covers: if endpoint lacks images, fall back to **ISBN‑based** image URL pattern. Cache cover URLs once resolved.
* Handle quotas: respect Retry‑After headers; exponential backoff on 429/5xx.

### 3) Spotify (Music)

* Use **Client Credentials**: get app token (no user login) and store expiry time.
* On 401, refresh token automatically; never log tokens.
* Limit scopes to what you need; do not request user scopes for v0.

**Where to put auth headers:** `ApiUtils` adds `Authorization: Bearer <token>` for Spotify calls; TMDb/NYT keys remain centralized too.

**Optional validation via CLI** (sanity tests before wiring into the app):

```bash
# Replace placeholders with real values
curl -s "https://api.themoviedb.org/3/movie/popular?api_key=$TMDB_API_KEY" | head -c 300
curl -s "https://api.nytimes.com/svc/books/v3/lists/current/hardcover-fiction.json?api-key=$NY_TIMES_API_KEY" | head -c 300
# Spotify requires a token; fetch token with Client Credentials on your backend or a local helper script (do not commit).
```

---

## J. Logging, debugging, and observability (developer loop)

**Logcat filters:**

```bash
adb logcat | egrep -i "Fludde|Parse|Glide|HTTP|Network"
```

**Record a repro trace (timeout or crash):**

```bash
adb bugreport ~/Desktop/bugreport-$(date +%s).zip
```

**Network inspection (optional):**

* Use **Android Studio Network Inspector** (if you open the project there) or run via **mitmproxy/Charles** and configure the emulator proxy. Do not capture tokens in screenshots.

**StrictMode (optional later):** enable to detect disk/net on main thread—fix any violations before release.

---

## K. Testing strategy you can run today (no source)

**Unit tests (hosted JVM):**

```bash
./gradlew testDebug --no-daemon
```

**Instrumented UI tests (emulator):**

```bash
./gradlew connectedDebugAndroidTest --no-daemon
```

**Android Lint:**

```bash
./gradlew :app:lintDebug
```

**Manual QA smoke:**

* Login → Feed → Compose (switch Movies/Books/Music) → Search user.
* Rotate device; verify no crashes and that lists restore correctly.
* Offline mode test: disable network and ensure the app fails gracefully (toasts/empty states).

**Test data seeding (Parse Dashboard or CLI):**

* Create a few `Post` entries tied to a test `User`.
* Upload at least one image file to validate Glide and File pointers.

---

## L. Hardening checklist before you share APKs/AABs

* **Secrets**: rotate any exposed keys; keep live keys out of repo and CI logs.
* **Target/compile SDK 35**; verify permission changes (modern media permissions instead of legacy storage).
* **Network hygiene**: timeouts, basic retry/backoff for catalog fetches; user‑visible errors already exist as toasts—ensure they’re consistent.
* **Crash/analytics (optional now)**: plan Crashlytics/Analytics once v0 is stable.
* **Privacy**: link Privacy Policy in Settings and Play Console; document what you collect (Parse user data, posts, images).
* **App size**: enable shrinking/obfuscation for release builds; keep mapping files for crash deobfuscation.

---

## M. File responsibilities (authoritative)

* `` — Package name, permissions, `Application` class, main Activity, FileProvider, Parse metadata placeholders. No live keys.
* `` — Initialize Parse once; register `Post` and `User` subclasses. No UI logic.
* `` — Own bottom navigation and fragment container; swap top‑level fragments on selection.
* **Fragments** —

  * `HomeFragment` — Optional curated/suggested feed (can be deferred or removed for v0).
  * `PostFragment` — Query Parse posts and bind via `PostAdapter` (include `user` pointer).
  * `ComposeParentFragment` — Hosts spinner (Movies/Books/Music), swaps to child fragments.
  * `SearchFragment` — Parse user search; forwards results to `SearchFragmentAdapter`.
  * `ProfileFragment` — Current user info now; later, user’s posts.
  * `fragments/child/*` — Fetch popular lists (TMDb/NYT/Spotify), display horizontally, surface selections back up.
* `` — Bind only. No network. Expose click listeners to fragments.
* `` — Parse models (`Post`, `User`) + DTOs (`MovieContent`, `BooksContent`, `MusicContent`). No Android views here.
* `` — Centralize HTTP headers, timeouts, basic logging, and error handling for v0.
* `` — Activity/fragment/item layouts; IDs must match code.
* `` — Item IDs must match `MainActivity` handling.
* **Root & module Gradle files** — Define plugins/config; generate BuildConfig fields from `apikey.properties`.
* `` — AndroidX flags, JVM args.
* `` — Repositories and `:app` include only.

---

## N. Security & compliance notes

* **Do not log PII** (emails, usernames beyond what’s shown in UI); redact identifiers in logs.
* **Token handling**: Spotify tokens expire; store in memory and refresh. Never persist app tokens in files.
* **Certificate pinning (optional later)**: if you add sensitive endpoints, consider OkHttp pinning post‑Retrofit migration.
* **3rd‑party SDKs**: maintain a list (Parse, Glide, MLKit common, etc.) and review their data collection statements.

---

## O. Developer ergonomics on macOS

**Clean build loop**

```bash
emulator -avd Pixel_7_API_35 &
./gradlew :app:installDebug
adb logcat | grep -i fludde
```

**Profile build times**

```bash
./gradlew :app:assembleDebug --profile
open build/reports/profile
```

**Clear app data on emulator**

```bash
adb shell pm clear com.example.fludde
```

**Export an APK for QA**

```bash
./gradlew :app:assembleDebug
open app/build/outputs/apk/debug
```

---

## P. Release hygiene (when you’re ready)

**Signing (debug vs release):**

* Keep a local, uncommitted release keystore; store credentials in a secure vault.
* Use Play App Signing for production; upload an AAB when the time comes.

**Versioning:**

* Adopt SemVer for `versionName`; auto‑bump `versionCode` in CI (later).

**Artifacts:**

```bash
./gradlew :app:bundleRelease
open app/build/outputs/bundle/release
```

**Pre‑publish checks:**

* Lint passes; no `NetworkOnMainThreadException` in logs.
* Target API 35, privacy policy linked, data safety form ready.

---

## Q. Modernization roadmap (from OUTLINE, staged for after v0)

1. **Stability (Phase 1)** — Swap LoopJ → Retrofit/OkHttp; align adapters with `ListAdapter + DiffUtil`; unify nav IDs; centralize keys; clean error UI.
2. **Quality (Phase 2)** — Introduce Hilt DI; refactor into presentation/domain/data; Room cache for feeds/catalog; Crashlytics/Analytics; static analysis (Checkstyle/SpotBugs) and later Kotlin + ktlint.
3. **UI (Phase 3)** — Compose for new screens; Material 3 theming; accessibility sweep (TalkBack, contrast, font scaling).
4. **Scale (Phase 4)** — Likes/comments/notifications; deep links; feature flags; optional custom backend if Parse limits are hit.

---

## R. Troubleshooting appendix

**AGP/Gradle mismatch** — Ensure wrapper (8.9), AGP (8.x), JDK (17). Re‑generate wrapper if needed. ``** is null** — Confirm `apikey.properties` exists and Gradle reads it into `buildConfigField`s. **HTTP 401 (Spotify)** — Token expired; refresh using Client Credentials flow; ensure header applied to every request. **TMDb images 404** — Double‑check base URL + size + path and ensure the path includes a leading slash. **NYT covers missing** — Use ISBN fallback; cache resolved URLs. **Startup crash** — Verify `Parse.initialize(...)` in `Application` only; confirm server URL and keys. **Install fails** — Uninstall previous debug build; clear package data; retry.

---

## S. Daily workflow (VS Code + Terminal)

```bash
# 1) Start emulator once (per session)
emulator -avd Pixel_7_API_35 &

# 2) Build & install quickly
./gradlew :app:installDebug

# 3) Watch logs
adb logcat | grep -i fludde

# 4) Run tests
./gradlew testDebug connectedDebugAndroidTest

# 5) Lint & reports
./gradlew :app:lintDebug
```

You’re set. March through Sections H → G until the build is green, then iterate with Q for modernization. If anything in your repo differs, point me to the exact file paths and I’ll retune this guide.
