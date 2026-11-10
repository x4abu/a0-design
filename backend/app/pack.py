"""
The design knowledge pack: 38 documents distilled from 13 reference sites
(21st.dev, React Bits, Magic UI, Uiverse, Mobbin, 60fps.design, Land-book, Landdding,
Recent, Godly, MotionSites, FontInLogo, Vibrant) plus the motion, colour and prompt
systems derived from them.

Served to the app and injected into generation context as compact slices — never the
whole pack, because context bloat is what makes output generic.
"""
from __future__ import annotations

import json
from functools import lru_cache
from pathlib import Path

PACK_DIR = Path(__file__).resolve().parent.parent / "knowledge" / "pack"

ALLOWED_SUFFIXES = {".md", ".json", ".kt", ".txt"}


@lru_cache(maxsize=1)
def index() -> list[dict]:
    items: list[dict] = []
    if not PACK_DIR.exists():
        return items
    for path in sorted(PACK_DIR.rglob("*")):
        if not path.is_file() or path.suffix not in ALLOWED_SUFFIXES:
            continue
        rel = path.relative_to(PACK_DIR).as_posix()
        items.append(
            {
                "path": rel,
                "title": _title(path),
                "section": rel.split("/")[0] if "/" in rel else "root",
                "bytes": path.stat().st_size,
            }
        )
    return items


def _title(path: Path) -> str:
    if path.suffix == ".md":
        for line in path.read_text(encoding="utf-8").splitlines():
            if line.startswith("# "):
                return line[2:].strip()
    return path.name


def read(rel: str) -> str | None:
    if not rel or ".." in rel:
        return None
    path = (PACK_DIR / rel).resolve()
    if PACK_DIR.resolve() not in path.parents or not path.is_file():
        return None
    if path.suffix not in ALLOWED_SUFFIXES:
        return None
    return path.read_text(encoding="utf-8")


def slice_doc(rel: str, limit: int = 2400) -> str:
    body = read(rel)
    return body[:limit] if body else ""


def motion_tokens() -> dict:
    body = read("json/motion.json")
    return json.loads(body) if body else {}


def palettes() -> dict:
    body = read("json/palettes.json")
    return json.loads(body) if body else {}


def components() -> dict:
    body = read("json/components.json")
    return json.loads(body) if body else {}


def context_block() -> str:
    """The part of the pack that goes into every generation prompt."""
    motion = slice_doc("motion/motion-tokens.md", 2600)
    palette = slice_doc("colour/colour-system.md", 2000)
    catalogue = slice_doc("components/component-catalogue.md", 2200)
    return (
        "# MOTION CONTRACT (durations and easings you may use)\n" + motion +
        "\n\n# COLOUR CONTRACT\n" + palette +
        "\n\n# COMPONENT MENU (name the pattern you are building)\n" + catalogue
    )
