const config = require('../config/env');
const NodeCache = require('node-cache');

const cache = new NodeCache({ stdTTL: config.cacheTtl });

async function searchTracks(query) {
  const cacheKey = `search_${query.toLowerCase()}`;
  const cached = cache.get(cacheKey);
  if (cached) return cached;

  const url = `${config.chamaApiBaseUrl}/search?q=${encodeURIComponent(query)}&api_key=${config.chamaApiKey}`;
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`Chama API search returned status ${response.status}`);
  }
  const data = await response.json();
  if (data.status && data.result) {
    cache.set(cacheKey, data.result);
    return data.result;
  }
  return [];
}

async function getTrackMetadata(trackUrl) {
  const cacheKey = `track_${trackUrl}`;
  const cached = cache.get(cacheKey);
  if (cached) return cached;

  const url = `${config.chamaApiBaseUrl}/track?url=${encodeURIComponent(trackUrl)}&api_key=${config.chamaApiKey}`;
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`Chama API track metadata returned status ${response.status}`);
  }
  const data = await response.json();
  if (data.status && data.result) {
    cache.set(cacheKey, data.result);
    return data.result;
  }
  return null;
}

async function getDownloadLink(spotifyUrl, quality = '320kbps') {
  const url = `${config.chamaApiBaseUrl}/download?q=${encodeURIComponent(spotifyUrl)}&quality=${quality}&api_key=${config.chamaApiKey}`;
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`Chama API download returned status ${response.status}`);
  }
  const data = await response.json();
  if (data.status && data.result) {
    return data.result;
  }
  return null;
}

module.exports = {
  searchTracks,
  getTrackMetadata,
  getDownloadLink,
};
