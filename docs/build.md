# Build & Run Guide — SONORA LK

## Prerequisites
- **JDK 17 or 21**
- **Android SDK (API 34)**
- **Node.js v18+** (for proxy server, optional)

## 1. Building the Android App

### Debug APK Build:
```bash
./gradlew assembleDebug
```
Output APK location:
`app/build/outputs/apk/debug/app-debug.apk`

### Running Unit Tests:
```bash
./gradlew testDebugUnitTest
```

## 2. Running the Node.js Proxy (Optional)
```bash
cd backend
npm install
npm run start
```
Default proxy runs on `http://localhost:3000`.
