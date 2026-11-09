# Review loop and steer commands (impeccable model)

Source: pbakaus/impeccable (Apache-2.0).

Generate → screenshot → deterministic checks → LLM critique → one-move steer → re-check.

Deterministic first: a rule-based slop detector costs nothing and catches most of it
(spacing off scale, three radii, contrast, placeholder copy, competing CTAs).

Then critique with context: always give the reviewer the theme tokens and the ban
list, otherwise the critique is generic. Score 0–80 across hierarchy, spacing, colour,
type, components, flow, states, originality. Below 56 → FIX-FIRST or REGENERATE.

Steer with named single moves, never "make it better":
distill · bolder · quieter · colorize · typeset · layout · animate · harden.
Maximum two moves per round; beyond that the theme or the prompt is wrong.

Ship with PRODUCT.md + DESIGN.md written into the project so the next session stays
on brand.
