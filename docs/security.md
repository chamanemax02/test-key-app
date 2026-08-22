# Security Guide — SONORA LK

## 1. Secret & Key Management
- Sensitive keys are never committed directly to public source control.
- In production, keys should be supplied via environment variables (`CHAMA_API_KEY`) or CI secrets (`secrets.ANDROID_KEYSTORE_BASE64`).
- `.env` and local keystores are excluded in `.gitignore`.

## 2. Network Security
- HTTPS is strictly enforced for remote traffic.
- Requests to the proxy server are guarded by Helmet and IP rate-limiting (Express Rate Limit).

## 3. Storage Safety
- Audio files are stored cleanly under standard `Environment.DIRECTORY_MUSIC/SONORA LK/`.
- Sanitized filenames prevent directory traversal or accidental overwrites.
