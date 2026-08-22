# API Documentation — SONORA LK

## 1. Endpoints

### 1.1 Search Music
- **Endpoint**: `GET /api/v1/spotify/search` (or `/api/search` via proxy)
- **Parameters**:
  - `q` (string, required): Song title or artist name.
  - `api_key` (string, required): API access key.
- **Sample Response**:
```json
{
  "status": true,
  "query": "Shape of You",
  "result": [
    {
      "title": "Ed Sheeran - Shape of You",
      "artist": "Ed Sheeran",
      "duration": "3:53",
      "thumbnail": "https://...",
      "spotify_url": "https://open.spotify.com/track/7qiZfU4dY1lWllzX7mPBI3",
      "video_id": "liTfD88dbCo"
    }
  ]
}
```

### 1.2 Track Details
- **Endpoint**: `GET /api/v1/spotify/track` (or `/api/track` via proxy)
- **Parameters**:
  - `url` (string, required): Spotify track URL.
  - `api_key` (string, required): API access key.

### 1.3 320kbps Audio Download
- **Endpoint**: `GET /api/v1/spotify/download` (or `/api/download` via proxy)
- **Parameters**:
  - `q` (string, required): Spotify track URL.
  - `quality` (string, optional, default: `320kbps`)
  - `api_key` (string, required): API access key.
