# PREMIUM MUSIC PLAYER ANDROID APP — COMPLETE DEVELOPMENT MASTER PROMPT

ඔබ professional-level Android application developer, UI/UX designer, backend/API integration engineer, security engineer සහ DevOps engineer කෙනෙක් ලෙස ක්‍රියා කරන්න. මට අවශ්‍ය වන්නේ Spotify වැනි modern music-player experience එකක් ලබාදෙන, ඉතාමත් ලස්සන, smooth, premium-looking Android application එකක් නිර්මාණය කිරීමයි.

මෙම project එකේ primary goal එක වන්නේ user කෙනෙකුට music search කරලා track information බලන්න, legally available audio preview/playback අහන්න, playlist manage කරන්න, background audio controls භාවිතා කරන්න, mini-player එකකින් application එකේ ඕනෑම screen එකක සිට currently playing track එක control කරන්න, සහ user සතුව නීත්‍යානුකූලව තිබෙන/licensed audio files application එකට import කරලා offline playback සහ download/export workflow එකක් භාවිතා කරන්න හැකි premium music application එකක් නිර්මාණය කිරීමයි.

Spotify application එක copy කිරීම නොව, Spotify වැනි polished music-app experience එකකින් inspiration ලබාගෙන completely original branding, original UI layout, original components සහ original application identity එකක් නිර්මාණය කරන්න.

APPLICATION NAME:

"SONORA LK"

Application එකේ branding එක modern, premium, youthful සහ Sri Lankan users ටත් attractive වන ආකාරයට නිර්මාණය කරන්න.

Application එකේ logo එක simple music-wave / sound-wave inspired icon එකක් විය යුතු අතර Spotify logo එක copy නොකරන්න.

==================================================
1. CORE APPLICATION CONCEPT
==================================================

Application එක music discovery සහ personal audio playback සඳහා optimized mobile application එකක් විය යුතුය.

Main features:

- Music search
- Track metadata
- Artist information
- Album information
- Album artwork
- Music preview playback where legally permitted
- Background playback for application-owned/licensed/preview audio
- Mini player
- Full-screen player
- Play/pause
- Previous
- Next
- Seek bar
- Shuffle
- Repeat
- Queue
- Favorites
- Recently played
- Playlists
- Local music
- User-imported audio
- Offline local playback
- Dark mode
- Light mode
- Responsive UI
- Smooth animations
- Loading skeletons
- Error states
- Empty states
- Network retry
- Cached metadata
- Persistent player state
- Android notification media controls
- Lock-screen playback controls
- Headset/Bluetooth media controls
- Android back-button behavior
- Deep-link support
- Share track information
- Copy track link
- Open original Spotify page
- Legal/licensed audio download functionality where applicable

IMPORTANT:

Do NOT implement Spotify stream ripping or unauthorized Spotify content downloading.

Do NOT bypass DRM.

Do NOT extract full Spotify audio streams from protected Spotify services.

Do NOT create a feature whose purpose is to download copyrighted Spotify music without authorization.

If the provided backend returns an audio URL, use it only where the content is legally authorized for playback/download and where the API owner has the necessary rights.

For Spotify catalog content, prioritize metadata, artwork, external Spotify links and legally permitted previews.

==================================================
2. DESIGN LANGUAGE
==================================================

UI එක premium music application එකක් වගේ feel වෙන්න ඕන.

Overall design:

- Black / deep charcoal background
- Subtle gradients
- White typography
- Soft gray secondary text
- Accent gradient
- Glassmorphism cards where appropriate
- Rounded corners
- Large album artwork
- Smooth shadows
- Minimal clutter
- Premium spacing
- Smooth micro-interactions
- Modern icons
- High-quality typography

Primary background:

#080808

Secondary background:

#111111

Card background:

#181818

Primary text:

#FFFFFF

Secondary text:

#A7A7A7

Accent:

Use a modern gradient rather than copying Spotify's green branding.

Possible accent gradient:

Purple → Pink → Blue

The accent should be configurable through a centralized theme file.

DO NOT hard-code colors throughout the application.

Create:

ThemeColors
ThemeTypography
ThemeSpacing
ThemeShapes
ThemeAnimations

and use these consistently.

==================================================
3. HOME SCREEN
==================================================

Home screen එක application එකේ main discovery dashboard එක විය යුතුය.

Top section:

"Good Morning"
or
"Good Afternoon"
or
"Good Evening"

Sri Lanka local time අනුව greeting එක dynamically වෙනස් කරන්න.

Below greeting:

Search bar.

Placeholder:

"ගීතයක්, artist කෙනෙක් හෝ album එකක් සොයන්න..."

Search bar එක:

- Rounded
- Dark card
- Search icon
- Clear button
- Voice/search future-ready architecture

Below search:

Quick sections:

"Recently Played"

"Made For You"

"Trending Now"

"Popular Artists"

"Your Playlists"

"Local Music"

"Recommended"

Each section horizontal scrollable carousel එකක් විය යුතුය.

==================================================
4. SEARCH SCREEN
==================================================

Search screen එක fast සහ responsive විය යුතුය.

User search කරන විට:

- Track results
- Artist results
- Album results
- Playlist results

වෙන් කර display කරන්න.

Search result card එකේ:

- Album artwork
- Song title
- Artist
- Album
- Duration
- More button

More button click කළ විට:

- Play
- Add to queue
- Add to playlist
- Favorite
- Share
- Open original source

වැනි actions පෙන්වන්න.

Search input debounce කරන්න.

Example:

User letters type කරන විට request එකක් request එකක් බැගින් send නොකර 300–500ms debounce එකක් භාවිතා කරන්න.

Loading state:

Skeleton shimmer.

No results:

"ඔබ සෙවූ ගීතය හමු නොවීය."

Retry:

"නැවත උත්සාහ කරන්න"

==================================================
5. API INTEGRATION
==================================================

User ලබාදුන් API architecture එක backend service එකක් ලෙස abstract කරන්න.

Provided API patterns:

Spotify track metadata endpoint:

https://chama-movie-api.koyeb.app/api/v1/spotify/track

Download endpoint එක user-owned/licensed content සඳහා පමණක් legal-use validation එකක් යටතේ integrate කළ හැක.

IMPORTANT SECURITY REQUIREMENT:

API key client-side source code එකට hard-code නොකරන්න.

Instead:

ANDROID APP
      ↓
YOUR BACKEND
      ↓
CHAMA API
      ↓
RESPONSE
      ↓
ANDROID APP

Recommended architecture:

App → secure backend proxy → external API

Backend environment variables:

CHAMA_API_KEY

API_BASE_URL

API_TIMEOUT

API_RATE_LIMIT

API_CACHE_DURATION

API response එක app එකට අවශ්‍ය minimum fields වලට normalize කරන්න.

Never expose private API credentials in APK source.

==================================================
6. API RESPONSE NORMALIZATION
==================================================

External API response format එක වෙනස් වුණත් application එක break නොවෙන විදිහට mapper layer එකක් හදන්න.

Create:

TrackModel

ArtistModel

AlbumModel

PlaylistModel

SearchResultModel

PlaybackModel

DownloadModel

API response → internal model conversion layer එකක් තිබිය යුතුය.

Example conceptual TrackModel:

id

title

artistName

artistId

albumName

albumId

albumImage

duration

spotifyUrl

previewUrl

isPlayable

releaseDate

explicit

source

audioUrl

downloadAllowed

licenseStatus

Do not assume every field exists.

Null-safe parsing කරන්න.

==================================================
7. PLAYER ARCHITECTURE
==================================================

Player system එක application එකේ most important component එකක්.

Use a proper Android media playback architecture.

Player must support:

Play

Pause

Resume

Seek

Next

Previous

Queue

Shuffle

Repeat Off

Repeat One

Repeat All

Playback speed where legally appropriate

Volume

Progress

Duration

Buffered position

Background playback

Notification controls

Lock-screen controls

Bluetooth controls

Headphone button events

Audio focus

Audio becoming noisy

Pause when another application requests audio focus where appropriate

Persist playback position.

Player state:

IDLE

LOADING

PLAYING

PAUSED

BUFFERING

COMPLETED

ERROR

==================================================
8. BACKGROUND PLAYBACK
==================================================

Application එකේ background playback architecture එක robust විය යුතුය.

User app එක minimize කළත්:

- Authorized audio playback continue කරන්න.
- Notification එක update කරන්න.
- Lock-screen controls work කරන්න.
- Bluetooth controls work කරන්න.
- Play/pause work කරන්න.
- Next/previous work කරන්න.

Foreground media service architecture එක භාවිතා කරන්න.

Notification:

Album artwork

Song title

Artist

Previous

Play/Pause

Next

Close

Notification tap → Player screen.

Battery usage optimize කරන්න.

Network errors handle කරන්න.

==================================================
9. MINI PLAYER
==================================================

Main application screens වල bottom එකට persistent mini-player එකක් තිබිය යුතුය.

Mini-player:

Height approximately 64–72dp.

Left:

Album artwork

Middle:

Song title
Artist

Right:

Play/Pause

Optional:

Next

Swipe gesture:

User mini-player එක left/right swipe කළ විට previous/next track.

Tap:

Full Player Screen open කරන්න.

Animation:

Mini-player → Full player transition smooth shared-element-like animation.

==================================================
10. FULL PLAYER SCREEN
==================================================

Full Player Screen එක premium visual experience එකක් විය යුතුය.

Top:

Back button

More menu

Center:

Large album artwork

Below:

Song title

Artist

Progress bar

Current time

Remaining time

Controls:

Previous

Play/Pause

Next

Below:

Shuffle

Repeat

Queue

Favorite

Lyrics button architecture future-ready ලෙස තබන්න.

Album artwork එක square aspect ratio එකක් සහ rounded corners සහ subtle shadow එකක් සහිතව display කරන්න.

Background එක artwork එකෙන් dominant colors extract කරලා subtle blurred gradient එකක් generate කළ හැක.

IMPORTANT:

Original artwork crop/alter policies applicable නම් artwork එක distort/crop නොකරන්න.

==================================================
11. QUEUE
==================================================

Queue screen එක create කරන්න.

Show:

Currently playing

Up next

Queue items

Each queue item:

Drag handle

Artwork

Title

Artist

Remove button

User ට:

- reorder
- remove
- clear queue
- play next
- add to queue

හැකි විය යුතුය.

Queue state app restart එකකදී recover කළ හැකි architecture එකක් තබන්න.

==================================================
12. PLAYLIST SYSTEM
==================================================

Userට personal playlists create කරන්න.

Features:

Create playlist

Rename playlist

Delete playlist

Add track

Remove track

Reorder tracks

Playlist artwork

Playlist description

Track count

Total duration

Private/local playlist architecture.

Example playlists:

"මගේ Songs"

"Chill"

"Workout"

"Love Songs"

"Night Drive"

==================================================
13. FAVORITES
==================================================

Heart icon එකෙන් favorite/unfavorite කරන්න.

Favorites local database එකක save කරන්න.

Offline app open කළත් favorites show කරන්න.

Favorite state sync architecture future-ready කරන්න.

==================================================
14. RECENTLY PLAYED
==================================================

Recently Played list එක local database එකක store කරන්න.

Maximum:

100 items.

Same track repeatedly play කළොත් duplicate records endless ලෙස create නොකර update timestamp කරන්න.

Show:

Last played

Artist

Artwork

Track title

==================================================
15. LOCAL MUSIC
==================================================

Device එකේ legally owned audio files import/play කරන්න.

Supported formats:

MP3

M4A

AAC

WAV

FLAC where device supports it

User permissions Android version අනුව correctly request කරන්න.

Do not request unnecessary permissions.

Android modern storage architecture භාවිතා කරන්න.

Local audio scanner එක create කරන්න.

Userට:

- Local songs
- Local albums
- Local artists

වෙන් කර බලන්න පුළුවන්.

Local songs සඳහා offline playback fully support කරන්න.

==================================================
16. LEGAL DOWNLOAD SYSTEM
==================================================

Download system එක strictly authorized content සඳහා design කරන්න.

If backend response contains:

downloadAllowed = true

and

licenseStatus = authorized

then download button show කරන්න.

Otherwise:

Download button hide/disable කරන්න.

Instead show:

"මෙම audio එක download කිරීම සඳහා අවසර නොමැත."

User-owned/licensed audio files සඳහා download/export functionality support කරන්න.

Download manager:

- progress
- pause
- resume
- cancel
- completed
- failed
- retry

Downloads screen:

Downloading

Completed

Failed

Empty state.

File names sanitize කරන්න.

Example:

Artist - Song Title.mp3

Path:

Music/SONORA LK/

Do not overwrite existing files unintentionally.

==================================================
17. DOWNLOAD API SAFETY
==================================================

External API endpoint එකෙන් returned URL එක blindly trust නොකරන්න.

Validate:

HTTPS

Allowed host

Content-Type

File extension

Maximum file size

Redirect policy

Timeout

Checksum where available

Do not allow arbitrary URL downloading.

SSRF risk avoid කරන්න.

Backend එක URL validation enforce කළ යුතුය.

==================================================
18. API KEY SECURITY
==================================================

Never write:

api_key=xxxx

inside:

Kotlin source

Java source

XML

JSON

assets

BuildConfig exposed constants

GitHub repository

README

APK resources

Instead use server-side environment variable.

If Android app directly needs public client identifier, only public/non-sensitive identifier use කරන්න.

Private keys server side තබන්න.

==================================================
19. BACKEND PROXY
==================================================

Create optional Node.js backend.

Structure:

server/

src/

routes/

controllers/

services/

models/

middleware/

config/

utils/

API routes:

GET /api/spotify/track

GET /api/search

GET /api/recommendations

GET /api/health

GET /api/config

POST /api/player/validate

Backend එකේ:

Rate limiting

CORS

Helmet/security headers

Request validation

Timeout

Logging

Error handling

Caching

API key protection

implement කරන්න.

==================================================
20. DATABASE
==================================================

Local app database:

Room database preferred.

Tables:

tracks

artists

albums

playlists

playlist_tracks

favorites

recently_played

downloads

queue

settings

Database migrations properly handle කරන්න.

==================================================
21. OFFLINE MODE
==================================================

Network unavailable නම්:

- Local songs play කරන්න.
- Favorites show කරන්න.
- Playlists show කරන්න.
- Recently played show කරන්න.
- Cached metadata show කරන්න.

Network-dependent features සඳහා clear offline indicator එකක්.

Example:

"Offline Mode"

No crashes.

==================================================
22. NAVIGATION
==================================================

Bottom navigation:

Home

Search

Library

Downloads

Settings

Player එක bottom navigation එකට overlay වෙන mini-player architecture එකක් use කරන්න.

Navigation state preserve කරන්න.

Deep navigation:

Home → Track → Artist → Album → Player

Back button correct behavior.

==================================================
23. LIBRARY SCREEN
==================================================

Library screen:

Favorites

Playlists

Recently Played

Downloaded

Local Music

Artists

Albums

Cards / list hybrid UI.

Top tabs:

Music

Playlists

Downloads

Local

==================================================
24. SETTINGS
==================================================

Settings screen:

Appearance

Playback

Audio

Downloads

Storage

Notifications

Data usage

About

Privacy

Terms

Open Source Licenses

Clear cache

Reset app data

API status

Version

Developer information

==================================================
25. THEME
==================================================

Dark theme default.

Light theme optional.

Theme modes:

System

Dark

Light

Theme switch animation smooth.

==================================================
26. ANIMATIONS
==================================================

Use subtle animations.

Examples:

Album artwork scale

Play button morph

Mini-player expand

Screen fade

Card elevation

Search results appear

Skeleton shimmer

Queue reorder animation

Favorite heart animation

Do NOT over-animate.

Target:

60 FPS smooth experience.

==================================================
27. PERFORMANCE
==================================================

Optimize:

Image loading

Memory

Network

Database

Player

Recycler/Lazy lists

Caching

Avoid unnecessary recomposition/re-render.

Use image caching.

Use pagination for large lists.

Do not load 1000 images simultaneously.

==================================================
28. ERROR HANDLING
==================================================

Every network request must handle:

400

401

403

404

408

429

500

502

503

504

No internet

Timeout

Malformed JSON

Empty response

Server unavailable

API key invalid

Rate limit

Unknown error

User-friendly Sinhala messages.

Example:

"සම්බන්ධතාවය පරීක්ෂා කර නැවත උත්සාහ කරන්න."

"Server එක මේ මොහොතේ ලබාගත නොහැක."

"මෙම ගීතයේ තොරතුරු ලබාගත නොහැක."

==================================================
29. LOADING STATES
==================================================

Every screen needs skeleton loading.

Avoid blank white/black screens.

Home skeleton:

Greeting placeholder

Search placeholder

Horizontal cards

Track rows

==================================================
30. EMPTY STATES
==================================================

Favorites empty:

"ඔබ තවමත් ගීතයක් Favorite කරලා නැහැ."

Playlist empty:

"ඔබේ පළමු playlist එක නිර්මාණය කරන්න."

Downloads empty:

"Download කළ audio මෙහි පෙන්වනු ඇත."

Search empty:

"ගීතයක් සොයන්න."

==================================================
31. ACCESSIBILITY
==================================================

Buttons වල content descriptions තිබිය යුතුය.

Text contrast sufficient.

Touch targets minimum approximately 48dp.

Screen reader friendly.

Do not rely only on color.

==================================================
32. LANGUAGE SUPPORT
==================================================

Primary UI:

Sinhala + English friendly.

Create strings resources.

Do NOT hard-code UI text.

strings.xml

si/strings.xml

en/strings.xml

Future languages easily add කරන්න.

==================================================
33. SECURITY
==================================================

Implement:

Secure network communication

HTTPS only

Certificate validation

Input validation

Rate limiting

Secure storage

No secrets in Git

No API keys in APK

No logging sensitive credentials

No debug endpoints in production

Disable verbose logs in release build.

==================================================
34. GITHUB REPOSITORY
==================================================

Create repository:

test-key-app

Repository structure:

README.md

.gitignore

LICENSE

.github/

workflows/

android/

backend/

docs/

screenshots/

gradle/

settings.gradle

build.gradle

gradle.properties

Do NOT commit:

API keys

tokens

passwords

local.properties

.keystore

.env

release signing keys

Google service private files

==================================================
35. ENVIRONMENT VARIABLES
==================================================

Backend .env example:

CHAMA_API_BASE_URL=

CHAMA_API_KEY=

PORT=3000

NODE_ENV=production

Never place actual secret values inside repository.

Create:

.env.example

with placeholders.

==================================================
36. GITHUB ACTIONS
==================================================

GitHub Actions workflow create කරන්න.

File:

.github/workflows/android.yml

Workflow:

Checkout

Setup JDK

Setup Android SDK

Gradle cache

Build debug APK

Upload APK artifact

Optional release build

Build must fail if compilation errors exist.

Example conceptual pipeline:

push

→ checkout

→ Java setup

→ Android setup

→ gradle assembleDebug

→ upload artifact

==================================================
37. APK BUILD
==================================================

GitHub Actions successfully complete වූ පසු APK artifact generate විය යුතුය.

Expected output:

app-debug.apk

For production:

app-release.apk

Release signing සඳහා GitHub Secrets භාවිතා කරන්න.

Secrets:

ANDROID_KEYSTORE_BASE64

KEYSTORE_PASSWORD

KEY_ALIAS

KEY_PASSWORD

Do NOT commit keystore.

==================================================
38. RELEASE BUILD
==================================================

Production release build එක:

Minify

R8

Resource shrinking

ProGuard rules

Crash-safe configuration

No debug logs

No API secrets

Enable signing.

==================================================
39. README
==================================================

README එක professional ලෙස ලියන්න.

Include:

Project overview

Features

Architecture

Screenshots

Setup

Environment variables

Backend setup

Android build

GitHub Actions

APK build

Security

License

Contribution

Disclaimer

Do NOT include private API key.

==================================================
40. BRANDING
==================================================

Application name:

SONORA LK

Tagline:

"ඔබේ සංගීතය. ඔබේ මොහොත."

Alternative English tagline:

"Your Music. Your Moment."

Logo:

Minimal waveform icon.

Splash screen:

Dark background

Animated waveform

SONORA LK

"Your Music. Your Moment."

Splash duration:

short and non-blocking.

==================================================
41. HOME UI DETAILS
==================================================

Home screen visual hierarchy:

Header

Search

Recently Played

Trending

Recommended

Popular Artists

Your Playlists

Local Music

Bottom mini-player

Bottom navigation

Cards should use:

12–20dp radius

consistent spacing

high-quality artwork

No excessive borders.

==================================================
42. ARTIST SCREEN
==================================================

Artist screen:

Hero artwork/profile

Artist name

Verified indicator where applicable

Popular tracks

Albums

Singles

Related artists

Open Spotify button

Follow architecture future-ready.

==================================================
43. ALBUM SCREEN
==================================================

Album screen:

Album artwork

Album title

Artist

Release date

Track count

Play button

Shuffle button

Track list

Each track:

Number

Artwork

Title

Duration

More

==================================================
44. TRACK DETAIL
==================================================

Track details:

Artwork

Title

Artist

Album

Duration

Release date

Source

Open Spotify

Share

Favorite

Add playlist

Queue

Play preview if available and legally permitted.

==================================================
45. SHARE
==================================================

Share button:

Generate a clean share text.

Example:

Song Title
Artist Name

Listen on SONORA LK

Original Spotify link

Do not misrepresent SONORA LK as Spotify.

==================================================
46. SPOTIFY ATTRIBUTION
==================================================

If Spotify metadata/artwork is used, preserve required attribution and provide a link back to the relevant Spotify track/artist/album where required.

Do not modify protected artwork in a way that violates applicable requirements.

Do not use Spotify branding in a way that suggests SONORA LK is officially affiliated with Spotify.

Include an About/Attribution section.

==================================================
47. LEGAL COMPLIANCE
==================================================

The application must clearly separate:

Spotify metadata

Spotify preview playback where allowed

User-owned local music

Licensed audio

Unauthorized downloads

Never implement DRM bypass.

Never implement stream ripping.

Never claim that the application is an official Spotify client.

Never use Spotify logo as application logo.

Never impersonate Spotify.

==================================================
48. API CACHE
==================================================

Metadata caching:

Track metadata:

24 hours

Artist metadata:

24 hours

Album metadata:

24 hours

Search:

short cache

Do not cache protected audio unnecessarily.

Use ETag/conditional requests where available.

==================================================
49. NETWORK LAYER
==================================================

Create:

ApiClient

ApiService

ApiResult

NetworkError

Repository

Use clean architecture.

Flow:

UI

↓

ViewModel

↓

Repository

↓

RemoteDataSource / LocalDataSource

↓

API / Database

==================================================
50. STATE MANAGEMENT
==================================================

Use:

UiState

Loading

Success

Error

Empty

PlayerState

PlaybackState

DownloadState

NetworkState

Avoid random mutable global variables.

==================================================
51. TESTING
==================================================

Create tests for:

API parsing

Repository

Database

Playlist

Favorites

Queue

Player state

URL validation

Download permission logic

Error handling

UI state.

==================================================
52. UI TESTING
==================================================

Test:

Launch

Search

Open result

Play preview

Pause

Seek

Favorite

Create playlist

Add track

Open mini-player

Open full player

Back navigation

Offline mode

==================================================
53. CRASH PREVENTION
==================================================

No unhandled exceptions.

Handle:

Null

Malformed data

Missing image

Missing artist

Missing album

Missing duration

Missing audio URL

Network timeout

Database failure

Player failure.

Fallback artwork:

assets/default_album_art.png

==================================================
54. PLACEHOLDER ARTWORK
==================================================

Create original default album artwork.

Do not use copyrighted artwork as placeholder.

Design:

dark gradient

waveform

SONORA LK logo

==================================================
55. AUDIO ENGINE
==================================================

Use a reliable Android media player implementation.

Recommended modern architecture:

Jetpack Media3 / ExoPlayer.

Support progressive streaming for authorized audio.

Handle buffering.

Use MediaSession.

Integrate with Android system media controls.

==================================================
56. NOTIFICATION
==================================================

Notification design:

Album image

Track

Artist

Play/Pause

Previous

Next

Progress if supported

Notification channel:

SONORA_PLAYBACK

Do not show unnecessary persistent notifications when no playback is active.

==================================================
57. APP STARTUP
==================================================

Startup flow:

Splash

↓

Load local settings

↓

Initialize database

↓

Initialize player

↓

Check cached data

↓

Load Home

Avoid blocking startup with unnecessary network calls.

==================================================
58. FIRST LAUNCH
==================================================

First launch onboarding:

Page 1:

"ඔබේ සංගීත ලෝකයට සාදරයෙන් පිළිගනිමු."

Page 2:

"ඔබේ playlists සහ favorites එක තැනක."

Page 3:

"ඔබේ device එකේ ඇති music එකත් අහන්න."

Button:

"ආරම්භ කරමු"

Permissions only when necessary.

==================================================
59. SETTINGS PLAYBACK
==================================================

Options:

Gapless playback

Auto play

Resume playback

Skip silence future-ready

Crossfade future-ready

Audio quality

Wi-Fi only for network audio

Download settings for authorized content only.

==================================================
60. QUALITY OPTIONS
==================================================

If backend provides multiple authorized quality options:

Low

Medium

High

320kbps where legally licensed.

Do not expose "320kbps" as a generic Spotify ripping option.

==================================================
61. SEARCH PERFORMANCE
==================================================

Search request:

300–500ms debounce.

Cancel previous request when new query arrives.

Minimum query:

2 characters.

Search history:

store last 20 searches.

Clear search history option.

==================================================
62. HOME PERSONALIZATION
==================================================

Use local listening behavior to improve UI ordering.

Do not upload personal listening history unnecessarily.

Recommendations should use only data for which the application has proper permission/legal basis.

==================================================
63. DATA PRIVACY
==================================================

Settings → Privacy.

Explain:

What data is stored

Where data is stored

How API requests work

What data is sent to backend

How to clear data.

==================================================
64. DELETE DATA
==================================================

Button:

"Clear all local data"

Confirmation dialog.

Clear:

favorites

recent history

playlists

queue

cached metadata

settings where appropriate

Do not delete actual user music files unless user explicitly chooses.

==================================================
65. DESIGN DETAILS
==================================================

Typography:

Use a clean modern font.

Headings:

Bold

Body:

Regular

Metadata:

Medium

Spacing system:

4

8

12

16

20

24

32

40

Use consistent margins.

==================================================
66. RESPONSIVE DESIGN
==================================================

Different Android screen sizes support කරන්න.

Small phones

Normal phones

Large phones

Tablet-ready architecture.

Avoid hardcoded absolute positioning.

==================================================
67. ROTATION
==================================================

If orientation changes are supported:

Player state must remain.

Search query remains.

Scroll position reasonable.

Playback continues.

==================================================
68. ACCESSIBILITY COLORS
==================================================

Do not use low-contrast gray text.

Interactive controls must be clearly visible.

Favorite active state should use icon + state semantics.

==================================================
69. APP ICON
==================================================

Generate original SONORA LK app icon.

Concept:

Circular dark background

Abstract waveform

Subtle neon gradient

Minimal

No Spotify logo.

Adaptive icon support.

==================================================
70. SPLASH
==================================================

Splash:

SONORA LK

Waveform animation

Dark premium background

No long splash delay.

==================================================
71. SECURITY AUDIT
==================================================

Before final build, scan repository for:

api_key

apikey

token

password

secret

Bearer

ghp_

github_pat_

private_key

client_secret

Remove all secrets.

IMPORTANT:

Never commit any GitHub Personal Access Token.

If a token was previously exposed, revoke it immediately and generate a replacement.

==================================================
72. GITHUB TOKEN
==================================================

For repository push, use GitHub authentication securely through GitHub CLI or environment credentials.

Never place a GitHub token inside:

README

source code

workflow YAML

Gradle

.env committed files

logs

screenshots

prompt output.

If authentication is unavailable, stop and clearly tell the user that repository push requires a connected GitHub account/authenticated environment.

Do not fabricate a successful push.

==================================================
73. REPOSITORY NAME
==================================================

Repository:

test-key-app

Description:

"SONORA LK — Premium Music Discovery and Personal Audio Player"

Make repository structure clean and professional.

==================================================
74. GITHUB ACTIONS APK
==================================================

Create:

.github/workflows/build-apk.yml

Trigger:

push

pull_request

manual workflow_dispatch

Build:

./gradlew assembleDebug

Then upload:

app/build/outputs/apk/debug/app-debug.apk

as GitHub Actions artifact.

Optional release workflow:

./gradlew assembleRelease

Only if signing secrets are configured.

==================================================
75. FINAL BUILD CHECK
==================================================

Before claiming completion:

1. Compile project.
2. Run unit tests.
3. Run lint.
4. Check API parsing.
5. Check navigation.
6. Check player.
7. Check background playback.
8. Check notification.
9. Check local audio.
10. Check playlist.
11. Check favorites.
12. Check downloads permission logic.
13. Check API security.
14. Search repository for secrets.
15. Build APK.
16. Verify APK exists.
17. Verify GitHub Actions workflow.
18. Verify README.
19. Verify .gitignore.
20. Verify no private credentials are committed.

==================================================
76. USER EXPERIENCE TARGET
==================================================

The application should feel:

Fast

Premium

Modern

Smooth

Minimal

Reliable

Professional

The user should be able to open the app and understand the interface within seconds.

Avoid unnecessary popups.

Avoid excessive advertisements.

Avoid confusing menus.

Keep important controls accessible.

==================================================
77. MAIN USER FLOW
==================================================

User opens app

↓

Splash

↓

Home

↓

Search

↓

Enter song/artist

↓

Results

↓

Tap track

↓

Track details

↓

Play authorized preview/audio

↓

Mini player appears

↓

User navigates elsewhere

↓

Playback continues where permitted

↓

User opens full player

↓

Controls playback

↓

Adds favorite

↓

Adds to playlist

↓

Returns to library

↓

Playlist visible

This entire flow must feel smooth.

==================================================
78. LOCAL MUSIC FLOW
==================================================

Library

↓

Local Music

↓

Scan/import

↓

Songs

↓

Tap song

↓

Play

↓

Background playback

↓

Notification

↓

Lock screen

All local playback should work without internet.

==================================================
79. DOWNLOAD FLOW
==================================================

Track

↓

Check authorization

↓

downloadAllowed?

YES:

Show download option.

NO:

Hide or disable download.

If YES:

Download

↓

Progress

↓

Completed

↓

Saved to Music/SONORA LK/

↓

Available offline.

==================================================
80. ERROR FLOW
==================================================

API unavailable

↓

Show friendly error

↓

Retry

If still unavailable:

Use cached metadata if available.

Never crash.

==================================================
81. FINAL UI QUALITY
==================================================

Before completion inspect every screen visually.

Check:

Alignment

Spacing

Typography

Icons

Overflow

Long titles

Long artist names

Missing images

Dark mode

Light mode

Small screens

Large screens

Loading

Error

Empty states.

==================================================
82. DOCUMENTATION
==================================================

Create:

docs/architecture.md

docs/api.md

docs/build.md

docs/security.md

docs/github-actions.md

docs/legal.md

==================================================
83. LEGAL DISCLAIMER
==================================================

About screen should clearly state:

"SONORA LK is an independent music application and is not affiliated with Spotify."

Where Spotify metadata is used:

"Spotify and related trademarks belong to their respective owners."

Only authorized/licensed audio should be downloaded or streamed through external services.

==================================================
84. FINAL DELIVERABLE
==================================================

Final project must contain:

Android app

Backend

API abstraction

Room database

Media3 player

Background playback

Notification controls

Mini player

Full player

Search

Track details

Artist screen

Album screen

Favorites

Playlists

Recently played

Local music

Authorized downloads

Settings

Dark/light theme

Sinhala/English localization

Security layer

GitHub Actions

README

Documentation

Tests

==================================================
85. IMPORTANT IMPLEMENTATION RULE
==================================================

Do not simply create a visual mockup.

Create a real functional application.

Buttons must actually work.

Navigation must work.

API requests must work.

Player must work.

Database must work.

Background playback must work.

GitHub Actions must build the project.

If an API response is unknown, first inspect the response shape and then create a robust mapper rather than assuming fields.

If an external endpoint fails, implement proper fallback/error handling rather than fake data.

Do not pretend a feature works when it does not.

==================================================
86. DEVELOPMENT ORDER
==================================================

Build in this order:

PHASE 1:

Project setup

Theme

Navigation

Home UI

PHASE 2:

API client

Track model

Search

Track details

PHASE 3:

Media3 player

Mini-player

Full-player

Background playback

Notification

PHASE 4:

Room database

Favorites

Recently played

Playlists

Queue

PHASE 5:

Local music

Offline mode

PHASE 6:

Authorized download manager

PHASE 7:

Settings

Localization

Privacy

PHASE 8:

Security

Testing

Performance

PHASE 9:

GitHub Actions

APK build

Documentation

PHASE 10:

Final QA

==================================================
87. DO NOT USE FAKE COMPLETION
==================================================

If build fails:

show actual error

fix it

rebuild

If API fails:

show actual API error

fix integration

If GitHub push cannot be performed:

do not claim that it was pushed.

If APK cannot be generated:

do not claim that APK exists.

If a required secret is missing:

request it through secure environment configuration, never ask user to paste private tokens into source code.

==================================================
88. FINAL OUTPUT FROM THE CODING AGENT
==================================================

At completion provide:

1. Project name
2. Repository name
3. Technology stack
4. Main features
5. API integration status
6. Security status
7. Test status
8. Build status
9. APK artifact path
10. GitHub Actions status
11. Known limitations
12. Setup instructions

Do not expose private credentials.

==================================================
89. FINAL PRODUCT VISION
==================================================

SONORA LK should look like a real commercial-quality music application.

The first impression should be:

"මේක professional music app එකක්."

The interface should not look like a beginner project.

Use:

Premium spacing

Beautiful album cards

Smooth transitions

Modern player

Persistent mini-player

Elegant dark theme

Fast search

Clean typography

High-quality icons

Professional empty states

Professional error states.

==================================================
90. FINAL COMMAND
==================================================

දැන් මෙම සම්පූර්ණ specification එක අනුගමනය කරමින් SONORA LK Android application එක production-quality architecture එකකට build කරන්න.

මුලින් project structure එක analyze කරලා architecture එක establish කරන්න.

ඊට පස්සේ UI → API → player → database → downloads → security → GitHub Actions → APK build යන order එකෙන් implement කරන්න.

Existing code තිබේ නම් blindly overwrite නොකර inspect කරලා improve කරන්න.

All sensitive credentials environment variables වලට move කරන්න.

GitHub repository එකේ repository name එක:

test-key-app

විය යුතුය.

GitHub Actions මඟින් APK build වන workflow එක අනිවාර්යයෙන් ඇතුළත් කරන්න.

APK එක GitHub Actions artifact එකක් ලෙස ලබාගත හැකි වන ලෙස configure කරන්න.

අවසානයේ compilation errors, runtime errors, API errors සහ UI issues තිබේ නම් ඒවා fix කරලා final build එක verify කරන්න.

IMPORTANT FINAL SAFETY RULE:

Spotify copyrighted audio සඳහා unauthorized downloading, stream ripping, DRM bypassing හෝ Spotify restrictions bypass කිරීම implement නොකරන්න.

Spotify metadata සහ legally permitted previews සඳහා පමණක් appropriate integration එක භාවිතා කරන්න.

User-owned / licensed audio සඳහා offline playback සහ authorized download functionality ලබාදෙන්න.

Application එක Spotify official app එකක් ලෙස represent නොකරන්න.

SONORA LK එක independent product එකක් ලෙස maintain කරන්න.

FINAL GOAL:

"Premium Spotify-inspired music discovery + personal/local music player experience, with secure API architecture, beautiful UI, background playback, playlists, favorites, offline local music, authorized downloads, GitHub Actions APK builds, and production-ready code."