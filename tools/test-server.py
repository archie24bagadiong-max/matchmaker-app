"""Match Maker test server: one shared copy of the club data for every device that opens the link.

For testing only (like VS-Optical's): run it on this PC and share it with a Cloudflare quick tunnel.
    python tools\\test-server.py            -> http://localhost:8790
    cloudflared tunnel --url http://localhost:8790   -> prints the https://….trycloudflare.com link

How it works: the page is served with a small script that gives it the same online-document store the app
already uses for the Claude link (club/state + photos/<id>), backed by this server. Every device polls for
changes every 1.5 s, so a change on one shows on the others. Last save wins, as with the Claude link.

Data is kept in D:\\Codes\\matchmaker-data\\store.json (outside the repo, never committed).
Only the app's public files are served; the roster and player templates are never sent.
"""
import json, os, sys, threading, time
from http.server import ThreadingHTTPServer, BaseHTTPRequestHandler

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA_DIR = os.environ.get('MM_DATA', os.path.join(os.path.dirname(ROOT), 'matchmaker-data'))
STORE = os.path.join(DATA_DIR, 'store.json')
PORT = int(sys.argv[1]) if len(sys.argv) > 1 else 8790
MAX_BODY = 30 * 1024 * 1024

# only these are served (the repo folder also holds the members' roster, which must never go out)
PUBLIC_FILES = {'index.html', 'sw.js', 'manifest.webmanifest', 'version.json', 'guide.html', 'iphone.html', 'qr.html',
                'MatchMaker.apk', 'MatchMaker.mobileconfig'}
PUBLIC_DIRS = {'icons', 'guide'}
TYPES = {'.html': 'text/html; charset=utf-8', '.js': 'text/javascript; charset=utf-8', '.json': 'application/json',
         '.webmanifest': 'application/manifest+json', '.png': 'image/png', '.svg': 'image/svg+xml', '.webp': 'image/webp',
         '.apk': 'application/vnd.android.package-archive', '.mobileconfig': 'application/x-apple-aspen-config'}

# the store: path -> {"rev": n, "data": {...} or None when deleted}
lock = threading.Lock()
docs, rev = {}, 0
if os.path.exists(STORE):
    try:
        with open(STORE, encoding='utf-8') as f:
            saved = json.load(f)
        docs, rev = saved.get('docs', {}), saved.get('rev', 0)
    except Exception as e:
        print('Could not read', STORE, e)


def persist():
    os.makedirs(DATA_DIR, exist_ok=True)
    tmp = STORE + '.tmp'
    with open(tmp, 'w', encoding='utf-8') as f:
        json.dump({'rev': rev, 'docs': docs}, f)
    os.replace(tmp, STORE)


SHIM = r"""<script>
/* test server: the app's online-document store (window.claude.use('db')), kept on the Match Maker test server */
(function(){
  const cache={}, docSubs={}, colSubs={}; let since=0, first=true, okOnce=false;
  const api=(op,body)=>fetch('api/'+op,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(body||{}),cache:'no-store'})
    .then(r=>{ if(!r.ok) throw {code:r.status===413?'invalid_argument':'unavailable'}; return r.json(); });
  const docSnap=p=>({exists:!!cache[p],metadata:{fromCache:false,hasPendingWrites:false},data:()=>cache[p]});
  const colSnap=c=>({docs:Object.keys(cache).filter(p=>p.startsWith(c+'/')&&cache[p]).map(p=>({id:p.slice(c.length+1),data:()=>cache[p]}))});
  function notify(changed){
    Object.keys(docSubs).forEach(p=>{ if(first||changed.has(p)) docSubs[p].forEach(f=>{ try{ f(docSnap(p)); }catch(e){} }); });
    Object.keys(colSubs).forEach(c=>{ if(first||[...changed].some(p=>p.startsWith(c+'/'))) colSubs[c].forEach(f=>{ try{ f(colSnap(c)); }catch(e){} }); });
    first=false;
  }
  async function poll(){
    try{ const r=await api('poll',{since}); const ch=new Set();
      Object.entries(r.docs||{}).forEach(([p,d])=>{ cache[p]=d.data||null; ch.add(p); });
      since=r.rev; okOnce=true; notify(ch); }
    catch(e){}
    setTimeout(poll,1500);
  }
  const ref=p=>({
    set:o=>api('set',{path:p,data:o}).then(()=>{ cache[p]=o; }),
    delete:()=>api('set',{path:p,data:null}).then(()=>{ cache[p]=null; }),
    onSnapshot:(cb)=>{ (docSubs[p]=docSubs[p]||[]).push(cb); if(okOnce) cb(docSnap(p)); return ()=>{}; }
  });
  const db={doc:ref,collection:c=>({onSnapshot:(cb)=>{ (colSubs[c]=colSubs[c]||[]).push(cb); if(okOnce) cb(colSnap(c)); return ()=>{}; }})};
  window.claude={use:async k=>k==='db'?db:null};
  setTimeout(poll,0);
})();
</script>"""


class H(BaseHTTPRequestHandler):
    server_version = 'MatchMakerTest'

    def log_message(self, fmt, *a):
        if not (self.path.startswith('/api/poll')):
            sys.stderr.write('%s %s\n' % (time.strftime('%H:%M:%S'), fmt % a))

    def send(self, code, body, ctype='application/json', extra=None):
        if isinstance(body, str):
            body = body.encode('utf-8')
        self.send_response(code)
        self.send_header('Content-Type', ctype)
        self.send_header('Content-Length', str(len(body)))
        self.send_header('Cache-Control', 'no-store')
        self.send_header('X-Content-Type-Options', 'nosniff')
        for k, v in (extra or {}).items():
            self.send_header(k, v)
        self.end_headers()
        self.wfile.write(body)

    def do_GET(self):
        path = self.path.split('?', 1)[0].split('#', 1)[0].lstrip('/') or 'index.html'
        parts = path.split('/')
        if '..' in parts or not (path in PUBLIC_FILES or (len(parts) == 2 and parts[0] in PUBLIC_DIRS and parts[1])):
            return self.send(404, 'Not found', 'text/plain')
        full = os.path.join(ROOT, *parts)
        if not os.path.isfile(full):
            return self.send(404, 'Not found', 'text/plain')
        with open(full, 'rb') as f:
            body = f.read()
        if path == 'index.html':
            body = body.decode('utf-8').replace('<head>', '<head>' + SHIM, 1).encode('utf-8')
        self.send(200, body, TYPES.get(os.path.splitext(path)[1], 'application/octet-stream'))

    def do_POST(self):
        global rev
        n = int(self.headers.get('Content-Length') or 0)
        if n > MAX_BODY:
            return self.send(413, '{"error":"too big"}')
        try:
            req = json.loads(self.rfile.read(n) or b'{}')
        except Exception:
            return self.send(400, '{"error":"bad json"}')
        op = self.path.split('?', 1)[0]
        if op == '/api/poll':
            since = int(req.get('since') or 0)
            with lock:
                out = {p: d for p, d in docs.items() if d['rev'] > since}
                return self.send(200, json.dumps({'rev': rev, 'docs': out}))
        if op == '/api/set':
            p = str(req.get('path') or '')
            if not (p == 'club/state' or (p.startswith('photos/') and 7 < len(p) < 80)):
                return self.send(400, '{"error":"bad path"}')
            with lock:
                rev += 1
                docs[p] = {'rev': rev, 'data': req.get('data')}
                persist()
                return self.send(200, json.dumps({'rev': rev}))
        self.send(404, '{"error":"no such call"}')


if __name__ == '__main__':
    print('Match Maker test server on http://localhost:%d  (data: %s)' % (PORT, STORE))
    ThreadingHTTPServer(('127.0.0.1', PORT), H).serve_forever()
