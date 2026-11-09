# Match Maker – JAMREIBIZ Badminton Club

A badminton club organizer: attendance, fair court matching, live scoring, fees and match history. It runs on Android tablets, laptops, Android phones and iPhones, and works without internet.

**Website:** https://archie24bagadiong-max.github.io/matchmaker-app/
**Picture guide:** https://archie24bagadiong-max.github.io/matchmaker-app/guide.html

## Install

| Device | How |
| --- | --- |
| Club tablets (Android) | Download [MatchMaker.apk](https://archie24bagadiong-max.github.io/matchmaker-app/MatchMaker.apk) → Install (Play Protect: More details → Install anyway). Needed for tablet sync, court tablets and display tablets. |
| PC / laptop | Open the website in Chrome or Edge → Install (icon in the address bar). |
| Android phone | Open the website → tap Install on the green bar. |
| iPhone / iPad | Open [iphone.html](https://archie24bagadiong-max.github.io/matchmaker-app/iphone.html) in Safari → Install Match Maker → Allow → Settings → Profile Downloaded → Install. Or: Share → Add to Home Screen. |

QR codes for all of these: [qr.html](https://archie24bagadiong-max.github.io/matchmaker-app/qr.html).

Each device keeps its own data (players, attendance, payments, history). Back up with **Payments → Export Excel report**; restore it in **Settings → Backup & restore**.

## What it does

- **Attendance:** tick players as they arrive; filter by level, team or playing day.
- **Fair matching:** Auto match and Fill empty courts pick the fewest-games players first (Equal rounds), respect court levels, skill mixing and member priority, and balance the teams.
- **Next games:** plan games ahead; **Game done** puts the next one straight on the finished court (with Undo). Tap a planned player to replace or swap them.
- **Change teams:** drag a player onto another on the same court (touch: press and hold, then drag), until the first point is scored.
- **Scoring:** official BWF rally scoring on the court card, on court tablets, or on any phone that scans the court's QR code.
- **Fees and payments:** member and guest fees, cash / STC Pay, part payments and dated balances.
- **History and reports:** every match with teams and winner; Excel report by date or date range.

Full documentation: [DOCS.md](DOCS.md).

## Files

| File | What it is |
| --- | --- |
| `index.html` | The whole app (one file). |
| `version.json` | Latest version number. The Android app and the website check it and offer the update when it is higher than their `APP_VERSION`. |
| `MatchMaker.apk` | Android app. It bundles `index.html` and updates itself from this repo. |
| `android/` | Android shell source and `build-apk.ps1` (builds and signs the APK without Gradle). |
| `iphone.html`, `MatchMaker.mobileconfig` | iPhone installer page and its one-tap install profile (Home Screen web clip). |
| `qr.html`, `guide.html`, `guide/` | QR code page, picture guide and its screenshots. |
| `sw.js`, `manifest.webmanifest`, `icons/` | Offline support and install metadata for the website. |
| `DOCS.md` | Full documentation and change history. |

## Releasing an update

1. Change `index.html` and bump `APP_VERSION` in it, and `version` in `version.json` (same number).
2. Rebuild the APK so new installs start on the latest page: `android/build-apk.ps1 -VersionCode N -VersionName 1.X` (signing key is kept outside the repo), then copy it to `MatchMaker.apk`.
3. Update `DOCS.md` (change history) and, if the screens changed, `guide.html`.
4. Commit and push to `main`. One push updates the website, the iPhone/laptop installs and the Android app.

Never commit the club roster or player template files: this repository is public.

## Developer

Archie Martinez Bagadiong "Baggy" · Contact: 0542873115 · archie24bagadiong@gmail.com
