/* Match Maker offline support for the web / laptop / iPhone version.
   - The page (index.html) and version.json: network first, so a new version shows up
     as soon as there is internet; falls back to the saved copy when offline.
   - Icons, manifest, fonts and the Excel library: cache first (they rarely change).
   Bump CACHE when this file's caching rules change. */
const CACHE = 'mm-v2';
const CORE = ['./', './index.html', './manifest.webmanifest', './icons/icon-192.png', './icons/icon-512.png', './icons/apple-touch-icon.png', './icons/favicon-32.png'];
const XLSX_URL = 'https://cdn.jsdelivr.net/npm/xlsx-js-style@1.2.0/dist/xlsx.bundle.js';   // SheetJS 0.18.5 + cell styles (formatted report)

self.addEventListener('install', e => {
  e.waitUntil((async () => {
    const c = await caches.open(CACHE);
    await c.addAll(CORE);
    try { await c.add(new Request(XLSX_URL, { mode: 'cors' })); } catch (err) { /* fetched later when online */ }
    await self.skipWaiting();
  })());
});

self.addEventListener('activate', e => {
  e.waitUntil((async () => {
    for (const k of await caches.keys()) if (k !== CACHE) await caches.delete(k);
    await self.clients.claim();
  })());
});

self.addEventListener('fetch', e => {
  const req = e.request;
  if (req.method !== 'GET') return;
  const url = new URL(req.url);
  // tablet sync talks to a Main tablet on the local network; never cache that
  if (url.port === '8787') return;
  const sameOrigin = url.origin === self.location.origin;
  const isPage = req.mode === 'navigate' || (sameOrigin && (url.pathname.endsWith('/') || url.pathname.endsWith('/index.html') || url.pathname.endsWith('/version.json')));
  if (isPage) { e.respondWith(networkFirst(req)); return; }
  const cacheable = sameOrigin || url.hostname === 'cdnjs.cloudflare.com' || url.hostname === 'cdn.jsdelivr.net' || url.hostname === 'fonts.googleapis.com' || url.hostname === 'fonts.gstatic.com';
  if (cacheable) e.respondWith(cacheFirst(req));
});

async function networkFirst(req) {
  const c = await caches.open(CACHE);
  // every page load is stored under one key, so the offline copy is always the newest one seen
  const key = req.mode === 'navigate' ? './index.html' : req;
  try {
    const res = await fetch(req, { cache: 'no-store' });
    if (res.ok) c.put(key, res.clone());
    return res;
  } catch (err) {
    return (await c.match(key, { ignoreSearch: true })) || (await c.match('./index.html')) || Response.error();
  }
}

async function cacheFirst(req) {
  const c = await caches.open(CACHE);
  const hit = await c.match(req);
  if (hit) return hit;
  const res = await fetch(req);
  if (res.ok || res.type === 'opaque') c.put(req, res.clone());
  return res;
}
