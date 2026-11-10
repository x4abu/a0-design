# DESIGN.md — <PROJECT NAME>

> One file, shipped inside the project. The next session (human or agent) reads this first
> and stays on brand.

## Theme
<One line: the mood, in plain words.> Palette locked: `<palette id>`.

## Colour (roles only)
| Role | Value |
|---|---|
| bg | `#______` → `#______` |
| surface | `#FFFFFF` |
| ink / ink-soft / faint | `#______` / `#______` / `#______` |
| hairline | `#______` |
| accent / accent-2 / accent-ink | `#______` / `#______` / `#______` |
| tint | accent @ 12% |
| success / warning / error | `#______` / `#______` / `#______` |

Banned: purple, violet, indigo, blue, neon, pure black/white.

## Type
| Role | Family | Size / line / tracking |
|---|---|---|
| Display | <serif or grotesk> | 34 / 37 / −0.03em |
| UI body | <Inter Tight / Manrope> | 15 / 23 / 0 |
| Caption | same | 12 / 16 |
| Mono | <Geist Mono> | 12 / 18 |

## Shape & space
Radii: card 28 · field 20 · chip pill · inner tile 14. Two per screen.
Spacing: 4 · 8 · 12 · 16 · 24 · 32 · 48. Screen padding 20–24.

## Motion
feedback 120 · short 200 · base 250 · large 450 · sheet 600.
Springs: settle (0.85/380) · snappy (0.9/900). One orchestrated moment per screen.
Named effects used: <e.g. blur-text entrance, expanding nav pill, count-up>.

## Components
- Primary CTA: pill 54dp, accent gradient, ink text, press .96
- Card: white 28dp, ink-tinted shadow 0 14 30 −20 @40%
- Toggle: 52×32, knob 26, 450ms settle, track crossfade 300ms
- Nav: floating glass pill, active item expands with label
- Input: 20dp radius, 1dp hairline, accent focus ring

## Avoid (this project specifically rejects)
1. <ban> 2. <ban> 3. <ban>

## Decisions log
| Date | Decision | Why |
|---|---|---|
| | | |
