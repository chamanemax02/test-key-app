require('dotenv').config();

const config = {
  port: parseInt(process.env.PORT, 10) || 3000,
  nodeEnv: process.env.NODE_ENV || 'development',
  chamaApiBaseUrl: process.env.CHAMA_API_BASE_URL || 'https://chama-movie-api.koyeb.app/api/v1/spotify',
  chamaApiKey: process.env.CHAMA_API_KEY || '',
  cacheTtl: parseInt(process.env.CACHE_TTL_SECONDS, 10) || 3600,
  rateLimitWindowMs: parseInt(process.env.RATE_LIMIT_WINDOW_MS, 10) || 15 * 60 * 1000,
  rateLimitMax: parseInt(process.env.RATE_LIMIT_MAX, 10) || 300,
};

module.exports = config;
