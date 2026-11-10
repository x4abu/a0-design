# 00 — START HERE (v2)

You are building a premium product UI. This pack is the rulebook; the taste layer is what
makes the output feel designed rather than merely compliant. Read in this order.

## 1. Deal before you design
```bash
python scripts/deal.py --project "<brief name>" --platform android|web|webapp
```
It returns one **hand**: palette · type pairing · layout archetype · two motion motifs ·
one bold move. Lock it, and write one line justifying it for this brief. Never mix hands.
(`taste/deal-not-choose.md`)

## 2. Read the contracts
1. `craft/motion-discipline.md` — when motion earns its place, and the real numbers
   (150ms state default, curves for opacity, springs for position, mobile 20–30% shorter).
2. `craft/colour-discipline.md` — pixel budgets, **max 2 accent uses per screen**.
3. `craft/typography-hierarchy.md` — five vectors, one entry point, ≥2.5× type ratio.
4. `craft/state-coverage.md` — five states every data surface must render.
5. `craft/accessibility.md`, `craft/form-validation.md`, `craft/laws-of-ux.md`.
6. `colour/palettes.md` + `colour/grading-recipes.md` — pick from the deck, grade it.
7. `motion/` — the component-level recipes (tokens, text, toggles, nav, cards, transitions,
   scroll, loaders) and `compose-recipes.kt`.

## 3. Build to the budget
`taste/restraint-budget.md` — per screen: accent 2 · gradient 1 · blur 1 · shadows 3 ·
radii 2 · motion 2 · ambient loops 1. Write the budget as a comment and tick it off.

## 4. Then run the taste tests
`taste/taste-tests.md` — eight tests, all pass/fail: accent removal, blind identity,
loudness count, one memorable thing, squint, type, motion justification, restraint audit.

## 5. Score it before you ship
`taste/critique-rubric.md` — eight categories, 0–80. **72+ ship, 56–71 fix-first, below 56
regenerate.** Plus the out-loud genericness test: cover the logo, name the product.

## Non-negotiables
- No purple, violet, indigo (`#6366F1`), blue or neon. No blue→purple gradients.
- No pure black text or pure white page. Warm canvas, soft ink.
- Accent ≤2 uses per screen · one primary CTA · two radii · two font families.
- Motion from the token table only; nothing interactive over 500ms; transforms stripped
  under reduced motion, crossfades kept.
- Five states on every data surface: loading, empty, error, populated, edge.
- Real copy only; invented metrics are a P0 failure.
- Type ratio display ÷ body ≥ 2.5×, one display voice with actual personality.

## Answer shape
1. The hand, and one line on why it fits the brief.
2. The screen structure (blocks in order) + which archetype.
3. Tokens used (roles, not hex soup) + the two motion motifs with timings.
4. AVOID (3–5 banned patterns dodged).
5. Self-check: budget ticked, eight taste tests, rubric score.
