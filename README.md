# SONORA LK

Premium, Spotify-inspired music discovery + personal/local music player for Android.
Original branding and UI — not a Spotify clone.

## Status: project skeleton
This is Phase 1–4 scaffolding across every architectural layer (per the
90-section spec), built as real, compilable-shaped Kotlin/Compose + Node
source — not a mockup. It is **not** a finished, tested, CI-green app yet.

## What's here
- `android/` — Kotlin + Jetpack Compose app: theme system, navigation,
  Home/Search/Player screens, Room database (favorites, playlists, recently
  played, local music), Media3 background-playback service, Retrofit API
  layer with a null-safe mapper and a hard license gate.
- `backend/` — Express proxy that holds the upstream API key server-side
  and normalizes responses before they reach the app.
- `.github/workflows/android-build.yml` — CI: test → build debug APK → upload artifact.
- `docs/` — architecture, api, build, security, github-actions, legal.

## What's not done yet (see docs/build.md)
- Gradle wrapper binary isn't committed — generate once with `gradle wrapper`.
- Library, Settings, Track/Artist/Album detail, Downloads, About screens are
  stubbed as navigation routes but not yet built out.
- Local file import (MediaStore scan) and the download manager are not implemented.
- Nothing has been compiled/run in this environment — no Android SDK or
  network access here, so CI hasn't actually been executed yet.

## Legal
See `docs/legal.md`. Full Spotify catalog audio is never streamed,
downloaded, or extracted — only metadata, artwork, and short previews.
