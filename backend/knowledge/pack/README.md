# a0 design knowledge pack — v2 (with the taste layer)

Everything I pulled from the 13 reference sites you listed, turned into something a model
can actually act on: named motion recipes with real timings, locked palettes with banned
hues, a component menu mapped to Jetpack Compose, and prompt packs for Android, web app
and website work.

**Drop this into the backend** (`backend/knowledge/pack/`) or paste individual files into
AI Studio's instructions. Nothing here needs a network call to use.

## v2 — what changed

The v1 pack was strong on *rules* and weak on *taste*: a model could follow every rule and
still produce something correct-but-forgettable, and it would reach for the same two
favourites every time. v2 fixes that with four additions.

1. **`taste/`** — the missing half.
   - `taste-tests.md` — eight pass/fail tests (accent removal, blind identity, loudness
     count, one memorable thing, squint, type, motion justification, restraint audit).
   - `restraint-budget.md` — hard per-screen budgets: accent 2, gradient 1, blur 1,
     shadows 3, radii 2, motion 2, loops 1.
   - `deal-not-choose.md` + `scripts/deal.py` — deal a hand (palette × type × layout ×
     motion × bold move) so output varies per project instead of collapsing into one house
     style, and stays stable for a given project.
   - `composition.md` — grid, optical alignment, whitespace ratios, density, image
     direction, rhythm: the mechanics rules cannot teach.
   - `copy-voice.md` — naming, microcopy shapes, banned strings, the read-aloud test.
   - `critique-rubric.md` — 0–80 across eight categories, the genericness test, and eight
     named one-move fixes instead of "make it better".
2. **`craft/`** — seven discipline docs adapted from [nexu-io/open-design]
   (https://github.com/nexu-io/open-design, Apache-2.0), which grounds motion in primary
   research rather than vibes: animate only for space/time/state, 150ms is the converged
   state default, curve for opacity and spring for position, Apple's real spring is
   (response 0.5, damping 0.825), M3 standard is `cubic-bezier(.2,0,0,1)` not the M2 curve,
   flashing and loop limits, plus colour discipline (accent ≤2 uses, 70–90% neutrals),
   typography hierarchy vectors, five-state coverage, form validation, laws of UX and an
   accessibility baseline.
3. **`design-system/`** — a real package contract: `DESIGN.md` template, `manifest.json`
   example with budgets and a11y floors, and `tokens.example.css` as the single source of
   truth (roles, radii, spacing, type, motion, ink-tinted elevation, reduced-motion media
   query).
4. **`craft/anti-slop-lint.md`** — the seven P0 sins as machine-checkable rules, including
   two my v1 missed: the rounded card with a coloured left-border accent, and sans-serif
   display text when the theme binds a serif.

## What is inside

```
00-START-HERE.md          reading order + the non-negotiables (start here)
taste/                    6 files — the taste layer (tests, budget, deal, composition,
                          copy voice, critique rubric)
craft/                    8 files — motion, colour, type, states, forms, UX laws, a11y, lint
sites/                    13 files — what each site is, what to steal, what to ignore
motion/                   8 files + compose-recipes.kt
  motion-tokens.md          durations, easings, springs, the seven rules
  text-animations.md        12 text effects with exact specs
  toggle-and-control.md     toggles, segmented, chips, sliders, buttons
  navigation.md             bottom bar, top bar, drawer, tabs, shared element
  cards-and-surfaces.md     cards, sheets, dialogs, glass, toasts, empty states
  page-transitions.md       push/pop/present/tab/splash/shared-element
  scroll-and-parallax.md    progressive blur, parallax, reveal, tilt, pull-to-refresh
  loaders-and-skeletons.md  shimmer, spinners, progress, success/error, ambient
  compose-recipes.kt        copy-paste Compose implementations
colour/                   3 files — roles, 7 shippable palettes, grading recipes
components/               the build menu: component → source → Compose → priority
prompts/                  4 prompt packs (android, web app, website, motion-only)
design-md/DESIGN.template.md   the handoff file every project should ship
design-system/            DESIGN.md template, manifest example, tokens.css example
scripts/deal.py           deterministic hand dealer (no dependencies)
json/                     motion.json, palettes.json, components.json, deal.json
MANIFEST.md               sources, licences and how each was used
```

## The five rules that matter more than any single component
1. **Deal a hand, then justify it.** Variety is part of taste; one house style for every
   product is a template.
2. **Accent ≤2 uses, one CTA, two radii.** Everything else is a tint, a hairline or air.
3. **Springs for touch, curves for colour.** 450ms settle, 2–4% overshoot, nothing over
   500ms that is not a screen transition.
4. **One orchestrated moment per screen.** The rest stays still — that is what makes the
   moment read as expensive.
5. **Ship all five states.** Loading, empty, error, populated, edge. The populated state
   alone is the most reliable sign of generated UI.

## Honest notes
- These sites are *references*, not assets. Copy structure and timing, never artwork,
  shaders or brand marks.
- React/WebGL components (React Bits backgrounds, Magic UI shaders, 21st shaders) do not
  port to Android as-is. The pack tells you what the phone equivalent is, and when to skip.
- Timings are the ones these libraries actually use (200–600ms, spring-driven). If a
  model proposes 1s+ transitions, it is wrong.
