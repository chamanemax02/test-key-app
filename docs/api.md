# API

## Backend proxy endpoints (what the Android app calls)
- `GET /v1/search?q={query}` → `{ tracks[], artists[], albums[] }`
- `GET /v1/track?id={id}` → single normalized track

## Environment variables (backend, never on-device)
| Var | Purpose |
|---|---|
| `CHAMA_API_KEY` | Upstream provider credential |
| `API_BASE_URL` | Upstream base URL |
| `API_TIMEOUT` | ms before aborting an upstream call |
| `API_RATE_LIMIT` | requests/min the proxy accepts from clients |
| `API_CACHE_DURATION` | seconds a response is cached in-memory |

## Response normalization
Upstream field names are not guaranteed stable. `routes/spotify.js`
normalizes with null-safe fallbacks (`raw.title ?? raw.name ?? null`, etc.)
before anything reaches the app, and the app's `ApiMapper` re-validates on
the client side as a second null-safety layer.

## Scope limits
- Full-length Spotify audio is never forwarded, downloaded, or cached —
  only `preview_url` (upstream-provided short preview) passes through.
- `downloadAllowed` is hard-set `false` for anything not explicitly
  licensed/user-owned; the client also independently enforces this.
