# MANIFEST — sources, licences, how each was used

Researched on 2026-11-10. Everything below is a **reference**: structure, naming and timing
were extracted and rewritten for Android/Compose. No artwork, code or brand assets were
copied into this pack.

| # | Site | What it is | What was taken | Licence / access |
|---|---|---|---|---|
| 1 | 21st.dev | Registry of 12,000+ community React/shadcn components | Component archetypes, registry model, shader/liquid-glass names | Community, per-component; free browsing |
| 2 | reactbits.dev | Open-source animated components (33 text, 38 animations, 48 components, micro) | The full named-effect vocabulary + Compose translations | Free & open source |
| 3 | magicui.design | 150+ animated React components | Progressive blur, number ticker, blur fade, bento, shine border | MIT |
| 4 | uiverse.io | Community UI elements (buttons, switches, loaders, inputs) | Micro-interaction timings (toggle overshoot, press squish, shimmer) | Free personal & commercial (per element) |
| 5 | mobbin.com | Library of real shipped apps, flows and patterns | Flow structures, premium pattern conventions, evidence discipline | Reference; paid tiers for full access |
| 6 | 60fps.design | Curated interaction details from best-in-class apps | The gesture/pattern/effect/element taxonomy + the ten defining details | Reference only |
| 7 | land-book.com | Curated website gallery with filters | Big-type hero, poster footer, editorial rhythm, hairline system | Reference; PRO for sections/motion |
| 8 | landdding.com | Fresh landing pages & templates | Current landing structures (studio grid, fintech, launch page) | Reference only |
| 9 | recent.design | Design inspiration feed | Weekly-extraction habit, signal/noise filtering | Reference only |
| 10 | godly.website | Tightly curated best-of web design (now resolves to Recent) | Ambition calibration: one thing done extremely well | Reference only |
| 11 | motionsites.ai | AI website prompts + motion inspiration | The pinned-axis prompt pattern | Reference only |
| 12 | fontinlogo.com | Typefaces behind famous brands + free alternatives | Display-vs-body pairing logic, free font shortlist | Reference only |
| 13 | vibrant.design | Curated sections, shaders and UI shots | Glass toggle, genie frosted menu, clamped slider, mechanical counter | Reference only |

## 14th source — nexu-io/open-design (Apache-2.0)
https://github.com/nexu-io/open-design — an open, agent-native design workspace and the
closest open analogue to Claude Code for design work. Its `craft/` folder is the most
rigorously sourced design-discipline material in the ecosystem: motion rules grounded in
Tversky/Morrison/Bétrancourt (IJHCS 2002), Harrison/Yeo/Hudson (CHI 2010) and Material 3
tokens; colour pixel budgets; typography hierarchy vectors; five-state coverage; the seven
P0 anti-slop sins as lintable rules; and a design-system package contract
(`manifest.json` + `DESIGN.md` + `tokens.css`).

**What was taken (adapted, not copied):** the motion-discipline reasoning and its corrected
numbers, the accent/neutral pixel budgets, the five-state requirement and edge matrix, the
typography hierarchy vectors, the P0 lint list, and the package-contract idea now shipped
in `design-system/`. **What was left behind:** the daemon/app architecture, the brand
catalogue of third-party design systems, and its TypeScript schemas (this pack stays
dependency-free).

## How it was used
- **sites/** — one file per source: what it is, what to steal, what to ignore, and the
  Android translation. Written from the live pages, not from memory.
- **motion/** — the component names from React Bits and Magic UI were used as the
  vocabulary; the timings were normalised into one token scale (120–600ms, spring-driven)
  so an app never mixes a 1.2s landing-page animation with a 120ms tap response.
- **colour/** — the palettes are the a0 tokens plus warm, non-generic alternates; every
  banned hue from the brief is enforced in `colour-system.md`.
- **components/** — a build menu with Compose translations and a P0/P1/P2 priority so the
  first version ships the pieces that carry the premium feel.
- **prompts/** — the four packs pin every axis (surface, layout, type, palette, two
  motions, bans) because that is what stops a model from producing the average.

## If you republish this pack
Keep this manifest with it. The reference sites are other people's work; the synthesis,
timings and Compose translations are original.
