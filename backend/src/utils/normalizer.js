function normalizeTrack(raw) {
  if (!raw) return null;
  const id = raw.video_id || raw.id || (raw.title ? Buffer.from(raw.title).toString('base64').slice(0, 16) : 'unknown');
  return {
    id: id,
    title: raw.title ? raw.title.trim() : 'Unknown Title',
    artist: raw.uploader || raw.artist ? (raw.uploader || raw.artist).trim() : 'Unknown Artist',
    album: raw.album ? raw.album.trim() : 'YouTube Music',
    duration: typeof raw.duration === 'number' ? `${Math.floor(raw.duration / 60)}:${String(raw.duration % 60).padStart(2, '0')}` : (raw.duration || '0:00'),
    durationMs: typeof raw.duration === 'number' ? raw.duration * 1000 : (raw.duration_ms || 0),
    thumbnail: raw.thumbnail || `https://i.ytimg.com/vi/${id}/hqdefault.jpg`,
    youtubeUrl: raw.youtube_url || `https://www.youtube.com/watch?v=${id}`,
    spotifyUrl: raw.spotify_url || '',
    directUrl: raw.direct_url || raw.download_url || '',
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
