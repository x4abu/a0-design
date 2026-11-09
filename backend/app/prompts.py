"""
Prompt templates: the generation recipes, the named steer commands and the
design-critic prompt. Lifted from DESIGN BRAIN Parts 11–12 so the server and the
app can never drift apart on wording.
"""
from __future__ import annotations

MASTER_TEMPLATE = """You are a senior product designer and Compose engineer working inside a0.

Hard rules (from the Design Brain):
1. Compose, don't invent — pick a screen archetype, a vault component, a locked theme.
2. One theme, locked. One accent, one primary CTA per screen.
3. Realistic content only. No lorem ipsum, no invented statistics, no placeholder labels.
4. Spacing from the 4pt scale, ≤2 radii per screen, ≤2 font families, ≤4 weights.
5. No blue-purple gradients, no #6366F1 indigo, no pure black/white, no neon glows.
6. Motion durations from the token scale, exits shorter than enters, reduced-motion fallback.
7. End with the Output Contract: AVOID list, self-check, DESIGN.md.

Brief: {prompt}
Platform: {platform}
Theme: {theme}
"""

ANDROID_RECIPE = """Android recipe:
- One @Composable per screen, previewable in isolation.
- Theme values come from a single Tokens object; no inline hex outside it.
- Use Material 3 shape scale mapped to the theme radius.
- Bottom navigation 3–5 destinations with a sliding pill indicator.
- Every async surface has loading (skeleton matching final layout), empty and error states.
"""

WEB_RECIPE = """Website recipe:
- Hero: the most characteristic thing in the subject's world, not a gradient blob.
- Type carries the personality: one or two clearly distinct families, deliberate scale.
- Section rhythm 120–200px desktop; text max-width ~720px.
- One orchestrated page-load moment instead of scattered fade-ups.
"""

WEBAPP_RECIPE = """Webapp recipe:
- Sidebar 240px or top-nav, never both.
- Every data screen defines Loading / Content / Empty / Error / Partial.
- Primitives-grade behaviour: focus trap, ARIA, keyboard, escape to close.
"""

STEER_COMMANDS = {
    "distill": "Remove clutter: demote or delete secondary elements, increase whitespace. Nothing else.",
    "bolder": "Sharpen hierarchy only: display size +1 step, more weight contrast, commit harder to the accent.",
    "quieter": "Reduce saturation, soften shadows, remove exactly one decoration layer.",
    "colorize": "Repair the tint system: re-derive category tints from one muted family.",
    "typeset": "Typography only: scale, line-height, tracking, pairing discipline.",
    "layout": "Structure only: alignment grid, section rhythm, container widths.",
    "animate": "Apply or repair choreography using the Part 8 motion tokens only.",
    "harden": "Stress the worst case: long names, 100+ items, empty states, slow network. Fix what breaks.",
}

REVIEW_PROMPT = """You are a senior product designer reviewing generated UI.
Below are the design tokens and the generated screens.

Score each category 0–10 and give a SPECIFIC fix for every deduction
(never "increase spacing" — write "card padding 16 → 24, section gap 32 → 48"):

1. HIERARCHY — does the most important thing catch the eye first?
2. SPACING & RHYTHM — is the token rhythm consistent?
3. COLOR — one-accent discipline? any gradient slop?
4. TYPOGRAPHY — does the display type have character? is body readable?
5. COMPONENTS — are states complete (pressed/disabled/loading)? one icon family?
6. UX FLOW — primary action in the thumb zone? one screen = one decision?
7. STATES — loading/empty/error considered?
8. ORIGINALITY — generic template, or its own identity?

Output format:
SCORE: X/80
TOP 3 FIXES: (highest impact first, with exact values)
SLOP FLAGS: (cite the ban by name)
VERDICT: SHIP / FIX-FIRST / REGENERATE
"""

BANNED_PROMPTS = [
    "make it better",
    "make it pop",
    "modern and clean",
    "beautiful UI",
    "use a blue-purple gradient",
    "add glassmorphism everywhere",
]


def recipe_for(platform: str) -> str:
    return {
        "android": ANDROID_RECIPE,
        "web": WEB_RECIPE,
        "webapp": WEBAPP_RECIPE,
    }.get(platform, ANDROID_RECIPE)


def steer_instruction(command: str) -> str | None:
    return STEER_COMMANDS.get(command.strip().lower())


def local_design_plan(prompt: str, platform: str = "android", theme: str = "pastel-soft") -> str:
    """
    Deterministic, offline answer used when no provider key is configured.
    It is honest about what it is: a structured design plan, not model output.
    """
    return (
        "Offline design plan (no model key configured on the server).\n\n"
        f"Brief: {prompt.strip()}\n"
        f"Platform: {platform}\n"
        f"Theme: {theme} (locked)\n\n"
        "Structure\n"
        "1. Hero — one confident headline, one primary CTA, ≥48dp of air above it.\n"
        "2. Content — three sections maximum, each with a single job.\n"
        "3. Proof — real content from the brief; no invented statistics.\n"
        "4. Close — the same CTA wording as the opening action.\n\n"
        "Tokens\n"
        "- bg #F8FBF4 → #E7F0DE, surface #FFFFFF, ink #14171A, accent #7FCF2E, accent-ink #14210A\n"
        "- radius: cards 28, chips/CTA pill, inputs 20 — two sizes only\n"
        "- spacing: 4 / 8 / 12 / 16 / 24 / 32 / 48\n"
        "- type: DM Sans 400/500/600/700, display 34, body 15, caption 12\n\n"
        "AVOID\n"
        "- indigo #6366F1 accent, blue-purple gradients, neon glows\n"
        "- three identical feature cards in a row\n"
        "- generic hero copy (\"Welcome to…\", \"Your all-in-one solution\")\n\n"
        "Self-check\n"
        "- spacing on the 4pt scale · one accent · one primary CTA per screen\n"
        "- contrast ≥4.5:1 body · targets ≥48dp · loading/empty/error defined\n"
    )
