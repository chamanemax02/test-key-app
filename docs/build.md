# Build

## Android
```
cd android
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

**Gradle wrapper:** not committed to this repo yet. CI generates it on
first run (via `gradle/actions/setup-gradle` + `gradle wrapper`) and
commits it back automatically, so after the first successful Actions run
`./gradlew` will exist in the repo and work locally too. To generate it
yourself instead: install Gradle locally and run
`gradle wrapper --gradle-version 8.7` from `android/`, then commit
`gradlew`, `gradlew.bat`, and `gradle/wrapper/`.

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
