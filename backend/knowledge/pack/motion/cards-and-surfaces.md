# Cards, sheets, surfaces

## Card
- Radius 28, padding 18–20, white, one elevation language: soft ink-tinted shadow
  (`0 14 30 -20` at 40%) **or** a hairline `#EEF3EA` — never both heavy.
- Enter: `scaleIn(.97f)` + fade 300ms, stagger 50ms across a row.
- Press: scale .98 + shadow drops one step, 150ms; release returns on a spring.
- Expand to detail: the card's media morphs (shared element) and the container grows;
  content fades in 80ms after the geometry settles.
- Swipe to delete: drag follows the finger with rubber-banding past 30%, the row collapses
  height 200ms then the list re-flows on a spring; an undo snackbar sits for 4s.

## Bottom sheet
- Slide up 600ms settle; scrim to 40% ink + 3dp blur; the sheet has a 40×5 grab handle.
- Content staggers in 40ms after the sheet lands (never during the slide).
- Drag: 1:1 with the finger, dismiss at 25% height or velocity > 1200dp/s.
- Dismiss: 300ms exit curve, scrim fades 250ms.
- Two sizes max: half (55%) and full (92%). Never three.

## Dialog (only for blocking decisions)
- `scaleIn(.94f)` + fade 220ms; scrim 50% ink; one primary, one text action.
- Destructive: the confirm button is a filled red-ink pill, the cancel is text.

## Glass surface
- Fill: white at 78–85%; border: 1dp white at 60%; shadow: ink-tinted, large and soft.
- Optional specular top edge: 1dp white at 90% on the top arc only.
- Backdrop blur where supported (`Modifier.blur` on a copy, or RenderEffect); on older
  devices, raise the fill opacity to 92% instead of faking blur.
- Never: glass on glass, glass on a photo without a scrim, or glass for body text.

## Toast / snackbar
- Top-anchored toast: slides down 12dp + fades, 300ms settle, auto-dismiss 2.4s, swipe up
  to dismiss. Never blocks the primary CTA.
- Snackbar: bottom, above the nav, 4s, one action maximum.

## Empty state
- Mascot or a single restrained illustration (never stock art), one sentence, one action.
- Enter: fade + 8dp rise, 320ms. No looping animation competing with the CTA.
