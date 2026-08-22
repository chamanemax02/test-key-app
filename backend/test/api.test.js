const test = require('node:test');
const assert = require('node:assert');
const { normalizeTrack, normalizeSearch } = require('../src/utils/normalizer');

test('normalizeTrack correctly normalizes raw Chama API result', () => {
  const raw = {
    id: '7qiZfU4dY1lWllzX7mPBI3',
    title: '  Shape of You  ',
    artist: 'Ed Sheeran',
    album: 'Divide',
    duration: '3:53',
    duration_ms: 233712,
    release_date: '2017-03-03',
    thumbnail: 'https://thumbnail.url',
    spotify_url: 'https://open.spotify.com/track/7qiZfU4dY1lWllzX7mPBI3',
    preview_url: 'https://preview.url',
  };

  const normalized = normalizeTrack(raw);
  assert.strictEqual(normalized.id, '7qiZfU4dY1lWllzX7mPBI3');
  assert.strictEqual(normalized.title, 'Shape of You');
  assert.strictEqual(normalized.artist, 'Ed Sheeran');
  assert.strictEqual(normalized.album, 'Divide');
  assert.strictEqual(normalized.duration, '3:53');
  assert.strictEqual(normalized.durationMs, 233712);
  assert.strictEqual(normalized.releaseDate, '2017-03-03');
  assert.strictEqual(normalized.downloadAllowed, true);
});

test('normalizeSearch handles empty or invalid lists safely', () => {
  assert.deepStrictEqual(normalizeSearch(null), []);
  assert.deepStrictEqual(normalizeSearch(undefined), []);
  assert.deepStrictEqual(normalizeSearch([]), []);
});
