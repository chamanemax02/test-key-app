const chamaService = require('../services/chamaApi.service');
const { normalizeSearch, normalizeTrack } = require('../utils/normalizer');

async function handleSearch(req, res, next) {
  try {
    const { q } = req.query;
    if (!q || q.trim().length < 2) {
      return res.status(400).json({
        status: false,
        message: 'Query parameter "q" must be at least 2 characters.',
      });
    }

    const rawResults = await chamaService.searchTracks(q.trim());
    const normalized = normalizeSearch(rawResults);

    res.json({
      status: true,
      query: q.trim(),
      count: normalized.length,
      result: normalized,
    });
  } catch (error) {
    next(error);
  }
}

async function handleTrack(req, res, next) {
  try {
    const { url } = req.query;
    if (!url) {
      return res.status(400).json({
        status: false,
        message: 'Query parameter "url" is required.',
      });
    }

    const rawTrack = await chamaService.getTrackMetadata(url.trim());
    if (!rawTrack) {
      return res.status(404).json({
        status: false,
        message: 'Track not found.',
      });
    }

    res.json({
      status: true,
      result: normalizeTrack(rawTrack),
    });
  } catch (error) {
    next(error);
  }
}

async function handleDownload(req, res, next) {
  try {
    const { q, quality = '320kbps' } = req.query;
    if (!q) {
      return res.status(400).json({
        status: false,
        message: 'Query parameter "q" (Spotify URL) is required.',
      });
    }

    const downloadInfo = await chamaService.getDownloadLink(q.trim(), quality);
    if (!downloadInfo || !downloadInfo.download_url) {
      return res.status(404).json({
        status: false,
        message: 'Download stream unavailable for this track.',
      });
    }

    res.json({
      status: true,
      result: {
        id: downloadInfo.id,
        title: downloadInfo.title,
        artist: downloadInfo.artist,
        quality: downloadInfo.quality || quality,
        downloadUrl: downloadInfo.download_url,
        filename: downloadInfo.filename,
      },
    });
  } catch (error) {
    next(error);
  }
}

async function handleRecommendations(req, res, next) {
  try {
    const defaultQueries = ['Trending Sri Lanka', 'Baila Hits', 'Sinhala Acoustic'];
    const randomQuery = defaultQueries[Math.floor(Math.random() * defaultQueries.length)];
    const rawResults = await chamaService.searchTracks(randomQuery);
    const normalized = normalizeSearch(rawResults);

    res.json({
      status: true,
      category: randomQuery,
      result: normalized,
    });
  } catch (error) {
    next(error);
  }
}

module.exports = {
  handleSearch,
  handleTrack,
  handleDownload,
  handleRecommendations,
};
