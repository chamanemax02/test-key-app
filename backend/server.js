require("dotenv").config();
const express = require("express");
const helmet = require("helmet");
const cors = require("cors");
const rateLimit = require("express-rate-limit");
const spotifyRoutes = require("./routes/spotify");

const app = express();
app.use(helmet());
app.use(cors());
app.use(express.json());

// Rate-limit at the proxy so a single misbehaving client can't burn
// through the upstream provider's quota (config'd via API_RATE_LIMIT).
app.use(
  rateLimit({
    windowMs: 60 * 1000,
    max: Number(process.env.API_RATE_LIMIT || 60),
    standardHeaders: true,
    legacyHeaders: false,
  })
);

app.use("/v1", spotifyRoutes);

app.get("/health", (_req, res) => res.json({ status: "ok" }));

app.use((err, _req, res, _next) => {
  console.error(err);
  res.status(500).json({ error: "Internal server error" });
});

const port = process.env.PORT || 3000;
app.listen(port, () => console.log(`SONORA LK backend proxy listening on :${port}`));
