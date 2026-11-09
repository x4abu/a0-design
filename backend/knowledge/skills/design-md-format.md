# DESIGN.md — the interchange format

Origin: Google Stitch's DESIGN.md; popularised by VoltAgent/awesome-design-md (MIT),
which collects brand DESIGN.md files; Ant Design keeps one at its repo root.

Structure that works:
  # <Project> design system
  ## Theme        — one line on the mood, plus the locked palette
  ## Colour       — roles: bg, surface, ink, ink-soft, accent, accent-ink, tints
  ## Type         — families, scale, weights, tracking
  ## Shape        — radius scale, which radius belongs to which component
  ## Spacing      — the scale, plus the rhythm between sections
  ## Components   — primary/secondary/tertiary, card, input, nav, chips
  ## Motion       — durations, easings, what animates and what never does
  ## Avoid        — the 3–5 slop patterns this project specifically rejects

Why it matters: it is the cheapest way to keep a second session (human or agent) on
brand. Every a0 generation ends with one.
