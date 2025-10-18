# Fludde – Finished Project Blueprint

A practical, “finished state” plan that fits the current app’s direction (social reviews for movies, books, music) while modernizing the tech where it matters. It assumes you’ll keep Parse/Back4App for v1 (fastest path), with an option to migrate to a custom backend later.

---

## 1) Product vision & scope

**Elevator pitch:** A social platform where people review movies, books, and music; follow friends; and discover content via curated API feeds.

**North-star goals**

* Frictionless posting from suggested content (TMDb, NYT Books, Spotify).
* Clean, modern feed with images, excerpts, and quick actions (like/save/share).
* Reliable search for users and content.
* Smooth onboarding and stable auth.

**MVP → v1.0 feature set**

* Auth (email + password) via Parse.
* Feed (global + following) with posts (category, title, description, review, images).
* Compose from catalog pickers (movies/books/music) + image attach.
* User profiles: avatar, bio, list of posts.
* Search users; (optional) search content.
* Push notifications: likes/comments (v1.1+ if time).
* Privacy & legal: Terms, Privacy Policy, support email.

**Nice-to-haves (v1.x)**

* Save/favorite content; reshare posts.
* Advanced search (by category, title, author/artist).
* Topic/genre tabs, smart recommendations.
* Share deep links.

---

## 2) Ideal finished tech stack

### Mobile app (Android)

* **Language:** Kotlin (migrate gradually from Java).
* **UI:** Jetpack Compose (gradual adoption; legacy XML remains where needed).
* **Architecture:** MVVM + Clean Architecture (presentation/domain/data).
* **DI:** Hilt.
* **Networking:** Retrofit + OkHttp, Moshi (or Kotlinx Serialization).
* **Async:** Kotlin Coroutines + Flow.
* **Image loading:** Coil (Glide acceptable if already entrenched).
* **Local cache:** Room (entities for Post, User, Content stubs).
* **Navigation:** Jetpack Navigation (Compose Destinations if fully Compose).
* **Logging:** Timber.
* **Feature flags / remote config:** Firebase Remote Config (optional).
* **Crash & analytics:** Firebase Crashlytics + Analytics.
* **Testing:** JUnit5 / MockK / Robolectric / Espresso (and Compose UI tests where applicable).
* **Secrets:** gradle properties + encrypted CI secrets.

### Backend & services

* **Primary (v1):** Parse/Back4App (current models already fit: Post, User, Files for images).
* **Media & external APIs:**

  * TMDb (popular, search, details; image base URL handling).
  * NYT Books (lists/history/reviews).
  * Spotify (Client Credentials OAuth; browse/recommendations; album art).
* **Push notifications:** FCM (triggered from Parse Cloud Code or server)
* **(Optional v2)** Custom backend (Node/Nest, Kotlin Ktor, or Go) with Postgres; GraphQL or REST; Redis cache; background workers for API sync.

---

## 3) Data model (Parse v1)

**User**

* id (default), username, email, image (ParseFile), bio (String), followers/following (Relation<User>)

**Post**

* id, user (Pointer<User>)
* category (enum: MOVIE/BOOK/MUSIC)
* contentTitle (String)
* description (String) – short content synopsis/excerpt
* review (String)
* contentImage (ParseFile) – poster/cover
* contentRef (JSON): provider, providerId, extra metadata (e.g., TMDb id)
* likeCount (Number), commentCount (Number) (v1.1)

**Activity/Notification** (v1.1)

* type (LIKE/COMMENT/FOLLOW), actor (User), target (Post/User), createdAt

---

## 4) API integration surface

* **TMDb**: Popular, Search, Details endpoints → `MovieContent` DTO → map to `contentRef` + UI cards; images via `base_url + size + path`.
* **NYT Books**: Lists/History/Reviews → `BooksContent` DTO → map author/title/ISBN; covers from their URLs or via Google Books fallback.
* **Spotify**: Client Credentials → token cache in-memory (with expiry); use Browse (new releases), Recommendations, or Search; `MusicContent` DTO; album art via `images[0].url`.
* **Resilience**: Exponential backoff, offline cache (Room), and request coalescing; show cached content when offline.

---

## 5) App architecture & layers

**Presentation** (Compose + ViewModels)

* Screens: HomeFeed, Compose (tabs: Movies/Books/Music), PostDetails, Search, Profile, Settings.
* UI State modeled as `data class UiState`; render from Flows.

**Domain**

* Use cases: `CreatePost`, `LoadFeed`, `SearchUsers`, `FetchPopularMovies/Books/Music`, `ToggleLike`, `LoadProfilePosts`.

**Data**

* Repositories: `PostRepository`, `UserRepository`, `CatalogRepository` (TMDb/NYT/Spotify), `ImagesRepository`.
* Sources: `Remote` (Parse SDK + Retrofit clients) + `Local` (Room); `NetworkBoundResource` pattern.

---

## 6) Finished repo structure (monorepo)

```text
fludde/
├─ app-android/
│  ├─ build.gradle.kts
│  ├─ gradle.properties
│  ├─ src/
│  │  ├─ main/
│  │  │  ├─ AndroidManifest.xml
│  │  │  ├─ kotlin/
│  │  │  │  ├─ com/fludde/app/
│  │  │  │  │  ├─ FluddeApp.kt
│  │  │  │  │  ├─ di/… (Hilt modules)
│  │  │  │  │  ├─ navigation/…
│  │  │  │  │  ├─ ui/
│  │  │  │  │  │  ├─ screens/
│  │  │  │  │  │  │  ├─ home/
│  │  │  │  │  │  │  ├─ compose/ (movie/book/music pickers)
│  │  │  │  │  │  │  ├─ profile/
│  │  │  │  │  │  │  ├─ search/
│  │  │  │  │  │  │  └─ postdetail/
│  │  │  │  │  │  └─ components/
│  │  │  │  │  ├─ presentation/ (ViewModels)
│  │  │  │  │  ├─ domain/ (UseCases, models)
│  │  │  │  │  └─ data/
│  │  │  │  │     ├─ repo/
│  │  │  │  │     ├─ local/ (Room)
│  │  │  │  │     └─ remote/
│  │  │  │  │        ├─ parse/ (Parse SDK wrappers)
│  │  │  │  │        ├─ tmdb/
│  │  │  │  │        ├─ nyt/
│  │  │  │  │        └─ spotify/
│  │  │  └─ res/ (only for legacy XML; Compose-first)
│  │  └─ test/ …
│  └─ proguard-rules.pro
│
├─ backend/  (optional v2)
│  ├─ api/ (NestJS/Ktor/etc.)
│  ├─ worker/ (scheduled syncs, webhooks)
│  ├─ docker/
│  └─ README.md
│
├─ infra/
│  ├─ terraform/ (Back4App/Firebase/Secrets, if applicable)
│  ├─ github-actions/
│  └─ scripts/
│
├─ docs/
│  ├─ architecture.md
│  ├─ api-integrations.md (TMDb/NYT/Spotify flows)
│  ├─ releases.md (changelog + release steps)
│  └─ privacy-terms/
│
├─ fastlane/ (optional for Play publishing)
├─ .github/workflows/
│  ├─ android-ci.yml (build, test, lint, detekt, assemble)
│  └─ release.yml (signed bundle + upload)
├─ .env.example (non-sensitive defaults)
├─ apikey.properties.example
├─ README.md
└─ LICENSE
```

> If staying with Parse-only: omit `backend/` and keep Cloud Code (if needed) in `cloud/` with hooks and jobs.

---

## 7) CI/CD & quality gates

* **CI:** GitHub Actions → build debug, run unit/UI tests, ktlint, detekt, lint.
* **Signing:** Play App Signing; keystore stored in GitHub Encrypted Secrets or manual local signing for release.
* **Versioning:** SemVer with `versionCode` auto bump on `main`.
* **Artifacts:** Upload `.aab` as CI artifact; attach mapping.txt for Crashlytics.

---

## 8) Security & secrets

* No secrets in VCS.
* Use `apikey.properties` locally; CI injects from GH Secrets → Gradle `BuildConfig` fields.
* Network: certificate pinning (OkHttp) for high‑risk endpoints (optional).
* Privacy: in‑app privacy dialog + settings toggles for analytics.

---

## 9) Testing strategy

* **Unit tests:** repositories, use cases (MockK).
* **Instrumentation/UI:** Espresso + Compose UI tests for top flows (login, create post, open feed).
* **Snapshot testing:** Paparazzi (optional) for Compose components.
* **Contract tests:** API clients against mock server (OkHttp MockWebServer).

---

## 10) Release checklist (v1.0)

1. Core flows green: Login/Signup, Feed, Compose (all 3 catalogs), Profile, Search.
2. Parse schema locked; Cloud Code (if any) deployed.
3. API quotas verified (TMDb/NYT/Spotify), graceful errors when quota/expires.
4. Crash-free sessions > 99.5% (internal/beta).
5. Accessibility pass (TalkBack, contrast, scalable fonts).
6. App size review; shrinker/ProGuard configured.
7. Privacy policy + terms linked in Settings & Play Console.

---

## 11) Migration plan from current codebase

* **Phase 1 (stability):**

  * Replace LoopJ with Retrofit; implement Spotify OAuth; centralize API keys.
  * Fix adapter constructors, add `ListAdapter + DiffUtil`.
  * Align MainActivity nav IDs, bottom nav wiring.
* **Phase 2 (quality):**

  * Introduce Hilt; modularize `data` and `domain` packages.
  * Add Room cache for feed & catalog; implement offline-first for list screens.
  * Add Firebase Crashlytics & Analytics.
* **Phase 3 (modern UI):**

  * Gradual Compose adoption: new screens in Compose; keep XML where stable.
  * Improve accessibility and theming (Material 3).
* **Phase 4 (scale):**

  * Notifications; likes/comments; favorites; deep links; feature flags.
  * Consider custom backend if Parse limits reached.

---

## 12) Definition of Done (for the “finished” repo)

* Clean build on CI with tests & linters passing.
* End-to-end happy paths covered by UI tests.
* Secrets externalized; release task produces signed AAB.
* Docs up to date (architecture, API usage, release steps).
* No hardcoded keys, no dead code, and strict lint warnings resolved.

---

### TL;DR

Keep Parse for speed, modernize the app (Kotlin, Retrofit, Hilt, Room, Compose), fix the API integrations properly (especially Spotify), and lock in CI/CD + testing so the Play Store release is repeatable and safe.
