#!/usr/bin/env python3
"""
Deal a design hand for a project. Deterministic: the same project name always gets the
same hand, so a project stays coherent across sessions; a new project gets a new hand, so
the product does not drift into one house style.

Usage:
    python scripts/deal.py --project "moss coffee" [--platform android|web|webapp]

No dependencies. Prints a markdown hand you can paste into a prompt.
"""
from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path

DECK = Path(__file__).resolve().parent.parent / "json" / "deal.json"


def pick(options: list, seed: str, slot: str):
    digest = hashlib.sha256(f"{seed}::{slot}".encode()).hexdigest()
    return options[int(digest[:8], 16) % len(options)]


def deal(project: str, platform: str = "android") -> dict:
    deck = json.loads(DECK.read_text(encoding="utf-8"))
    seed = project.strip().lower()
    palette = pick(deck["palettes"], seed, "palette")
    pairing = pick(deck["typePairings"], seed, "type")
    layout = pick(deck["layouts"], seed, "layout")
    motifs = []
    pool = list(deck["motionMotifs"])
    for slot in ("motif1", "motif2"):
        choice = pick(pool, seed, slot)
        motifs.append(choice)
        pool.remove(choice)
    bold = pick(deck["boldMoves"], seed, "bold")
    return {
        "project": project,
        "platform": platform,
        "palette": palette,
        "type": pairing,
        "layout": layout,
        "motion": motifs,
        "bold_move": bold,
    }


def main() -> None:
    ap = argparse.ArgumentParser(description="Deal a design hand")
    ap.add_argument("--project", required=True)
    ap.add_argument("--platform", default="android", choices=["android", "web", "webapp"])
    args = ap.parse_args()
    hand = deal(args.project, args.platform)
    print(f"# Hand for {hand['project']} ({hand['platform']})\n")
    print(f"- **palette**: {hand['palette']}")
    print(f"- **type**: {hand['type']['id']} — display {hand['type']['display']}, UI {hand['type']['ui']}")
    print(f"- **layout**: {hand['layout']}")
    print(f"- **motion**: {' + '.join(hand['motion'])}")
    print(f"- **one bold move**: {hand['bold_move']}\n")
    print("Justify in one line why this hand suits this brief, then lock it.")
    print("Never mix two hands; two motion motifs maximum.")


if __name__ == "__main__":
    main()
