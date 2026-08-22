const test = require('node:test');
const assert = require('node:assert');
const { normalizeTrack, normalizeSearch } = require('../src/utils/normalizer');

test('normalizeTrack correctly normalizes raw Chama API result', () => {
  const raw = {
    video_id: 'JGwWNGJdvx8',
    title: '  Shape of You  ',
    uploader: 'Ed Sheeran',
    duration: 264,
    thumbnail: 'https://thumbnail.url',
    youtube_url: 'https://www.youtube.com/watch?v=JGwWNGJdvx8',
  };

  const normalized = normalizeTrack(raw);
  assert.strictEqual(normalized.id, 'JGwWNGJdvx8');
  assert.strictEqual(normalized.title, 'Shape of You');
  assert.strictEqual(normalized.artist, 'Ed Sheeran');
  assert.strictEqual(normalized.duration, '4:24');
  assert.strictEqual(normalized.durationMs, 264000);
  assert.strictEqual(normalized.downloadAllowed, true);
});

test('normalizeSearch handles empty or invalid lists safely', () => {
  assert.deepStrictEqual(normalizeSearch(null), []);
  assert.deepStrictEqual(normalizeSearch(undefined), []);
  assert.deepStrictEqual(normalizeSearch([]), []);
});
