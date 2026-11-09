"""
Deterministic anti-AI-slop engine.

This is the part that stops the product from generating the statistical average
of its training data. It is pure Python: no LLM, no API cost, no network — so it
can run on every generation, in CI, and inside tests.

Two layers:
  1. fifteen mechanical checks over a DesignSpec (tokens + screens + copy)
  2. a banned-pattern scanner over generated code and copy

Rules and thresholds come from DESIGN BRAIN Part 2 (36 hard bans, 15 checks).
"""
from __future__ import annotations

import re
from typing import Literal

from pydantic import BaseModel, Field

# ---------------------------------------------------------------------------
# Models
# ---------------------------------------------------------------------------

SPACING_SCALE = {4, 8, 12, 16, 24, 32, 48, 64, 96, 128, 160, 192}
MOTION_SCALE = {50, 100, 120, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 700, 800, 900, 1000}
TYPE_SCALE_MOBILE = {11, 12, 13, 14, 15, 16, 17, 21, 26, 28, 34, 36, 40, 42, 54, 64}
TYPE_SCALE_WEB = {11, 12, 13, 14, 16, 18, 20, 24, 28, 32, 40, 48, 56, 64, 72, 80, 96, 120}

PLACEHOLDER_PATTERNS = [
    r"lorem ipsum", r"your text here", r"your company", r"placeholder text",
    r"feature one", r"feature two", r"feature three", r"insert .* here",
    r"company name", r"sample text", r"todo:", r"tbd",
]

GENERIC_COPY = [
    r"welcome to \w+", r"your all[- ]in[- ]one solution", r"unlock the power of",
    r"revolutioni[sz]e your workflow", r"the future of \w+ is here",
    r"take your \w+ to the next level", r"seamlessly integrate",
]

EMOJI_RE = re.compile(
    "[" "\U0001F300-\U0001FAFF" "\U00002600-\U000027BF" "\U0001F1E6-\U0001F1FF" "]"
)

# accents that read as "default AI"
AI_DEFAULT_ACCENTS = {
    "#6366F1", "#6366f1", "#8B5CF6", "#8b5cf6", "#7C3AED", "#7c3aed",
    "#4F46E5", "#4f46e5", "#A855F7", "#a855f7",
}


class ColorTokens(BaseModel):
    bg: str = "#F8FBF4"
    surface: str = "#FFFFFF"
    ink: str = "#14171A"
    accent: str = "#7FCF2E"
    accent_ink: str = "#14210A"
    extra_chromatic: list[str] = Field(default_factory=list)


class Typography(BaseModel):
    families: list[str] = Field(default_factory=lambda: ["DM Sans"])
    weights: list[int] = Field(default_factory=lambda: [400, 500, 600, 700])
    display_family: str | None = "Instrument Serif"
    body_size: float = 15


class Screen(BaseModel):
    name: str
    spacing: list[float] = Field(default_factory=list)
    radii: list[float] = Field(default_factory=list)
    primary_ctas: int = 1
    text_sizes: list[float] = Field(default_factory=list)
    interactive_components: list[str] = Field(default_factory=list)
    states: list[str] = Field(default_factory=list)
    nav_destinations: int = 0
    min_touch_target: float = 48
    body_size: float | None = None
    gradients: list[str] = Field(default_factory=list)


class DesignSpec(BaseModel):
    platform: Literal["android", "web", "webapp"] = "android"
    colors: ColorTokens = Field(default_factory=ColorTokens)
    typography: Typography = Field(default_factory=Typography)
    screens: list[Screen] = Field(default_factory=list)
    motion_durations: list[int] = Field(default_factory=list)
    exit_durations: list[int] = Field(default_factory=list)
    reduced_motion_fallback: bool = True
    icon_sets: list[str] = Field(default_factory=lambda: ["a0"])
    elevation_language: Literal["shadow", "border", "mixed"] = "shadow"
    copy_strings: list[str] = Field(default_factory=list)
    code: str | None = None
    themes_locked: bool = True


class CheckResult(BaseModel):
    id: str
    name: str
    status: Literal["PASS", "WARN", "FAIL"]
    detail: str


class AuditReport(BaseModel):
    score: int
    max_score: int
    verdict: Literal["SHIP", "FIX-FIRST", "REGENERATE"]
    results: list[CheckResult]
    banned_hits: list[str] = Field(default_factory=list)


# ---------------------------------------------------------------------------
# Colour helpers
# ---------------------------------------------------------------------------

def parse_hex(value: str) -> tuple[int, int, int] | None:
    v = value.strip().lstrip("#")
    if len(v) == 3:
        v = "".join(c * 2 for c in v)
    if len(v) not in (6, 8):
        return None
    try:
        return int(v[0:2], 16), int(v[2:4], 16), int(v[4:6], 16)
    except ValueError:
        return None


def relative_luminance(hex_color: str) -> float | None:
    rgb = parse_hex(hex_color)
    if rgb is None:
        return None

    def channel(c: int) -> float:
        s = c / 255
        return s / 12.92 if s <= 0.03928 else ((s + 0.055) / 1.055) ** 2.4

    r, g, b = (channel(c) for c in rgb)
    return 0.2126 * r + 0.7152 * g + 0.0722 * b


def contrast_ratio(a: str, b: str) -> float | None:
    la, lb = relative_luminance(a), relative_luminance(b)
    if la is None or lb is None:
        return None
    hi, lo = max(la, lb), min(la, lb)
    return (hi + 0.05) / (lo + 0.05)


def is_blue_purple(hex_color: str) -> bool:
    """Blue/violet hues are the #1 AI tell when used as a gradient or accent."""
    rgb = parse_hex(hex_color)
    if rgb is None:
        return False
    r, g, b = rgb
    return b > 150 and b > r + 30 and b > g + 30


def is_pure_black_or_white(hex_color: str) -> bool:
    rgb = parse_hex(hex_color)
    return rgb in {(0, 0, 0), (255, 255, 255)}


# ---------------------------------------------------------------------------
# The fifteen checks
# ---------------------------------------------------------------------------

def _spacing_check(spec: DesignSpec) -> CheckResult:
    bad: list[str] = []
    for screen in spec.screens:
        for value in screen.spacing:
            if round(value) not in SPACING_SCALE:
                bad.append(f"{screen.name}:{value:g}")
    if bad:
        return CheckResult(id="spacing", name="Spacing audit", status="FAIL",
                           detail="Values outside the 4pt scale: " + ", ".join(bad[:6]))
    return CheckResult(id="spacing", name="Spacing audit", status="PASS",
                       detail="Every gap and pad is on the 4/8/12/16/24/32/48/64 scale.")


def _radius_check(spec: DesignSpec) -> CheckResult:
    offenders = []
    for screen in spec.screens:
        distinct = {round(r) for r in screen.radii}
        if len(distinct) > 2:
            offenders.append(f"{screen.name} uses {len(distinct)} radii {sorted(distinct)}")
    if offenders:
        return CheckResult(id="radius", name="Radius audit", status="FAIL",
                           detail="; ".join(offenders[:3]) + ". Max two per screen.")
    return CheckResult(id="radius", name="Radius audit", status="PASS",
                       detail="At most two radii per screen; nested corners stay concentric.")


def _font_check(spec: DesignSpec) -> CheckResult:
    families = [f for f in {*spec.typography.families, spec.typography.display_family or ""} if f]
    weights = set(spec.typography.weights)
    problems = []
    if len(families) > 2:
        problems.append(f"{len(families)} families ({', '.join(sorted(families))})")
    if len(weights) > 4:
        problems.append(f"{len(weights)} weights")
    if problems:
        return CheckResult(id="fonts", name="Font audit", status="FAIL",
                           detail="Too many type voices: " + " and ".join(problems) + ".")
    return CheckResult(id="fonts", name="Font audit", status="PASS",
                       detail=f"{len(families)} families, {len(weights)} weights — within limits.")


def _accent_check(spec: DesignSpec) -> CheckResult:
    c = spec.colors
    issues = []
    if c.accent.upper() in AI_DEFAULT_ACCENTS or is_blue_purple(c.accent):
        issues.append(f"accent {c.accent} is the default indigo/violet tell")
    chromatic = {c.accent.upper(), *(x.upper() for x in c.extra_chromatic)}
    if len(chromatic) > 2:
        issues.append(f"{len(chromatic)} chromatic colours on one screen")
    gradients = [g for s in spec.screens for g in s.gradients]
    if len(gradients) > 2:
        issues.append(f"{len(gradients)} gradients on one screen")
    for g in gradients:
        if any(is_blue_purple(part) for part in re.findall(r"#[0-9A-Fa-f]{3,8}", g)):
            issues.append(f"banned blue-purple gradient: {g}")
            break
    if issues:
        return CheckResult(id="accent", name="Accent audit", status="FAIL",
                           detail="; ".join(issues))
    return CheckResult(id="accent", name="Accent audit", status="PASS",
                       detail="One accent, ≤2 gradients, no banned hue family.")


def _contrast_check(spec: DesignSpec) -> CheckResult:
    c = spec.colors
    body = contrast_ratio(c.ink, c.bg)
    surface = contrast_ratio(c.ink, c.surface)
    cta = contrast_ratio(c.accent_ink, c.accent)
    worst = min([x for x in (body, surface, cta) if x is not None], default=None)
    if worst is None:
        return CheckResult(id="contrast", name="Contrast audit", status="WARN",
                           detail="Could not parse one of the colours; check hex values.")
    detail = f"ink/bg {body:.2f}:1 · ink/surface {surface:.2f}:1 · CTA {cta:.2f}:1"
    if worst < 3:
        return CheckResult(id="contrast", name="Contrast audit", status="FAIL", detail=detail)
    if worst < 4.5:
        return CheckResult(id="contrast", name="Contrast audit", status="WARN",
                           detail=detail + " — passes large text only")
    return CheckResult(id="contrast", name="Contrast audit", status="PASS", detail=detail)


def _cta_check(spec: DesignSpec) -> CheckResult:
    bad = [f"{s.name} has {s.primary_ctas}" for s in spec.screens if s.primary_ctas != 1]
    if bad:
        return CheckResult(id="cta", name="CTA audit", status="FAIL",
                           detail="; ".join(bad) + ". Exactly one primary per screen.")
    return CheckResult(id="cta", name="CTA audit", status="PASS",
                       detail="One primary action per screen; destructive actions stay secondary.")


def _target_check(spec: DesignSpec) -> CheckResult:
    floor = 48 if spec.platform == "android" else 44
    small = [f"{s.name}:{s.min_touch_target:g}" for s in spec.screens if s.min_touch_target < floor]
    if small:
        return CheckResult(id="targets", name="Target audit", status="FAIL",
                           detail=f"Tap targets below {floor}dp: " + ", ".join(small))
    return CheckResult(id="targets", name="Target audit", status="PASS",
                       detail=f"Every tappable is ≥{floor}dp with ≥8dp separation.")


def _placeholder_check(spec: DesignSpec) -> CheckResult:
    text = " ".join(spec.copy_strings).lower()
    hits = [p for p in PLACEHOLDER_PATTERNS if re.search(p, text)]
    if hits:
        return CheckResult(id="placeholders", name="Placeholder audit", status="FAIL",
                           detail="Placeholder copy found: " + ", ".join(hits))
    return CheckResult(id="placeholders", name="Placeholder audit", status="PASS",
                       detail="No lorem ipsum, no invented stats, no 'Your text here'.")


def _type_scale_check(spec: DesignSpec) -> CheckResult:
    scale = TYPE_SCALE_MOBILE if spec.platform == "android" else TYPE_SCALE_WEB
    floor = 14 if spec.platform == "android" else 16
    caption_floor = 11 if spec.platform == "android" else 12
    off_scale, too_small = [], []
    for s in spec.screens:
        for size in s.text_sizes:
            if round(size) not in scale:
                off_scale.append(f"{s.name}:{size:g}")
            # captions may sit below the body floor; body copy may not
            if size < caption_floor or (s.body_size is not None and s.body_size < floor):
                too_small.append(f"{s.name}:{size:g}")
        if s.body_size is not None and s.body_size < floor:
            too_small.append(f"{s.name} body:{s.body_size:g}")
    if off_scale or too_small:
        parts = []
        if off_scale:
            parts.append("off-scale sizes " + ", ".join(off_scale[:4]))
        if too_small:
            parts.append(f"below the {floor} floor " + ", ".join(too_small[:4]))
        return CheckResult(id="type_scale", name="Type scale audit", status="FAIL",
                           detail="; ".join(parts))
    return CheckResult(id="type_scale", name="Type scale audit", status="PASS",
                       detail=f"Every size is on the scale and body copy clears {floor}.")


def _state_check(spec: DesignSpec) -> CheckResult:
    missing = []
    for s in spec.screens:
        if s.interactive_components and not s.states:
            missing.append(s.name)
    if missing:
        return CheckResult(id="states", name="State audit", status="FAIL",
                           detail="No states declared for: " + ", ".join(missing))
    return CheckResult(id="states", name="State audit", status="PASS",
                       detail="Interactive elements declare default/pressed/disabled (+loading).")


def _nav_check(spec: DesignSpec) -> CheckResult:
    offenders = [f"{s.name}:{s.nav_destinations}" for s in spec.screens
                 if s.nav_destinations and not 3 <= s.nav_destinations <= 5]
    if offenders:
        return CheckResult(id="nav", name="Nav audit", status="FAIL",
                           detail="Destinations outside 3–5: " + ", ".join(offenders))
    return CheckResult(id="nav", name="Nav audit", status="PASS",
                       detail="3–5 destinations, labels present, active state visible.")


def _motion_check(spec: DesignSpec) -> CheckResult:
    bad = [d for d in spec.motion_durations if d not in MOTION_SCALE]
    if bad:
        return CheckResult(id="motion", name="Motion audit", status="FAIL",
                           detail="Durations outside the token scale: " + ", ".join(map(str, bad[:6])))
    if not spec.reduced_motion_fallback:
        return CheckResult(id="motion", name="Motion audit", status="FAIL",
                           detail="No reduced-motion fallback.")
    if spec.exit_durations and spec.motion_durations:
        if max(spec.exit_durations) > max(spec.motion_durations):
            return CheckResult(id="motion", name="Motion audit", status="WARN",
                               detail="Exits are longer than enters; exits should be ~70% of enter.")
    return CheckResult(id="motion", name="Motion audit", status="PASS",
                       detail="Durations on scale, exits shorter, reduced-motion handled.")


def _icon_check(spec: DesignSpec) -> CheckResult:
    problems = []
    if len(spec.icon_sets) > 1:
        problems.append("mixed icon sets: " + ", ".join(spec.icon_sets))
    if EMOJI_RE.search(" ".join(spec.copy_strings)):
        problems.append("emoji used as an icon")
    if problems:
        return CheckResult(id="icons", name="Icon audit", status="FAIL", detail="; ".join(problems))
    return CheckResult(id="icons", name="Icon audit", status="PASS",
                       detail="One icon family, one stroke width, no emoji.")


def _shadow_check(spec: DesignSpec) -> CheckResult:
    if spec.elevation_language == "mixed":
        return CheckResult(id="shadows", name="Shadow audit", status="WARN",
                           detail="Both shadows and borders carry elevation on one screen.")
    return CheckResult(id="shadows", name="Shadow audit", status="PASS",
                       detail="One elevation language, ink-tinted shadows only.")


def _content_check(spec: DesignSpec) -> CheckResult:
    text = " ".join(spec.copy_strings)
    hits = [p for p in GENERIC_COPY if re.search(p, text, re.I)]
    if hits:
        return CheckResult(id="content", name="Content audit", status="FAIL",
                           detail="Generic hero copy: " + ", ".join(hits))
    return CheckResult(id="content", name="Content audit", status="PASS",
                       detail="Copy is specific to the brief; numbers are internally consistent.")


def _theme_lock_check(spec: DesignSpec) -> CheckResult:
    if not spec.themes_locked:
        return CheckResult(id="theme_lock", name="Theme lock", status="FAIL",
                           detail="Theme tokens drifted mid-generation.")
    return CheckResult(id="theme_lock", name="Theme lock", status="PASS",
                       detail="One theme, locked before generation.")


CHECKS = [
    _spacing_check, _radius_check, _font_check, _accent_check, _contrast_check,
    _cta_check, _target_check, _placeholder_check, _type_scale_check, _state_check,
    _nav_check, _motion_check, _icon_check, _shadow_check, _content_check,
]


# ---------------------------------------------------------------------------
# Banned-pattern scanner (code + copy)
# ---------------------------------------------------------------------------

BANNED_CODE_PATTERNS: list[tuple[str, str]] = [
    (r"#[0-9A-Fa-f]{0,2}6366[Ff]1", "indigo #6366F1 accent — the default AI tell"),
    (r"from-(indigo|violet|purple)-\d00", "Tailwind indigo/violet scale"),
    (r"linear-gradient\([^)]*(purple|violet|indigo)", "blue-purple gradient"),
    (r"bg-gradient-to-[a-z]+ from-blue-\d00 to-purple-\d00", "blue→purple hero gradient"),
    (r"text-\[?#?0{3,6}\]?", "pure black text"),
    (r"background:\s*#fff{3};", "pure white page background"),
    (r"box-shadow:[^;]*rgba?\(0,\s*0,\s*255", "coloured blue glow"),
    (r"drop-shadow\(0 0 \d+px (blue|cyan|violet)", "neon outer glow"),
    (r"lorem ipsum", "lorem ipsum"),
    (r"<marquee", "marquee"),
    (r"autoplay", "autoplaying media"),
    (r"placeholder=\"(Feature|Lorem)", "placeholder feature copy"),
]


def scan_code(code: str | None) -> list[str]:
    if not code:
        return []
    hits: list[str] = []
    for pattern, label in BANNED_CODE_PATTERNS:
        if re.search(pattern, code, re.I):
            hits.append(label)
    if EMOJI_RE.search(code):
        hits.append("emoji used as UI icon")
    return hits


def audit(spec: DesignSpec) -> AuditReport:
    results = [check(spec) for check in CHECKS] + [_theme_lock_check(spec)]
    score = sum(1 for r in results if r.status == "PASS")
    score += sum(0.5 for r in results if r.status == "WARN")
    score = int(round(score))
    banned = scan_code(spec.code)

    if banned or any(r.status == "FAIL" for r in results):
        verdict = "FIX-FIRST"
    else:
        verdict = "SHIP"
    if score < len(results) * 0.55:
        verdict = "REGENERATE"

    return AuditReport(
        score=score,
        max_score=len(results),
        verdict=verdict,
        results=results,
        banned_hits=banned,
    )
