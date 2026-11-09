# Anti-AI-slop

Slop is the most predictable output. This repository treats it as a bug class with
tests. Three layers:

**1 · Prose.** `knowledge/design_brain.md` Part 2 lists the 36 hard bans and the three
AI-default aesthetics to avoid. It is injected into every generation context.

**2 · Code.** `backend/app/antislop.py` runs fifteen deterministic checks — spacing,
radius, fonts, accent, contrast, CTA, touch targets, placeholders, type scale, states,
nav, motion, icons, shadows, content, theme lock — plus a banned-pattern scanner over
generated code (indigo accents, blue-purple gradients, pure black/white, neon glow,
lorem ipsum, marquee, autoplay, emoji icons).

Each check returns PASS / WARN / FAIL with a specific reason. Any FAIL → `FIX-FIRST`.
More than 45 % failing → `REGENERATE`. Nothing is scored by an LLM, so the gate is
free, instant and reproducible.

**3 · Tests.** `backend/tests/test_antislop.py` ships one design that must SHIP and one
that must be rejected — the slop fixture fails on spacing, radius, fonts, accent, CTA,
placeholders and nav, and trips the code scanner.

## Using it

```bash
curl -s localhost:8000/api/antislop/check -H 'content-type: application/json' -d '{
  "platform": "android",
  "colors": {"accent": "#6366F1"},
  "screens": [{"name":"Home","spacing":[13,19],"radii":[4,8,12],"primary_ctas":3}]
}' | jq '.verdict, [.results[] | select(.status=="FAIL") | .id]'
```

## Thresholds that matter

- Body text ≥ 4.5:1, large text ≥ 3:1, computed from hex, never eyeballed.
- Spacing values must be on the 4pt scale; odd numbers are an amateur tell.
- At most two radii per screen and two font families with four weights.
- Exactly one primary CTA per screen; destructive actions never look primary.
- Motion durations from the token scale; exits shorter than enters; reduced-motion
  fallback required.
