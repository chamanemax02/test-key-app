const config = require('../config/env');
const NodeCache = require('node-cache');

const cache = new NodeCache({ stdTTL: config.cacheTtl });

async function searchYouTube(query) {
  const cacheKey = `yt_search_${query.toLowerCase()}`;
  const cached = cache.get(cacheKey);
  if (cached) return cached;

  const url = `https://chama-movie-api.koyeb.app/api/v1/youtube/search?q=${encodeURIComponent(query)}&api_key=${config.chamaApiKey}`;
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`YouTube API search returned status ${response.status}`);
  }
  const data = await response.json();
  if (data.status && (data.data || data.result)) {
    const list = data.data || data.result;
    cache.set(cacheKey, list);
    return list;
  }
  return [];
}

async function getYouTubeAudioDownload(youtubeUrl) {
  const url = `https://chama-movie-api.koyeb.app/api/v1/youtube/download?url=${encodeURIComponent(youtubeUrl)}&api_key=${config.chamaApiKey}`;
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`YouTube download stream returned status ${response.status}`);
  }
  const data = await response.json();
  if (data.status && (data.data || data.result)) {
    return data.data || data.result;
  }
  return null;
}

async function searchTracks(query) {
  return searchYouTube(query);
}

module.exports = {
  searchYouTube,
  getYouTubeAudioDownload,
  searchTracks,
};
