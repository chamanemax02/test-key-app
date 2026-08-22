# Build

## Android
```
cd android
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

**Known gap:** this skeleton does not include the Gradle wrapper binary
(`gradlew`/`gradle-wrapper.jar`) — generate it once locally with
`gradle wrapper --gradle-version 8.7` (requires a local Gradle install)
and commit the result, or CI's `./gradlew` step will fail on a fresh clone.

## Backend
```
cd backend
cp .env.example .env   # fill in CHAMA_API_KEY
npm install
npm start
```

## CI
`.github/workflows/android-build.yml` runs unit tests, builds the debug
APK, and uploads it as a workflow artifact on every push/PR to `main`.
