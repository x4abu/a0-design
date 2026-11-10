"""
The taste layer: what stops a rule-compliant design from being forgettable.

Three mechanisms:
  1. deal — a deterministic hand (palette × type × layout × motion × bold move) so every
     project gets a designed combination and no two projects collapse into one house style;
  2. budget — hard per-screen limits on accent uses, gradients, blur, shadows, radii, motion;
  3. tests — eight pass/fail taste tests and a 0–80 critique rubric.

Source of truth is the pack (knowledge/pack/taste/ and json/deal.json).
"""
from __future__ import annotations

import hashlib
import json

from . import pack

SLOTS = ("palette", "type", "layout", "motif1", "motif2", "bold")


def _deck() -> dict:
    body = pack.read("json/deal.json")
    return json.loads(body) if body else {}


def _pick(options: list, seed: str, slot: str):
    digest = hashlib.sha256(f"{seed}::{slot}".encode()).hexdigest()
    return options[int(digest[:8], 16) % len(options)]


def deal(project: str, platform: str = "android") -> dict:
    deck = _deck()
    if not deck:
        return {"error": "deal deck missing"}
    seed = project.strip().lower()
    palette = _pick(deck["palettes"], seed, "palette")
    pairing = _pick(deck["typePairings"], seed, "type")
    layout = _pick(deck["layouts"], seed, "layout")
    pool = list(deck["motionMotifs"])
    motifs = []
    for slot in ("motif1", "motif2"):
        choice = _pick(pool, seed, slot)
        motifs.append(choice)
        pool.remove(choice)
    return {
        "project": project,
        "platform": platform,
        "palette": palette,
        "type": pairing,
        "layout": layout,
        "motion": motifs,
        "bold_move": _pick(deck["boldMoves"], seed, "bold"),
        "rule": "One hand per project. Justify it in one line, lock it, never mix two.",
    }


BUDGET = {
    "accent_uses_per_screen": 2,
    "gradients_per_screen": 1,
    "blur_surfaces_per_screen": 1,
    "shadowed_surfaces_per_screen": 3,
    "radii_per_screen": 2,
    "motion_effects_per_screen": 2,
    "ambient_loops_per_screen": 1,
    "font_families": 2,
    "font_weights": 4,
}

TESTS = [
    "Accent removal — does hierarchy survive without the accent colour?",
    "Blind identity — cover the logo; can a stranger name the product?",
    "Loudness count — accent surfaces + gradients + motion + oversized type ≤ 2",
    "One memorable thing — can you name it in one sentence?",
    "Squint test — same focal point at 40% zoom as at 100%?",
    "Type test — display ÷ body ≥ 2.5×, negative display tracking, one display voice",
    "Motion justification — every animation explains space, time or state",
    "Restraint audit — ≤1 blur, ≤1 gradient, ≤3 shadows, ≤2 accent uses",
]

RUBRIC = [
    "Hierarchy", "Space & rhythm", "Colour", "Type",
    "Components", "Flow & UX", "States", "Originality",
]


def context_block() -> str:
    """Compact taste contract injected into every generation prompt."""
    tests = "\n".join(f"- {t}" for t in TESTS)
    budget = " · ".join(f"{k}={v}" for k, v in BUDGET.items())
    return (
        "# TASTE CONTRACT\n"
        "Deal a hand before designing (palette × type × layout × two motion motifs × one bold "
        "move), justify it in one line, and never mix two hands.\n"
        f"Budget per screen: {budget}.\n"
        "Run these eight tests before answering; any failure means revise:\n" + tests + "\n"
        "Score 0–80 across " + ", ".join(RUBRIC) + "; ship at 72+, fix 56–71, regenerate below 56."
    )


def tests_doc() -> str:
    return pack.slice_doc("taste/taste-tests.md", 6000)


def budget_doc() -> str:
    return pack.slice_doc("taste/restraint-budget.md", 4000)
