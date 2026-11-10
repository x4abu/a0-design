# Uiverse — the largest open-source UI element library

**What it is.** A community library of small UI elements (buttons, switches, checkboxes,
loaders, inputs, tooltips, cards) copyable as HTML/CSS, Tailwind, React and Figma.
Free for personal and commercial use. Curated collections live in `uiverse-io/galaxy`
(loading UI, forms & inputs, animated buttons, glassmorphism cards, tooltips).

**Why it matters.** It is the best source for *micro-interaction* detail: how a switch
thumb overshoots, how a button squishes, how a loader breathes. Individual elements are
often over-decorated — take the timing, not the decoration.

## Element families to mine
- **Switches / toggles** — thumb overshoot, track colour crossfade, icon morph inside the
  knob (sun→moon), label crossfade beside the track.
- **Buttons** — squish on press (scale .96 + 2% darker), shine sweep on hover, border
  draw-on, arrow that slides right on hover.
- **Loaders** — three-dot bounce with staggered delays, ring spinner with a gap, skeleton
  shimmer at 1.4s linear, "breathing" dot for streaming states.
- **Inputs** — label that floats up and shrinks, focus ring that expands 2px, caret colour
  from the accent, error shake (2 cycles, 4dp amplitude, 220ms).
- **Cards** — border-gradient on hover, inner glow, image zoom 1.04 on hover with 600ms.

## Porting rules for Android
Take the *timing and easing*, drop the CSS tricks. A 200ms scale to .96 with a spring
return reads better than any box-shadow stack. Never port hover-only effects — a phone
has no cursor; convert them to press, long-press or focus states.

**Licence.** Community-made, free for personal and commercial use (check the individual
element). Link: https://uiverse.io · galaxy: https://github.com/uiverse-io/galaxy
