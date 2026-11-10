# Typography hierarchy (how hierarchy behaves)

Adapted from OpenDesign's `craft/typography-hierarchy.md` (Apache-2.0). Sizes live in
`motion`/`design-md`; this file is about behaviour.

## The contract — every surface must satisfy all three
1. **One dominant entry point.** One element wins. If everything competes, nothing leads.
2. **Intentional rhythm between levels.** Hierarchy is the *contrast* between levels, not
   a list of sizes. Adjacent levels too close in scale, weight or spacing read as flat.
3. **Recoverable information flow.** You may invert or break the hierarchy, but a reader
   must still reconstruct the structure without re-reading.

## Use all five vectors, not just size
| Vector | Controls | Reads as |
|---|---|---|
| Scale | size contrast | large → primary |
| Weight | mass contrast | heavier → primary |
| Spacing | breathing room | more space → more important |
| Tracking | tension / velocity | tighter → faster; wider → ceremonial |
| Alignment | relationship to the grid | breaking alignment → importance |

A screen that only changes size is a screen with one lever. Two vectors per level is the
practical minimum (e.g. 34/37 + −0.03em, or 15/23 + 60% ink).

## Type-scale ratios
- Mobile: display ÷ body **≥ 2.5×**. 34/15 is 2.27 — push display to 38–42 or body to 14.
- Web: hero 64–120 vs body 17 → ratio 4–7×.
- Line height: display 1.05–1.15, body 1.5–1.65, caption 1.35.
- Tracking: display −0.02 to −0.04em, body 0, eyebrow +0.12em. Never negative tracking on
  body text.

## Controlled violations (allowed, with a reason)
- Weight inversion (light display + bold label) — only when the display is large enough to
  hold the surface.
- Breaking the grid with one element — once per page, never twice.
- A tiny caption at 11sp — only for metadata that is genuinely secondary, never for body.

## Test
At 40% zoom the eye must land on the same element it lands on at 100%. If the order
changes, the hierarchy is carried by detail rather than by structure.
