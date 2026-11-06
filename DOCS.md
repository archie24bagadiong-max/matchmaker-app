# Match Maker - JAMREIBIZ Badminton Club — App Documentation

As of 7 Nov 2026. Full formatted version: https://claude.ai/artifact/2rCTvyBGck3BVPcYq5Q1yL

## Overview

Match Maker runs the club's badminton sessions: attendance, fair doubles matches on the courts, games played, fees, winners, and Excel export. Built for a roster of about 100 players graded A–D on 3 courts.

| Version | Where it runs | Login | Data saved | Updates |
| --- | --- | --- | --- | --- |
| Android app (MatchMaker.apk) | Tablet | None | On the tablet only | Update bar inside the app, from this repo |
| Claude link | Claude app or browser | Claude account | Online, synced | Republished by Claude |
| Offline file (badminton-matchmaker.html) | Chrome as a file | None | That device only | None (superseded) |

The Android app is the main version at the court. The Android app and the Claude link do not share data.

## Quick start

First-time setup:
1. Install MatchMaker.apk on the tablet.
2. Fill the Players sheet of the Excel template: Name, Category (A–D), Type (Subscriber/Guest).
3. Players → Choose file → pick the Excel file.
4. Check Settings: courts, Doubles, skill mixing, subscriber priority, fees.

Each session day:
1. Attendance: tick who arrived (Check in all / by level).
2. Courts: Auto match or Pick players on each empty court.
3. Game done → choose winner (Left/Right) or Skip. The court clears.
4. Refill with Auto match or Pick players.
5. Payments: mark Unpaid → Paid.
6. Export Excel report if wanted.
7. Settings → Start new session (tap twice) to close the day. Payments are archived; roster and history stay.

## Screens

| Tab | Shows | Actions |
| --- | --- | --- |
| Courts | Present/playing/waiting/resting counts, courts, queue, resting list | Fill empty courts, Auto match, Pick players, Game done, Reshuffle, Clear, court levels, replace a player, Back to play |
| Attendance | Present per level, tick list | Tick/untick, check in all or by level, search |
| Players | Add form, Excel import, bulk paste, roster | Add, Choose file, Add list, change level, Sub/Guest, rename, delete |
| Payments | Totals, per-player games and fee | Filter, mark Paid, Export Excel report, Copy summary |
| History | Matches by day with court, teams, winner; wins leaderboard | Filter by date/court, Change winner, delete match, Export Excel |
| Settings | Options | Courts 1–8, Doubles/Singles, mixing, priority, fees, App updates, Start new session, Clear history, Backup roster, Delete all players |

Player tags: level badge A–D, SUB (blue) or GUEST (yellow), games played (e.g. 3g).

## Matching rules

1. Eligible: checked in, not resting, not on a court, level allowed on that court.
2. Queue order (Settings → Subscriber priority): Subscribers first (default; guests only when not enough subscribers) · Fair turns (fewest games, subscriber wins ties) · Equal. Ties: earliest last-game end first.
3. Group: first in queue is the seed; same level first, then per Skill mixing (Same only / Next level OK default / Any). If the seed's level cannot fill a group, the next player becomes seed.
4. Teams: A=4, B=3, C=2, D=1; the split with the smallest team difference wins, avoiding repeat partners.

Can't play: replaced by closest-level substitute and moved to Resting until Back to play; the unfinished game is not counted for them.

## Scoring

Each court has a **Keep score** button that opens a full-screen scoreboard, run from one tablet for all courts. Official BWF rules: rally point, games to 21, win by 2, capped at 30 (29–29: next point wins), best of 3.

1. Tap **Serves first** on the team that won the toss; use **⇄ Swap left/right** to set who stands in the right court (only at 0–0).
2. Tap a team's big score to give it the rally. The app shows who serves, from which court (even score = right, odd = left), and who receives.
3. Serving side wins the rally: same server, switches court. Receiving side wins: they serve, from the court matching their score.
4. Teams change ends automatically: the scoreboard and the court card swap sides after game 1, after game 2, and when a team reaches 11 in game 3 (Undo swaps back). Alerts also show the interval at 11, 20–20 and 29–29.
5. The game winner serves first in the next game. **Undo** reverses the last rally (any number of times); **Reset score** clears it.
6. When the match ends, **Save result & clear court** records the winner and game scores in History (and the Match History sheet's Score column). **Game done** picks the winner automatically from the score: the team with more games won, or if games are level, the higher current score. It asks only when nothing separates the teams (no score kept, or a tied score).
7. Replacing a player mid-match keeps the score; new players on a court start a fresh score.

## Tablet sync (local network)

Several tablets can show the same session over a hotspot or Wi-Fi, with no internet and no outside service. Needs APK 1.5 or later on every tablet.

1. Turn on a hotspot (on the Main tablet or a phone) and connect all tablets to it.
2. On the Main tablet: **Settings → Tablet sync → Main tablet**. It shows an address such as `192.168.43.1`.
3. On each other tablet: **Settings → Tablet sync → Display tablet**, type that address, tap **Connect**.
4. The header shows **Main · sharing** or **Display · live**. Displays update about every 1.5 seconds.

- Only the Main tablet makes changes (attendance, courts, scores, payments). Display tablets are read-only and dim the editing buttons.
- A Display tablet keeps its own data separately; switching back to **Off** brings it back.
- The address can change when the hotspot restarts; re-enter it on the Displays if they show **Display · offline**.
- The Main tablet keeps its screen on; keep the app open on it during the session.
**Court tablets (APK 1.7 or later).** A tablet can be locked to one court's scoreboard:

1. On the court tablet: **Settings → Tablet sync → Court tablet**. Enter the Main tablet's address, choose the court, and set an organizer PIN (optional), then **Start court mode**.
2. It shows only that court: a waiting screen until the organizer puts players on the court, then the full scoreboard. Players can choose who serves, swap sides at 0–0, score, undo and reset — nothing else.
3. Every change is sent to the Main tablet within a second and shown on the Main and Display tablets.
4. When the match ends, the court tablet shows "Match finished"; the organizer taps **Game done** on the Main tablet (winner picked automatically) and the court tablet returns to waiting.
5. To leave court mode, tap ⚙ and enter the PIN.

Example for 4 courts: tablets 1–4 = Court tablets, tablet 5 = Main, tablet 6 = Display.

- How it works: the Main tablet runs a small server on port 8787 (`GET /state` returns the session as JSON, `POST /score` receives a court's score); Displays and Court tablets poll it. Each score carries a sequence number so the newest change wins.

## Player photos

Each player can have a photo, shown next to their name on the court cards, the scoreboard, the pick list, Attendance and Players. Players without a photo show their initials.

1. Tap a player's name on a court, in the waiting queue, or **Edit** on the Players tab.
2. Tap **📷 Take photo** (opens the tablet camera) or **Choose photo** (gallery). **Remove** deletes it (tap twice).
3. The photo is cropped to a square and saved at about 20 KB.

- The camera button needs APK 1.6 or later; older APKs fall back to choosing a file.
- Photos are stored on the tablet, separate from the session data, and are passed to Display tablets automatically. In the Claude link they sync across signed-in devices.
- Photos are not included in the Excel report.

## Fees

Subscriber: 2.50 SR per game. Guest: 25 SR minimum covering 3 games, then 3.33 SR per extra game (6 games = 35 SR).

Guest fee = 25 + max(0, games − 3) × 10/3

| Games | Subscriber | Guest |
| --- | --- | --- |
| 1 | 2.50 | 25.00 |
| 2 | 5.00 | 25.00 |
| 3 | 7.50 | 25.00 |
| 4 | 10.00 | 28.33 |
| 5 | 12.50 | 31.67 |
| 6 | 15.00 | 35.00 |
| 7 | 17.50 | 38.33 |

All fee numbers are editable in Settings → Fees. Above 6 games the 3.33 SR/game rate is an assumption. Start new session archives the day's payment rows into a ledger (up to 4,000 rows); payment history starts 7 Nov 2026.

## Excel

Upload (MatchMaker_Template.xlsx): first sheet only; columns Name, Category (A–D or 1–4), Type (Subscriber/Guest, blank = Subscriber). Missing category = C. Existing names are updated. MatchMaker_Players.xlsx holds the 4th-quarter list (27 subscribers, 13 guests) with random levels to correct.

Export (MatchMaker_Report_YYYY-MM-DD.xlsx): Players · Payments (all days) · Match History · Win Statistics · Daily Summary. From Payments → Export Excel report or History → Export Excel.

## Installation and updates

Install: copy MatchMaker.apk to the tablet → tap → allow this source → Install (Play Protect: More details → Install anyway). A newer APK installs over the old one and keeps data.

Updates: Claude uploads index.html and a higher version.json here and republishes the Claude link. On opening (with internet) the app compares version.json with its own APP_VERSION and shows "New version available — Update"; tapping it saves the new page inside the app and reloads, keeping all data. Settings → App updates → Check for updates. Changes to the Android shell (file picker, file saving) need a new APK.

## Data and backup

| Data | Kept until |
| --- | --- |
| Roster | Deleted on Players or Settings → Delete all players |
| Today's attendance, games, paid, courts, resting | Start new session |
| Payment ledger | Kept, last 4,000 rows |
| Match history | Clear match history, last 1,000 matches |

Uninstalling or clearing app storage erases everything — back up with Export Excel report.

## Troubleshooting

| Problem | Fix |
| --- | --- |
| App closes on opening | Fixed in APK 1.2; install the latest APK |
| Choose file does nothing in the Claude app | Use the tablet app, Chrome, paste into Add many at once, or send the file to Claude |
| Claude link opens in the Claude app | Choose Chrome, paste the link, or turn off "Open supported links" for Claude |
| Export asks for the latest APK | Install the current MatchMaker.apk once |
| No Update bar | Needs internet on opening; GitHub can take ~5 min; use Check for updates |
| Guests rarely play | Switch priority to Fair turns or use Pick players |

## Technical reference

- Claude link: https://claude.ai/artifact/NCBtfjcXPbVzeZ8yNHivGj
- Update source: https://github.com/archie24bagadiong-max/matchmaker-app (index.html, version.json)
- Android package com.badminton.matchmaker, min API 24, target API 34, debug-signed (same key each build).
- One index.html runs every version; Excel via SheetJS 0.18.5 (bundled in APK).
- Android shell: WebView loading the page saved in app storage; bridge `AndroidApp.saveUpdate(html)` and `AndroidApp.saveFile(base64, name)`.
- Storage: tablet `localStorage` key `bmm_offline_v1`; Claude link online document `club/state`.
- Data fields: players, present, resting, games, paid, lastEnd, partners, courts, hist, ledger, mode, mix, prio, fee.

| Date | Version | Change |
| --- | --- | --- |
| 7 Nov 2026 | Page v12 | Court tablets update themselves while their court is waiting; all tablets re-check for updates every 15 min (court: 3 min) |
| 7 Nov 2026 | Page v11 | Scoreboard layout fixed for landscape / short screens |
| 7 Nov 2026 | Page v10, APK 1.7 | Court tablets locked to one court's scoreboard |
| 7 Nov 2026 | Page v9, APK 1.6 | Player photos from camera or gallery |
| 7 Nov 2026 | Page v8 | Automatic change of ends on the scoreboard |
| 7 Nov 2026 | Page v7, APK 1.5 | Tablet sync over local network; Android code now in Java (android/ folder) |
| 7 Nov 2026 | Page v6 | Game done picks the winner from the score |
| 7 Nov 2026 | Page v5 | Title adds Badminton Club |
| 7 Nov 2026 | Page v4 | App title: Match Maker - JAMREIBIZ |
| 7 Nov 2026 | Page v3 | Live badminton scoring with official serving rules |
| 7 Nov 2026 | Page v2, APK 1.3 | Excel report export, payment ledger, Android file saving |
| 7 Nov 2026 | Page v1, APK 1.2 | Fixed crash on opening |
| 7 Nov 2026 | APK 1.1 | In-app updates |
| 7 Nov 2026 | APK 1.0 | First Android build |
| 6–7 Nov 2026 | Claude link | Courts, attendance, players, payments, guests, history, import |
