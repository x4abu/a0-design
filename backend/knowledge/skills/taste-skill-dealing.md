# Deal, don't describe (taste-skill model)

Source: Leonxlnx/taste-skill (MIT). Siblings: taste-skill-classics, imagegen-skills.

Two mechanisms worth copying:

1. Dealing. Instead of asking a model to "choose a nice palette", deal from a vetted
   deck of palette × type × layout combos. Random dealing removes the same-output
   problem; every deal is still a designed combination.
2. Gating. Effects are gated by category — WebGL belongs to tech/gaming briefs, never
   to a calm journaling app. Gate before generating, not after.

Plus a headless render checker: contrast, alignment and placeholder strings get
PASS/WARN/FAIL at 1440 / 1024 / 768 / 390 widths.

In a0: `themes.json` is the deck, `antislop.audit()` is the checker.
