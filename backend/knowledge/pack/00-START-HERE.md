# 00 — START HERE (instructions for the agent reading this pack)

You are building a premium product UI. This pack is your rulebook. Read in this order and
do not skip:

1. `colour/colour-system.md` — pick roles, not colours. Learn the ban list.
2. `colour/palettes.md` — choose ONE palette and lock it for the whole build.
3. `motion/motion-tokens.md` — every duration and easing you are allowed to use.
4. `motion/*.md` for the components you are about to build (toggle, nav, text, sheet…).
5. `components/component-catalogue.md` — the build menu with Compose translations.
6. `prompts/<platform>.md` — the prompt shape for the surface you are building.

## Non-negotiables
- No purple, violet, indigo, blue or neon. No `#6366F1`. No blue→purple gradients.
- No pure black text, no pure white page. Warm canvas, soft ink.
- One accent, one primary CTA per screen, two radii per screen, two font families.
- Motion comes from the token table only. Springs for touch, settle for surfaces.
- Every interactive element has default / pressed / disabled (+ loading for async).
- Loading, empty and error states are designed, not left to chance.
- Real copy only. If data does not exist, say so in the UI.

## How to answer a design request
1. Name the surface (Android screen / web app view / landing section) and the one decision
   it makes.
2. Choose the palette and the type pairing from the pack — say which and why, in one line.
3. Lay out the blocks in order, using the catalogue's archetypes.
4. Add motion: at most two effects per screen, named from `motion/`, with their timings.
5. End with: AVOID (3–5 bans dodged), the self-check, and a DESIGN.md block.

## Self-check (run it before you answer)
tokens only · one accent · one primary CTA · ≤2 radii · body ≥4.5:1 · targets ≥48dp ·
states defined · reduced motion handled · no placeholder copy · no banned hue · durations
from the table · one orchestrated moment.
