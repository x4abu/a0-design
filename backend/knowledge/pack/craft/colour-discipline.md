# Colour discipline (how to *use* the palette)

Adapted from OpenDesign's `craft/color.md` (Apache-2.0), itself adapted from refero_skill
(MIT). The palette lives in `colour/palettes.md`; this file enforces usage.

## Four layers, with pixel budgets
| Layer | Share of pixels | Tokens |
|---|---|---|
| Neutrals | **70–90%** | bg, surface, ink, ink-soft, faint, hairline |
| Accent (one) | **5–10%** | accent only — never invent a second |
| Semantic | 0–5% | success / warning / danger |
| Effect | <1% | gradients, glows — rarely justified |

If a screenshot is more than 15% accent, it is not minimal, it is loud.

## Accent discipline (the biggest readability failure in AI UI)
- **At most 2 visible uses of the accent per screen.** Typical pairs: one chip + one
  primary CTA, or one accent card + one active tab.
- Links count as accent. If a screen has a CTA, demote links to ink + underline.
- Focus rings count as accent. Ration them.
- The accent is for *action and state*, never for decoration, borders on every card, or
  headings.

## Contrast (compute, never eyeball)
- Body ≥ 4.5:1 · large text (≥24sp) ≥ 3:1 · borders and icons ≥ 3:1.
- On a tint, use the deep step of the same hue, never black.
- A failing pair is fixed by changing the role, not by adding a shadow or a stroke.

## Semantic colour
- Success/warning/danger are for state only, never for branding.
- Always pair with an icon or text — colour alone fails colour-blind users.
- Warm variants (terracotta for error) beat pure red in warm palettes.

## Dark mode
Not an inversion. bg `#1E1916`, surface `#2C2520`, hairline `#3A322C`, ink `#F4ECE3`;
accent saturation −10%; shadows at 30% strength; a 1px top highlight on raised surfaces.

## Gradients
Two stops, same hue family, ≤25% lightness delta, 135°, only on the primary CTA, one hero
surface, or one word of text. Never on cards, never full-bleed, never behind body copy.
