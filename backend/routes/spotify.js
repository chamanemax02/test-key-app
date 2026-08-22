const express = require("express");
const fetch = require("node-fetch");
const NodeCache = require("node-cache");

const router = express.Router();
const cache = new NodeCache({ stdTTL: Number(process.env.API_CACHE_DURATION || 300) });

const UPSTREAM_BASE = process.env.API_BASE_URL;
const API_KEY = process.env.CHAMA_API_KEY;
const TIMEOUT_MS = Number(process.env.API_TIMEOUT || 8000);

/**
 * Every route here does three things the Android app must never do itself:
 *  1. Attach the upstream API key (server-side only — never shipped in the APK)
 *  2. Cache responses briefly to cut redundant upstream calls
 *  3. Strip the response down to fields the app actually needs, so an
 *     upstream schema change doesn't propagate straight into the client
 */

async function fetchUpstream(path, params) {
  const url = new URL(`${UPSTREAM_BASE}${path}`);
  Object.entries(params).forEach(([k, v]) => v != null && url.searchParams.set(k, v));

  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), TIMEOUT_MS);
  try {
    const res = await fetch(url, {
      headers: { Authorization: `Bearer ${API_KEY}` },
      signal: controller.signal,
    });
    if (!res.ok) throw new Error(`Upstream ${res.status}`);
    return await res.json();
  } finally {
    clearTimeout(timeout);
  }
}

router.get("/search", async (req, res) => {
  const q = req.query.q;
  if (!q) return res.status(400).json({ error: "Missing q" });

  const cacheKey = `search:${q}`;
  const cached = cache.get(cacheKey);
  if (cached) return res.json(cached);

  try {
    const upstream = await fetchUpstream("/spotify/search", { q });
    // Normalize to the shape SonoraApiService/ApiMapper expects on the client.
    // NOTE: only preview-length audio (if any) is ever forwarded — full
    // track URLs from the upstream provider are intentionally dropped here
    // unless a separate licensing check marks the item as user-owned/licensed.
    const normalized = normalizeSearch(upstream);
    cache.set(cacheKey, normalized);
    res.json(normalized);
  } catch (e) {
    res.status(502).json({ error: "Upstream unavailable", detail: e.message });
  }
});

router.get("/track", async (req, res) => {
  try {
    const upstream = await fetchUpstream("/spotify/track", { id: req.query.id });
    res.json(normalizeTrack(upstream));
  } catch (e) {
    res.status(502).json({ error: "Upstream unavailable", detail: e.message });
  }
});

function normalizeTrack(raw) {
  if (!raw) return null;
  return {
    id: raw.id ?? raw.track_id ?? null,
    name: raw.title ?? raw.name ?? null,
    artist: raw.artist ?? raw.artists?.[0]?.name ?? null,
    artistId: raw.artist_id ?? null,
    album: raw.album ?? raw.album_name ?? null,
    albumId: raw.album_id ?? null,
    image: raw.image ?? raw.album_art ?? null,
    durationMs: raw.duration_ms ?? null,
    externalUrl: raw.spotify_url ?? raw.external_url ?? null,
    // Only ever pass through a genuine short preview clip.
    previewUrl: raw.preview_url ?? null,
    releaseDate: raw.release_date ?? null,
    explicit: raw.explicit ?? false,
    // downloadAllowed/licenseStatus are decided by OUR licensing rules,
    // never taken as-is from the upstream payload.
    downloadAllowed: false,
    licenseStatus: "preview_only",
    audioUrl: null,
  };
}

function normalizeSearch(raw) {
  return {
    tracks: (raw?.tracks ?? []).map(normalizeTrack).filter(Boolean),
    artists: (raw?.artists ?? []).map((a) => ({
      id: a.id ?? null,
      name: a.name ?? null,
      image: a.image ?? null,
      genres: a.genres ?? [],
      externalUrl: a.spotify_url ?? null,
    })),
    albums: (raw?.albums ?? []).map((a) => ({
      id: a.id ?? null,
      name: a.name ?? null,
      artist: a.artist ?? null,
      image: a.image ?? null,
      releaseDate: a.release_date ?? null,
      trackCount: a.track_count ?? null,
      externalUrl: a.spotify_url ?? null,
    })),
  };
}

module.exports = router;
