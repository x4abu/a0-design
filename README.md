# a0 design

An AI design studio for Android — rebuilt, screen for screen, from the `a0` HTML
prototype as a native Kotlin + Jetpack Compose app, with a backend that carries the
Design Brain and a deterministic anti-AI-slop engine.

**It ships with no content and no model of its own.** There is no demo project, no
sample usage, no fake catalogue. You add your own provider key (OpenRouter, OpenAI or
Anthropic); the app verifies it against the provider before storing it, then designs
for real. Without a key it says so and sends you to the key screen — it never fakes a
result.

| | |
|---|---|
| App | Kotlin 2.0 · Jetpack Compose · Material 3 · minSdk 26 · targetSdk 35 |
| Backend | FastAPI · Pydantic v2 · httpx (bring your own model key) |
| Design system | Lime + white pastel theme, 3 bundled typefaces, 1:1 tokens from the prototype |
| Guardrail | 15 deterministic anti-slop checks + 36 hard bans, enforced in code |
| CI | GitHub Actions → `assembleDebug` → APK artifact (+ release on tag) |

## Start here

1. Open the app → **Settings → API keys**.
2. Paste an OpenRouter, OpenAI or Anthropic key. It is checked against the provider
   (a real authenticated request) and only then encrypted into the Android Keystore.
3. Go to **Studio**, describe the product, and send.
4. The design lands in **Projects** with a version history; token usage is counted from
   the `usage` block the provider actually returned.

No key → the Studio shows a card explaining that nothing can be generated, with a
button to the key screen. Server-side, `POST /api/generate` answers **409** with the
same message instead of inventing a design.

## What's inside

```
android/                 Jetpack Compose app (7 screens, mascot, all animations)
  app/src/main/java/design/a0/app/
    ui/theme/            tokens + type + live design-system state
    ui/comp/             glass surfaces, buttons, chips, segmented, toggle, field, toast
    ui/mascot/           the glossy sphere: moods, blink, bob, particles (pure Canvas)
    ui/screens/          Studio (chat), Preview, Projects, Explore, Usage, Settings, API keys
    ui/preview/          the editable design inside the phone / tablet / web frame
    ui/sheets/           drawer, bottom nav, model / attach / export / action sheets
    vm/                  one state holder mirroring the prototype's JS state
    net/                 backend client (degrades honestly when offline)
backend/                 FastAPI: Design Brain + anti-slop + generate/review/steer
  app/antislop.py        the 15 checks and the banned-pattern scanner
  app/brain.py           context assembly in the Part 0 §8 order
  knowledge/             Design Brain, tokens, themes, 9 design-skill documents
knowledge/design_brain.md  the single-file Design Brain, markdown edition
.github/workflows/       backend tests → APK build → artifact
```

## The screens

Everything from the prototype is here — no screen skipped:

1. **Studio** — hero mascot, typing greeting, three prompt suggestions, the feed
   (user bubbles, typewriter replies, plan questions, the six-step generation card,
   stop control, ready card), and the composer with attachments, Auto/Plan, model
   picker and the mic/send morph.
2. **Preview** — device frame with three designed screens, element selection with the
   quick-edit toolbar and annotation, plus four panes: Canvas (pan/zoom frames and
   variants), Code (Compose / React / HTML), Style (accent, typeface, roundness, mode,
   fidelity, contrast chip), History (compare / restore).
3. **Projects** — stat cards, drafts, exports, collections, recent designs.
4. **Explore** — filters, trending styles, new designs rail.
5. **Usage** — monthly estimate, per-provider and per-project bars.
6. **Settings** — ten rows with pastel tiles, toggles, font size, language, system prompt.
7. **API keys** — provider chips, key field with reveal, test connection.

Plus the drawer, the floating pill navigation (active item expands into a dark capsule),
all four bottom sheets and the top toast.

## Motion

The prototype's CSS keyframes are reproduced as Compose animation: 4s bob, 5s blink
(93 % → 96 %), think eye-sweep, happy jump with heart/star particles, curious tilt,
typing eyes-down, sad tilt, sleep breathing. Screens enter with fade + slide + scale;
sheets use the `cubic-bezier(.32,.72,0,1)` settle; segmented thumbs slide on a spring.

## Backend

```bash
cd backend
pip install -r requirements.txt
uvicorn app.main:app --reload      # http://127.0.0.1:8000/docs
pytest -q                          # 6 tests, no network needed
```

| Endpoint | What it does |
|---|---|
| `GET /health` | liveness + how many brain parts and skills are loaded |
| `GET /api/brain/sections` | the parts of the Design Brain |
| `GET /api/brain/{part}` | one part (`PART 0` … `PART 12`) |
| `POST /api/brain/context` | assembles the exact prompt context for a brief |
| `POST /api/antislop/check` | the 15 checks → PASS/WARN/FAIL + verdict |
| `POST /api/generate` | Design Brain context → model (your key) → spec JSON · **409 if no key** |
| `POST /api/review` | deterministic audit + the design-critic prompt |
| `POST /api/steer` | named one-move fixes (distill, bolder, quieter, …) |
| `GET /api/skills` | the 9 design-skill documents with licences |

Without a key the server refuses with **409** and a message naming the two ways to
provide one (Bearer header from the app, or `OPENROUTER_API_KEY` / `OPENAI_API_KEY` /
`ANTHROPIC_API_KEY` on the server). It never invents model output. `render.yaml` and
`Dockerfile` are included.

The app has two engines, both real: **Direct** (default — the device calls the provider
with your key) and **Backend** (set the URL in API keys; the server assembles the full
Design Brain first).

Point the app at your deployment with one line in
`android/app/build.gradle.kts` → `A0_BACKEND_URL`.

## Build the APK

Locally:

```bash
cd android
gradle assembleDebug          # needs JDK 17 + Android SDK 35
# → app/build/outputs/apk/debug/app-debug.apk
```

Or let CI do it: push to `main` and download the `a0-design-apk` artifact from the
workflow run. Push a `v*` tag to attach the APK to a GitHub release.

## What is stored where

| Data | Where |
|---|---|
| API keys | Android Keystore, AES/GCM encrypted, never leaves the device |
| Projects + versions + usage | App-private SharedPreferences (JSON) |
| Model catalogue | Fetched live from OpenRouter's public `/models` endpoint |
| Designs | Returned by the model you chose, parsed as a strict JSON spec |

Settings → *Privacy — clear local data* deletes all of it, keys included.

## Design knowledge pack (13 sites → one rulebook)

`backend/knowledge/pack/` holds 38 documents distilled from the reference sites the brief
named: 21st.dev, React Bits, Magic UI, Uiverse, Mobbin, 60fps.design, Land-book, Landdding,
Recent.design, Godly, MotionSites, FontInLogo, Vibrant.design.

- `motion/` — one token table (120–600ms, spring-driven) plus named recipes for text,
  toggles, navigation, cards, page transitions, scroll/parallax and loaders, and
  `compose-recipes.kt` with copy-paste implementations.
- `colour/` — role-based colour system, seven shippable palettes, and grading recipes.
- `components/` — the build menu: component → source site → Compose translation → priority.
- `prompts/` — prompt packs for Android, web app, website and motion-only passes.
- `json/` — machine-readable `motion.json`, `palettes.json`, `components.json`.

The backend serves it (`GET /api/pack`, `/api/pack/motion`, `/api/pack/palettes`,
`/api/pack/{path}`) and injects the motion + colour + component contract into every
generation prompt via `app/pack.py`, so the model cannot invent a 1.2s transition or a
purple gradient.

## Design knowledge

`knowledge/design_brain.md` is the full Design Brain (master rules, tokens, 36 bans +
15 checks, themes, component vault, 114 screen archetypes, UX laws, 14 flows, motion,
assets, the GitHub design-skills collection, the review loop, prompt templates).
`backend/knowledge/skills/` distils nine sources with licences and links — Anthropic's
frontend-design skill, the ui-ux-pro-max pipeline, taste-skill's dealing, impeccable's
review loop, emilkowalski's motion work, Lovable-style prompting, Penpot/Onlook's
tokens-as-truth model, the DESIGN.md format and an accessibility baseline.

## Licence

MIT for this repository. Bundled fonts keep their own licences (DM Sans, Instrument
Serif and Geist Mono are all SIL Open Font Licence); third-party design knowledge is
attributed in `backend/knowledge/skills.json`.
