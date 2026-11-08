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

## How to use the app

Set up once, then each playing day the organizer runs the session from the Main tablet while players score on the court tablets.

### A. Before the first session (once)

1. **Install the app** on every tablet: open MatchMaker.apk → allow this source → **Install** (Play Protect: **More details → Install anyway**). A newer APK installs over the old one and keeps the data.
2. **Prepare the player list** in MatchMaker_Template.xlsx: Players sheet, columns Name, Level (A–D), Type (Subscriber or Guest). Keep Players as the first sheet.
3. **Copy the Excel file to the Main tablet** (WhatsApp, email or Drive → save to Downloads).
4. On the Main tablet: **Players → Choose file** → pick the file.
5. Check **Settings**: Number of courts (for example 4), Doubles, Mixing skill levels, Subscriber priority, Fees.
6. Optional: add **player photos** — tap a name → 📷 **Take photo**.

### B. Setting up the tablets at the court (each day)

1. Turn on a **hotspot** (a phone or the Main tablet) and connect every tablet to it. No mobile data is needed.
2. **Main tablet (organizer):** Settings → Tablet sync → **Main tablet**. Note the address it shows (e.g. 192.168.43.1).
3. **Court tablets (one per court):** Settings → Tablet sync → **Court tablet** → Main address, court, PIN → **Start court mode**.
4. **Display tablet:** Settings → Tablet sync → **Display tablet** → Main address → **Connect**.
5. Only one tablet? Leave Tablet sync **Off** and run everything on it.

```
          Hotspot network (no internet needed)
                 ┌──────────────────────┐
                 │ Main tablet (organizer)│
                 └──────────┬───────────┘
      players & courts ↓    │    ↑ scores
   ┌────────┬────────┬──────┴─┬────────┬──────────┐
 Court 1  Court 2  Court 3  Court 4   Display (view only)
```

### C. Running the session (organizer, on the Main tablet)

1. **Attendance:** tick everyone who arrives (Check in all, or by level).
2. **Courts:** **Auto match** (fair, balanced teams) or **Pick players** (tick 4, fewest games first). **Fill empty courts** does all at once.
3. The court tablet switches to that match's scoreboard automatically.
4. **Someone can't play:** tap their name → **Can't play — replace & set resting**, or pick a substitute. Later: **Back to play**.
5. **Match ends:** tap **Game done**. Winner is picked from the score and saved to History; the court empties.
6. Put the next players on and repeat.
7. **Payments:** tap **Unpaid → Paid** as players pay.

### D. Scoring a match (players, on the court tablet)

1. After the toss, tap **Serves first**; use **⇄ Swap left/right** so names match positions (only at 0–0).
2. Tap the big score of the team that wins each rally. The top bar shows server, side and receiver.
3. Mistake? **↶ Undo**.
4. Interval at 11, deuce at 20–20, 29–29 are announced; sides swap on screen when teams change ends.
5. When over, the tablet shows **Match finished**; the organizer records it.
6. Leaving court mode: **⚙** + PIN.

### E. Closing the day

1. **Payments → Unpaid**: collect what is left.
2. **Export Excel report** and save it (record and backup).
3. **Settings → Start new session** (tap twice).

## Everyday tasks (on the Main tablet)

| I want to… | Do this |
| --- | --- |
| Add one player or guest | Players → name, level, Subscriber/Guest → Add |
| Add many players | Players → Choose file, or paste `Maria, B, Guest` lines into Add many at once |
| Change level or Sub/Guest | Players → dropdown, or tap Sub/Guest |
| Rename or add a photo | Players → Edit, or tap the name on a court / in the queue |
| Mark someone resting | Queue → name → Rest; on court → Can't play |
| Someone went home | Queue → name → Went home, or untick in Attendance |
| Give guests more turns | Settings → Subscriber priority → Fair turns |
| Fix a wrong winner | History → Change winner |
| Send the day's totals | Payments → Copy summary → WhatsApp |
| Get the Excel report | Payments → Export Excel report |
| Start a new day | Settings → Start new session (twice) |
| Get a new version | Tap Update (court tablets update themselves while waiting) |

## Quick start (short version)

First-time setup:
1. Install MatchMaker.apk on the tablet.
2. Fill the Players sheet of the Excel template: Name, Level (A–D), Type (Subscriber/Guest).
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
| Settings | Options | Courts 1–8, Doubles/Singles, mixing, priority, fees, Update app (top), Start new session, Clear history, Backup roster, Delete all players |

Player tags: level badge A–D, SUB (blue) or GUEST (yellow), games played (e.g. 3g).

## Matching rules

1. Eligible: checked in, not resting, not on a court, level allowed on that court.
2. Queue order (Settings → Subscriber priority): Subscribers first (default; guests only when not enough subscribers) · Fair turns (fewest games, subscriber wins ties) · Equal. Ties: earliest last-game end first.
3. Group: first in queue is the seed; same level first, then per Skill mixing (Same only / Next level OK default / Any). If the seed's level cannot fill a group, the next player becomes seed.
4. Teams: A+=8, A-=7, B+=6, B-=5, C+=4, C-=3, D+=2, D-=1; the split with the smallest team difference wins, avoiding repeat partners.

Levels: 8 grades, A+ strongest … D- beginner. Skill mixing works on the letter: Same only = A+ with A-; Next level OK = A with B, B with C… Court level buttons (A B C D) each cover the + and - of that letter. Data from before 8 Nov 2026 with plain A/B/C/D became A+/B+/C+/D+.

Player types: Member, Family (pays the member fee) and Guest. Active status from the club roster: Reg, A-SUB, or No (inactive — hidden from Attendance until "Show inactive"). Gender (Male/Female) and Full Name are kept per player (Players → Edit).

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

## Match format

**Settings → Match format** sets the scoring for new matches (matches already being scored keep their format).

| Setting | Options | Standard |
| --- | --- | --- |
| Games per match | 1, 2, 3, 4 or 5 | 3 |
| Points per game | 11 (max 15), 15 (max 21), 21 (max 30) — always win by 2 | 21 |
| Match ends (3 or 5 games) | Best of N (stop when a team has the majority) or Play all N games | Best of 3 |

- 1 game: the game winner wins.
- 2 or 4 games: all games are always played. More games won wins; if equal, more total points; if still equal, the match is a **draw** (saved with no winner).
- The interval and change of ends happen at half the points (6, 8 or 11); in the last possible game the teams also change ends there.
- The scoreboard header shows the format, e.g. "Game 2 of up to 3 · to 21".

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

## Teams

Players can belong to a team (for example "Racket Kings"). Teams are optional.

- **Set a team:** Players → add form (Team box), **Edit** → Team, a 4th value in Add many at once (`Archie, C, Subscriber, Racket Kings`), or a **Team** column in Excel.
- **Lists:** the team shows under each name on Players and Attendance; a **team filter** sits next to the search box (Check in all works per team too).
- **Custom teams (Players tab):** type a name in **Custom teams** and tap **Add team** (✕ deletes, tap twice). Or fill the **Teams** sheet in the Excel file. These are match teams: players can be put in any of them for each game.
- **Scoreboard:** tap the team box at the top of each side to open the picker: tap a saved custom team, type a new name (**Use**), or choose **No team name**; it fills in automatically when both players share a team; ✕ clears it. Court tablets can set it too. The custom name is for that match only and does not change the players' saved teams.
- **Results:** the court card shows the team names; the win message, History card ("Winning team"), and **History → Teams** standings (played, won, lost, win %) use them.
- **Excel report:** Team column on Players, Payments and Win Statistics; Match History has Team 1 name, Team 2 name and Winning team; new **Team Statistics** sheet.

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

Upload (MatchMaker_Template.xlsx or the club roster sheet): first sheet only; columns Name (or Player Master List), Level (A+ … D-; a plain letter becomes "+"; a "Category" header still works), type Member/Family/Guest (found by its values even under another header such as "Timestamp"; blank = Member), Active (Reg / A-SUB / No), Gender, Full Name, Team. Missing level = C+. Existing names are updated. MatchMaker_Players.xlsx holds the 4th-quarter list (27 subscribers, 13 guests) with random levels to correct.

Export (MatchMaker_Report_YYYY-MM-DD.xlsx): Players · Payments (all days) · Match History · Win Statistics · Daily Summary. From Payments → Export Excel report or History → Export Excel.

## Installation and updates

Install: copy MatchMaker.apk to the tablet → tap → allow this source → Install (Play Protect: More details → Install anyway). A newer APK installs over the old one and keeps data.

Updates: Claude uploads index.html and a higher version.json here and republishes the Claude link. On opening (with internet) the app compares version.json with its own APP_VERSION and shows "New version available — Update"; tapping it saves the new page inside the app and reloads, keeping all data. Or tap Settings → **Update app** (top of Settings): it checks and installs in one tap. Changes to the Android shell (file picker, file saving) need a new APK.

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
| No Update bar | Needs internet on opening; GitHub can take ~5 min; use Settings → Update app |
| Guests rarely play | Switch priority to Fair turns or use Pick players |

## Technical reference

- Claude link: https://claude.ai/artifact/NCBtfjcXPbVzeZ8yNHivGj
- Update source: https://github.com/archie24bagadiong-max/matchmaker-app (index.html, version.json)
- Live website: https://archie24bagadiong-max.github.io/matchmaker-app/ (GitHub Pages from `main`, root). Works offline after the first visit and can be installed from the browser. Its data stays in that browser, separate from the tablets.
- Android package com.badminton.matchmaker, min API 24, target API 34, debug-signed (same key each build).
- One index.html runs every version; Excel via SheetJS 0.18.5 (bundled in APK).
- Android shell: WebView loading the page saved in app storage; bridge `AndroidApp.saveUpdate(html)` and `AndroidApp.saveFile(base64, name)`.
- Storage: tablet `localStorage` key `bmm_offline_v1`; Claude link online document `club/state`.
- Data fields: players, present, resting, games, paid, lastEnd, partners, courts, hist, ledger, mode, mix, prio, fee.

| Date | Version | Change |
| --- | --- | --- |
| 7 Nov 2026 | Page v18 | Match format setting: 1–5 games, 11/15/21 points, best of or play all, draws |
| 7 Nov 2026 | Page v19 | Receiving player highlighted in orange on the scoreboard (server stays lime) |
| 7 Nov 2026 | Page v20 | Update app button at the top of Settings (checks and installs in one tap) |
| 7 Nov 2026 | Page v21 | Scoreboard auto-fits short screens and phones (big score boxes always visible) |
| 7 Nov 2026 | Page v22 | Tied match (e.g. 1–1 in a 2-game match): Play decider (one game to 11, change ends at 6) or keep result (total points, else draw) |
| 7 Nov 2026 | Page v23 | Main tablet: Game done blinks red and plays a ting-ting chime when a court’s match is finished |
| 7 Nov 2026 | Page v24 | Landscape Courts tab uses the full width: 1 court fills it, 2 split it, 3 in thirds, 4 in quarters (5–8 wrap) |
| 7 Nov 2026 | Page v25 | Tap a court to open a pop-up menu (Game done, scoreboard, reshuffle, clear, players, levels); finished courts get a red border and blinking Done tag |
| 7 Nov 2026 | Page v26 | Developer card in Settings → About and at the end of Help: Archie Martinez Bagadiong “Baggy”, 0542873115 |
| 7 Nov 2026 | Page v27 (1.27) | Version shown as major.minor: internal number n → (n÷31+1).(n mod 31); 1.30 is followed by 2.0. version.json keeps the plain number |
| 7 Nov 2026 | Page v28 (1.28) | Update bar and Settings just say “New version 1.xx available” (no notes) |
| 7 Nov 2026 | Page v29 (1.29) | Set the serve before starting: tap the player who serves first, then the player who receives (0–0, first game); serving team can still be changed until the first point |
| 7 Nov 2026 | Page v30 (1.30) | Payments: mark each player paid by 💵 Cash or 📱 STC Pay; Collected shows the split; Excel Payments sheet has a Method column and Daily Summary has Cash / STC Pay totals |
| 7 Nov 2026 | Page v31 (2.0) | Export all activity: new Activity Log sheet (check-ins/outs, match start and finish with result, payments received/cancelled with amount and Cash / STC Pay, new sessions — all with date and time). Export button also in Settings |
| 8 Nov 2026 | Page v42 (2.11) | Faster and iPhone fit: opens without waiting for internet fonts (hotspot-only start); saving is grouped (one write per burst of taps, written at once when the app is hidden or reloads); photos shown via cached blob links; iPhone landscape notch padding, no text enlarging, pop-up height with Safari toolbar, page behind pop-ups does not scroll, no pull-to-refresh, faster taps, update bar under the notch |
| 8 Nov 2026 | Page v41 (2.10) | Pick lists: not played first, players who already played go to the bottom (1, 2, 3…) in every sort; Auto match / Pick players place the higher level in box 1 of each team (teams unchanged) |
| 8 Nov 2026 | Page v40 (2.9) | Sort buttons are Played (0, 1, 2, 3…), Level (A+ … D-) and Guest (guests, then Family, then Members); ties by fewest games, then name |
| 8 Nov 2026 | Page v39 (2.8) | Sort buttons (Fewest games · Level · Name) when filling a court box, in Pick players and when picking a substitute; Level sorts A+ … D-, fewest games first within a level; the choice is remembered on the device |
| 8 Nov 2026 | Page v38 (2.7) | Live website on GitHub Pages; QR code poster (qr.html, icons/qr-code.png); website shows an install prompt — iPhone/iPad: Share → Add to Home Screen steps, Android/laptop: Install button ("Not now" hides it for 7 days) |
| 8 Nov 2026 | Page v37 (2.6) | Search box (name or full name) when picking a substitute or filling a court box; score screen draws each team's score on its half of a badminton court, with each player shown in the service box they stand in (left/right from the team's view facing the net; singles players move with the server's score) |
| 8 Nov 2026 | Page v36 (2.5) | "Category" renamed to "Level"; 8 levels A+ … D-; Family type (member fee); Active status (No = inactive, hidden from Attendance); Gender and Full Name; club roster sheet imports directly; Excel Players sheet adds Full Name, Active, Gender |
| 7 Nov 2026 | Page v17 | Custom teams list on Players (and Teams sheet in Excel); team picker on the scoreboard |
| 7 Nov 2026 | Page v16 | Custom team name typed on the scoreboard per match |
| 7 Nov 2026 | Page v15 | Team names: players, scoreboard, history, team standings, Excel |
| 7 Nov 2026 | Page v14 | Scoreboard: big Games won panel per team with each game score; shorter score boxes |
| 7 Nov 2026 | Page v13 | In-app How to use guide (? button, Settings) and scoring help on the scoreboard |
| 7 Nov 2026 | Docs | Full how-to guide and everyday tasks added |
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

## Developer

Archie Martinez Bagadiong "Baggy" · Contact: 0542873115 · archie24bagadiong@gmail.com
