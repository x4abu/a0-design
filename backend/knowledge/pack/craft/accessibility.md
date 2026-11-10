# Accessibility baseline (enforced)

Adapted from OpenDesign's `craft/accessibility-baseline.md` (Apache-2.0) plus WCAG 2.2 AA.

- Body ≥4.5:1, large text ≥3:1, borders/icons ≥3:1 — computed, never eyeballed.
- Touch targets ≥48dp Android / 44pt iOS, with ≥8dp between them.
- Text survives 200% scaling without clipping; no fixed-height text containers.
- Focus is always visible, order follows reading order, dialogs trap focus and close on
  Escape/back.
- Labels are persistent; placeholder-only labels are banned.
- Every icon-only control has an accessible label; active nav items expose `selected`.
- Never colour alone for state — pair with icon or text.
- Reduced motion: strip transforms, keep crossfades (see `craft/motion-discipline.md`).
- Flashing: no more than three flashes per second; prefer one-shot over loops.
- Screen-reader pass: each screen announces a heading, the primary action, and the result
  of any async action.
