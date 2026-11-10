# a0 design knowledge pack

Everything I pulled from the 13 reference sites you listed, turned into something a model
can actually act on: named motion recipes with real timings, locked palettes with banned
hues, a component menu mapped to Jetpack Compose, and prompt packs for Android, web app
and website work.

**Drop this into the backend** (`backend/knowledge/pack/`) or paste individual files into
AI Studio's instructions. Nothing here needs a network call to use.

## What is inside

```
00-START-HERE.md          how an agent should read this pack (read this first)
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
json/                     motion.json, palettes.json, components.json (machine-readable)
MANIFEST.md               sources, licences and how each was used
```

## The three rules that matter more than any single component
1. **One accent, one CTA, two radii.** Everything else is a tint, a hairline or air.
2. **Springs, not easings, for anything a finger touches.** 450ms settle, 2–4% overshoot.
3. **One orchestrated moment per screen.** The rest stays still — that is what makes the
   one moment read as expensive.

## Honest notes
- These sites are *references*, not assets. Copy structure and timing, never artwork,
  shaders or brand marks.
- React/WebGL components (React Bits backgrounds, Magic UI shaders, 21st shaders) do not
  port to Android as-is. The pack tells you what the phone equivalent is, and when to skip.
- Timings are the ones these libraries actually use (200–600ms, spring-driven). If a
  model proposes 1s+ transitions, it is wrong.
