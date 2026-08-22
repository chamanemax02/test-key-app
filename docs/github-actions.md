# CI/CD & GitHub Actions — SONORA LK

## Pipeline Architecture

The repository includes a GitHub Actions workflow located at `.github/workflows/build-apk.yml`.

### Workflow Stages:
1. **Checkout Code**: Checks out branch repository.
2. **Setup Java & Android SDK**: Configures Eclipse Temurin JDK 17 with Gradle caching.
3. **Run Automated Unit Tests**: Executes `./gradlew testDebugUnitTest`.
4. **Compile & Assemble APK**: Executes `./gradlew assembleDebug`.
5. **Publish Artifact**: Uploads `app-debug.apk` as a downloadable GitHub artifact (`SonoraLK-Debug-APK`).
