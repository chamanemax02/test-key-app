const express = require('express');
const helmet = require('helmet');
const cors = require('cors');
const config = require('./config/env');
const { apiLimiter } = require('./middleware/rateLimiter');
const { errorHandler } = require('./middleware/errorHandler');
const apiRoutes = require('./routes/api.routes');

const app = express();

// Security Middlewares
app.use(helmet());
app.use(cors());
app.use(express.json());
app.use('/api', apiLimiter);

// API Routes
app.use('/api', apiRoutes);

// Root Status
app.get('/', (req, res) => {
  res.json({
    name: 'SONORA LK Backend API Proxy',
    version: '1.0.0',
    status: 'ONLINE',
    docs: '/api/health',
  });
});

// Error Handler
app.use(errorHandler);

if (process.env.NODE_ENV !== 'test') {
  app.listen(config.port, () => {
    console.log(`[SONORA LK Proxy] Server running on http://localhost:${config.port}`);
  });
}

module.exports = app;
