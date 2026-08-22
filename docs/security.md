# Security

- The upstream API key (`CHAMA_API_KEY`) lives only in the backend's
  environment (`.env`, or the hosting platform's secret store). It is never
  referenced in Android source, `BuildConfig`, resources, or the compiled APK.
- The Android app's only configured endpoint is our own backend
  (`BuildConfig.API_BASE_URL`); there is no code path that calls the
  upstream provider directly.
- Backend applies `helmet` (secure headers), CORS, and per-IP rate limiting
  in front of the upstream call.
- No DRM bypass, stream-ripping, or full-track extraction logic exists
  anywhere in this codebase — enforced redundantly at both the backend
  normalizer and the client-side `ApiMapper`.
