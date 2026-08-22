# 🎵 SONORA LK — Premium Music Discovery & Audio Player

<div align="center">
  <h3>ඔබේ සංගීතය. ඔබේ මොහොත.</h3>
  <p><i>Your Music. Your Moment.</i></p>
</div>

---

## 🌟 Features

- 🎧 **Music Discovery & Search**: Fast search with 400ms debounce, category filtering (Tracks, Artists, Albums), and persistent history.
- 🎨 **Modern Glassmorphic Dark UI**: Custom-built with Jetpack Compose & Material 3, dynamic time-based greetings, and neon purple/pink/blue gradient styling.
- 🔊 **Jetpack Media3 Audio Engine**: Powered by ExoPlayer & `MediaSessionService` for seamless background playback, notification bar controls, and lock-screen media integration.
- 🎚 **Mini-Player & Full Screen Player**: Interactive floating mini-player with swipe-to-skip gestures and expandable full-screen player with seekbar and animated waveform visualizer.
- 📂 **Local Device Music Player**: Automatic scanner for local MP3, M4A, FLAC, and WAV files with offline playback.
- 💾 **Room Local Database**: Persistent offline storage for Favorites, Custom Playlists, and Recently Played history.
- 🇱🇰 **Bilingual Support**: Native Sinhala (`si`) and English (`en`) localization.
- 🚀 **Automated CI/CD**: GitHub Actions workflow generates downloadable debug APK (`app-debug.apk`) on every push.

---

## 🏗 Architecture

```
SONORA LK (Android Client)
  ├── UI: Jetpack Compose + Material 3
  ├── Architecture: MVVM + Clean Architecture (Kotlin Coroutines & Flow)
  ├── Audio Engine: AndroidX Media3 (ExoPlayer + MediaSessionService)
  ├── Database: AndroidX Room (SQLite)
  ├── Networking: Retrofit 2 + OkHttp 3 + Gson
  └── Image Caching: Coil Compose
```

---

## 🛠 Quick Start & Build

### 1. Build Android APK
```bash
# Build Debug APK
./gradlew assembleDebug

# Run Unit Tests
./gradlew testDebugUnitTest
```
The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

### 2. Run Backend Proxy (Optional)
```bash
cd backend
npm install
npm run dev
```

---

## 📄 Documentation

- [Architecture Overview](docs/architecture.md)
- [API Specification](docs/api.md)
- [Build Instructions](docs/build.md)
- [Security Guidelines](docs/security.md)
- [GitHub Actions Workflow](docs/github-actions.md)
- [Legal Disclaimer](docs/legal.md)

---

## ⚖️ License & Disclaimer

This project is licensed under the [MIT License](LICENSE).  
SONORA LK is an independent music player and is not affiliated with or endorsed by Spotify AB.
