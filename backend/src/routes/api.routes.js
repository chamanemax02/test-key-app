const express = require('express');
const {
  handleSearch,
  handleTrack,
  handleDownload,
  handleRecommendations,
} = require('../controllers/spotify.controller');

const router = express.Router();

router.get('/search', handleSearch);
router.get('/track', handleTrack);
router.get('/download', handleDownload);
router.get('/recommendations', handleRecommendations);

router.get('/health', (req, res) => {
  res.json({
    status: true,
    service: 'SONORA LK Backend Proxy',
    timestamp: new Date().toISOString(),
    uptime: process.uptime(),
  });
});

router.get('/config', (req, res) => {
  res.json({
    status: true,
    appName: 'SONORA LK',
    version: '1.0.0',
    features: {
      search: true,
      streaming: true,
      downloads: true,
      localAudio: true,
    },
  });
});

module.exports = router;
