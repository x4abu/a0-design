# Colour system

## The rule that does most of the work
One warm neutral canvas, one ink, **one accent**. Everything else is a tint of one of those
three. If a screen needs a second loud colour, it needs a better hierarchy instead.

## Roles (use these names, never raw hex in code)
| Role | Job | a0 value |
|---|---|---|
| bg | page canvas | `#F8FBF4` → `#E7F0DE` |
| surface | cards, sheets | `#FFFFFF` |
| surface-glass | floating chrome | white @ 82% |
| ink | primary text | `#14171A` |
| ink-soft | secondary text | `#3D4640` |
| ink-faint | captions, placeholders | `#79837C` |
| hairline | 1px separation | `#E4EADF` |
| accent | one loud thing | `#7FCF2E` |
| accent-ink | text on accent | `#14210A` |
| accent-2 | gradient partner, +25% lightness | `#C6F36A` |
| tint | accent @ 10–14% | `rgba(127,207,46,.12)` |
| success / warning / error | states only | `#6BD39B` / `#D6A24A` / `#B3402E` |

## Banned colours (a hard fail, not a preference)
- Indigo `#6366F1`, violet `#8B5CF6`, purple `#A855F7` — the single biggest AI tell.
- Blue→purple or cyan→magenta gradients, rainbow/holographic fills.
- Pure `#000000` text, pure `#FFFFFF` page background.
- Neon (`#00FF88`, `#FF00FF`) and any glow with a hue.
- More than two chromatic families on one screen.

## Tints, not new colours
Need a category colour? `accent.copy(alpha = 0.12f)` on surface, text in the accent at 80%
or in ink. Five categories = five alphas, not five hues. If you truly need a second family,
derive it from the same warm base (terracotta / sage / brass) — never from blue.

## Contrast (compute it, never eyeball)
- Body text ≥ 4.5:1 · large text (≥24sp) ≥ 3:1 · UI borders and icons ≥ 3:1.
- On a tint, use the ink of the same hue family at its 900 step, never black.
- Formula: relative luminance per WCAG, ratio = (L1 + .05) / (L2 + .05).
- A failing pair is fixed by changing the role, not by adding a shadow.

## Dark mode
Warm, never blue-slate: bg `#1E1916`, surface `#2C2520`, ink `#F4ECE3`, hairline `#3A322C`.
Shadows nearly disappear — express elevation with surface lightness steps and hairlines.
The accent shifts one step lighter and one step less saturated.
