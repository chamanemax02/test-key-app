# Architecture Overview — SONORA LK

## 1. High-Level Architecture

SONORA LK is structured following Google's modern Android Clean Architecture and MVVM (Model-View-ViewModel) pattern, with Jetpack Compose as the modern reactive UI framework.

```
UI Layer (Jetpack Compose Screens & Components)
   │
   ▼
ViewModel Layer (HomeViewModel, SearchViewModel, PlayerViewModel, LibraryViewModel)
   │
   ▼
Repository Layer (MusicRepository, LocalMusicRepository, PlaylistRepository)
   │
   ├────────► Local Data Source (Room Database: SQLite)
   │
   ├────────► System Media Store (MediaStore Audio Scanner)
   │
   └────────► Remote Data Source (Retrofit API Service / Backend Proxy)
```

## 2. Audio Playback Subsystem

- **Jetpack Media3 ExoPlayer**: Handles audio streaming, local file playback, and buffer management.
- **SonoraMediaService**: Extends `MediaSessionService`, keeping audio playback alive in the background with persistent notification controls (Android Lock Screen, Bluetooth, Notification bar).
- **MusicPlayerManager**: Singleton state manager exposing `playbackState` as a Kotlin `StateFlow`.

## 3. Storage & Caching Layer

- **Room Database (`sonora_lk.db`)**: Stores favorites, user playlists, recently played tracks (max 100 entries), queue state, and cached track metadata.
- **MediaStore Scanner**: Directly reads and indexes device-stored `.mp3`, `.m4a`, `.flac`, and `.wav` files with zero network dependency.
