"""
The Design Brain: the design knowledge base that every generation is assembled
from. It ships as one markdown file (knowledge/design_brain.md) converted from the
single-file edition, split into parts at load time.

Assembly order follows DESIGN BRAIN Part 0 §8 — master rules, then the locked
theme, then platform rules, then only the screens/components the request needs,
then the user's prompt last. Injecting the whole library at once is what makes
generated design generic, so we never do that.
"""
from __future__ import annotations

import json
import re
from functools import lru_cache
from pathlib import Path

KNOWLEDGE_DIR = Path(__file__).resolve().parent.parent / "knowledge"
BRAIN_FILE = KNOWLEDGE_DIR / "design_brain.md"

PART_RE = re.compile(r"^##\s+(PART\s+\d+[^\n]*)", re.MULTILINE)

# How much of each part we allow into a prompt. Parts 4–5 are catalogues: we take
# a slice, never the whole thing (context bloat lowers quality).
PART_BUDGET = {
    "PART 0": 9000,
    "PART 1": 6000,
    "PART 2": 9000,
    "PART 3": 5000,
    "PART 4": 2500,
    "PART 5": 2500,
    "PART 6": 2500,
    "PART 7": 2000,
    "PART 8": 3500,
    "PART 9": 1200,
    "PART 10": 2500,
    "PART 11": 2500,
    "PART 12": 3000,
}


@lru_cache(maxsize=1)
def load_brain() -> dict[str, str]:
    text = BRAIN_FILE.read_text(encoding="utf-8")
    matches = list(PART_RE.finditer(text))
    parts: dict[str, str] = {}
    for i, m in enumerate(matches):
        key = m.group(1).split("—")[0].strip()
        end = matches[i + 1].start() if i + 1 < len(matches) else len(text)
        parts[key] = text[m.start():end].strip()
    return parts


def section_titles() -> list[str]:
    return list(load_brain().keys())


def get_part(key: str) -> str:
    parts = load_brain()
    for name, body in parts.items():
        if name.upper().startswith(key.upper()):
            return body
    return ""


def slice_part(key: str, limit: int | None = None) -> str:
    body = get_part(key)
    cap = limit or PART_BUDGET.get(key.upper(), 3000)
    return body[:cap]


def _theme_block(theme: str) -> str:
    """Pull one named theme out of Part 3 so only the chosen palette is injected."""
    part3 = get_part("PART 3")
    if not part3:
        return ""
    wanted = theme.lower().replace("-", " ")
    chunks = re.split(r"\n###\s+", part3)
    for chunk in chunks:
        if wanted.split()[0] in chunk.lower()[:80]:
            return "### " + chunk.strip()[:2200]
    return part3[:2200]


def _screen_block(archetypes: list[str] | None) -> str:
    if not archetypes:
        return ""
    part5 = get_part("PART 5")
    hits = []
    for name in archetypes:
        pattern = re.compile(rf"[^\n]*{re.escape(name)}[^\n]*", re.I)
        hits.extend(m.group(0) for m in pattern.finditer(part5))
    return "\n".join(dict.fromkeys(hits))[:2000]


PLATFORM_NOTES = {
    "android": (
        "PLATFORM: Android (Jetpack Compose, Material 3 Expressive).\n"
        "- Touch targets ≥48dp; bottom navigation 3–5 items; thumb-zone primary CTA.\n"
        "- Dark mode is first-class; layouts must hold at ≥600dp (foldable/tablet).\n"
        "- Speak Material 3 (tonal palettes, expressive shapes, idiomatic FAB) — do not clone iOS glass.\n"
        "- Output Kotlin + Compose, one @Composable per screen, tokens from the theme only."
    ),
    "web": (
        "PLATFORM: Website. Hero answers what/who/why in under 5 seconds with a real product image.\n"
        "Canonical order: Nav → Hero → Logo cloud → Features (bento) → Showcase → Proof → Pricing → FAQ → CTA → Footer.\n"
        "Section padding 120–200px desktop / 64px mobile; body never below 16px."
    ),
    "webapp": (
        "PLATFORM: Web app. Sidebar 240px or top-nav — never both.\n"
        "Every data screen defines Loading / Content / Empty / Error / Partial.\n"
        "Behaviour must be primitives-grade: focus trapping, ARIA, keyboard support."
    ),
}


def assemble_context(
    prompt: str,
    platform: str = "android",
    theme: str = "pastel-soft",
    screens: list[str] | None = None,
    system_prompt: str = "",
) -> str:
    """Build the exact prompt context described in Part 0 §8."""
    blocks = [
        "# DESIGN BRAIN — master rules (always applied)\n" + slice_part("PART 0"),
        "# Tokens you may use (nothing outside these)\n" + slice_part("PART 1"),
        "# Anti-slop contract (violating a ban is a reject)\n" + slice_part("PART 2"),
        "# Locked theme\n" + _theme_block(theme),
        "# " + PLATFORM_NOTES.get(platform, PLATFORM_NOTES["android"]),
    ]
    screen_block = _screen_block(screens)
    if screen_block:
        blocks.append("# Screen archetypes in scope\n" + screen_block)
    from . import pack, taste  # local import keeps this module import-safe on its own
    blocks += [
        "# Motion system (Design Brain Part 8)\n" + slice_part("PART 8"),
        pack.context_block(),
        taste.context_block(),
        "# Output contract\n"
        "End every answer with: (1) the AVOID list naming 3–5 slop patterns you deliberately avoided, "
        "(2) the self-check list from Part 0 §9, (3) a DESIGN.md block with the theme tokens you used.",
    ]
    if system_prompt.strip():
        blocks.append("# User system prompt override\n" + system_prompt.strip()[:2000])
    blocks.append("# The brief\n" + prompt.strip())
    return "\n\n".join(b for b in blocks if b.strip())


def themes() -> list[dict]:
    path = KNOWLEDGE_DIR / "themes.json"
    if not path.exists():
        return []
    return json.loads(path.read_text(encoding="utf-8"))


def tokens() -> dict:
    path = KNOWLEDGE_DIR / "tokens.json"
    if not path.exists():
        return {}
    return json.loads(path.read_text(encoding="utf-8"))
