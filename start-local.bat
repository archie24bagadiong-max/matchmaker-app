@echo off
rem Runs Match Maker on this laptop without internet hosting.
rem Open http://localhost:8080 in Chrome or Edge, then use the Install button in the address bar.
rem Other devices on the same Wi-Fi/hotspot can open http://<this laptop's IP>:8080
rem (offline mode and install need https or localhost, so those work fully only on this laptop).
cd /d "%~dp0"
echo Match Maker running at http://localhost:8080  (close this window to stop)
start "" http://localhost:8080
python -m http.server 8080 --bind 0.0.0.0
