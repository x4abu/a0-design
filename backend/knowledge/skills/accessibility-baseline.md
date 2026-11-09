# Accessibility baseline (enforced, not optional)

Sources: inclusive-design-skills practice, WCAG 2.2 AA, Material 3 guidance.

- Body text ≥4.5:1, large text ≥3:1, UI borders and icons ≥3:1. Computed, not eyeballed.
- Touch targets ≥48dp Android / 44pt iOS, with ≥8dp between them.
- Never colour alone: pair status colour with an icon or text.
- Text must survive 200% scaling without clipping — no fixed-height text containers.
- Focus is always visible; focus order follows reading order; every dialog traps focus
  and closes on Escape.
- Labels are persistent, never placeholder-only.
- Respect reduced motion and honour the platform's animation-duration scale.
- Empty states are an invitation to act, not a mood piece; errors explain the fix.
