# GitHub Actions

Workflow: `.github/workflows/android-build.yml`
Triggers: push/PR to `main`, or manual `workflow_dispatch`.
Steps: checkout → JDK 17 → `./gradlew testDebugUnitTest` → `./gradlew assembleDebug` → upload `app-debug.apk` as an artifact.

No secrets are required for a debug build. A release/signed build would
add a signing step reading the keystore + passwords from repo secrets
(not included in this skeleton — flag if you want it added).
