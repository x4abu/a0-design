# DESIGN.md — <PROJECT NAME>

> Ship this inside the project, next to `manifest.json` and `tokens.css`. The next session
> (human or agent) reads this first and stays on brand.

## Theme
<One line: the mood in plain words.> Palette `<id>` · type pairing `<id>` · layout
`<archetype>` · motion `<motif1 + motif2>` · one bold move: `<move>`.

## Colour (roles only — never raw hex in components)
| Role | Value | Share of pixels |
|---|---|---|
| bg | `#______` → `#______` | 70–90% with neutrals |
| surface | `#FFFFFF` | |
| ink / ink-soft / faint | `#______` / `#______` / `#______` | |
| hairline | `#______` | |
| accent | `#______` | 5–10%, **max 2 uses per screen** |
| accent-2 | `#______` | gradients only |
| accent-ink | `#______` | |
| tint | accent @ 12% | |
| success / warning / danger | `#______` / `#______` / `#______` | 0–5%, states only |

Banned: purple, violet, indigo (`#6366F1`), blue, neon, pure black/white, coloured glows.

## Type
| Role | Family | Size / line / tracking | Vector |
|---|---|---|---|
| Display | <serif or grotesk> | 42 / 45 / −0.03em | scale + tracking |
| Title | same | 34 / 37 / −0.04em | |
| Body | <Inter Tight> | 15 / 23 / 0 | |
| Small / caption | same | 14 / 20 · 12 / 16 | colour only |
| Mono | <Geist Mono> | 12 / 18 | |

Ratio display ÷ body ≥ 2.5×. One display voice with personality, one UI voice.

## Shape & space
Radii: card 28 · field 20 · chip pill · inner tile 14 — **two per screen**.
Spacing: 4 · 8 · 12 · 16 · 24 · 32 · 48. Side padding 20–24. Inside padding > outside gap.

## Motion
feedback 120 · state 150 · short 200 · base 250 · large 450 · sheet 600. Mobile runs
20–30% shorter than the web equivalent. Curves for opacity/colour, springs for position.
Motifs: `<name them>`. One ambient loop maximum. Reduced motion strips transforms, keeps
crossfades.

## Components
- Primary CTA: pill 54dp, accent gradient or ink, press .96
- Card: 28dp, one elevation language (shadow OR hairline)
- Toggle: 52×32, knob 26, 450ms settle, track crossfade 300ms
- Nav: floating glass pill, active item expands with label
- Input: 20dp, 1dp hairline, accent focus ring

## Budget (tick off while building)
```
accent 0/2 · gradient 0/1 · blur 0/1 · shadows 0/3 · radii 0/2 · motion 0/2 · loops 0/1
```

## Avoid (this project specifically rejects)
1. <ban> 2. <ban> 3. <ban>

## Decisions log
| Date | Decision | Why |
|---|---|---|
| | | |
