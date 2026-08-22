function normalizeTrack(raw) {
  if (!raw) return null;
  return {
    id: raw.id || raw.video_id || (raw.title ? Buffer.from(raw.title).toString('base64').slice(0, 16) : 'unknown'),
    title: raw.title ? raw.title.trim() : 'Unknown Title',
    artist: raw.artist ? raw.artist.trim() : 'Unknown Artist',
    album: raw.album ? raw.album.trim() : '',
    duration: raw.duration || '0:00',
    durationMs: raw.duration_ms || 0,
    releaseDate: raw.release_date || '',
    thumbnail: raw.thumbnail || '',
    spotifyUrl: raw.spotify_url || '',
    previewUrl: raw.preview_url || '',
    downloadAllowed: true,
  };
}

function normalizeSearch(rawList) {
  if (!Array.isArray(rawList)) return [];
  return rawList.map(item => normalizeTrack(item)).filter(Boolean);
}

module.exports = {
  normalizeTrack,
  normalizeSearch,
};
