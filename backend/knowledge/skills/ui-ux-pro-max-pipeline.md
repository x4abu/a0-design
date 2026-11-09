# Generation pipeline (ui-ux-pro-max model)

Source: nextlevelbuilder/ui-ux-pro-max-skill (MIT). Referenced for architecture only.

The pipeline that makes generation consistent instead of lucky:

  request
    → five parallel domain searches (product type, style, palette, pattern, typography)
    → reasoning layer with per-industry rules and anti-patterns
    → fixed output contract:
         Pattern · Style · Colors · Type · Effects · Avoid list · Checklist

Why it matters: the catalogues live in searchable data (CSV/JSON), not in one giant
prompt. Only the relevant slices are injected.

In a0 this is `brain.assemble_context()`: master rules → locked theme → platform →
only the screens in scope → the brief last. Context bloat is the main cause of
generic output.
