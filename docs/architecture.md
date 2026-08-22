# Architecture

```
Android App (Kotlin, Jetpack Compose, Media3, Room)
        │
        ▼
Backend Proxy (Node/Express) — holds CHAMA_API_KEY
        │
        ▼
Third-party metadata API (chama-movie-api)
```

## Layers (Android)
- **ui/** — Compose screens (Home, Search, Player, Library, Settings)
- **data/model/** — internal stable models (TrackModel, ArtistModel, ...)
- **data/remote/** — Retrofit service + DTOs + `ApiMapper` (the only place
  raw API responses become app models, and the only place that decides
  whether audio is safe to expose)
- **data/local/** — Room database: favorites, recently played, playlists,
  local track index, cached metadata for offline fallback
- **data/repository/** — `MusicRepository`, single source of truth for screens
- **player/** — `PlaybackService` (Media3 `MediaSessionService`, drives
  notification/lock-screen/Bluetooth controls) + `PlaybackRepository`
  (app-wide observable playback state)
- **theme/** — centralized design tokens (colors, typography, spacing,
  shapes, animation durations) — no hard-coded values in screens

## Data flow
Search/track requests go App → Backend Proxy → Upstream API → Proxy
(normalizes + strips unsafe fields) → App. The app never talks to the
upstream API directly and never holds its key.

## Licensing gate
`ApiMapper` (client) and `normalizeTrack` (backend) both enforce: a track
only carries a full `audioUrl` / `downloadAllowed = true` when its
`licenseStatus` is `LICENSED` or `USER_OWNED`. Spotify-sourced catalog
tracks are always `PREVIEW_ONLY` — metadata, artwork, and short preview
clips only.
