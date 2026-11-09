"""The anti-slop engine is the product's spine — so it gets tested, not vibed."""
from fastapi.testclient import TestClient

from app.antislop import DesignSpec, audit, contrast_ratio
from app.brain import assemble_context, section_titles
from app.main import app

client = TestClient(app)

GOOD_SPEC = {
    "platform": "android",
    "colors": {
        "bg": "#F8FBF4", "surface": "#FFFFFF", "ink": "#14171A",
        "accent": "#7FCF2E", "accent_ink": "#14210A", "extra_chromatic": [],
    },
    "typography": {
        "families": ["DM Sans"], "weights": [400, 500, 600, 700],
        "display_family": "Instrument Serif", "body_size": 15,
    },
    "screens": [
        {
            "name": "Home", "spacing": [4, 8, 12, 16, 24, 32, 48],
            "radii": [28, 999], "primary_ctas": 1,
            "text_sizes": [11, 12, 15, 17, 34], "body_size": 15,
            "interactive_components": ["cta", "chip"],
            "states": ["default", "pressed", "disabled"], "nav_destinations": 4,
            "min_touch_target": 48, "gradients": ["linear-gradient(135deg,#CBF56F,#9BDD40)"],
        }
    ],
    "motion_durations": [120, 200, 300, 600],
    "exit_durations": [200],
    "reduced_motion_fallback": True,
    "icon_sets": ["a0"],
    "elevation_language": "shadow",
    "copy_strings": ["Slow coffee, well made.", "Order a bag"],
    "themes_locked": True,
}

SLOP_SPEC = {
    "platform": "web",
    "colors": {
        "bg": "#FFFFFF", "surface": "#FFFFFF", "ink": "#000000",
        "accent": "#6366F1", "accent_ink": "#FFFFFF",
        "extra_chromatic": ["#22D3EE", "#A855F7", "#F43F5E"],
    },
    "typography": {
        "families": ["Inter", "Roboto", "Poppins"], "weights": [300, 400, 500, 600, 700, 800],
        "display_family": "Inter", "body_size": 13,
    },
    "screens": [
        {
            "name": "Landing", "spacing": [13, 19, 27, 41],
            "radii": [4, 8, 12, 16, 24, 999], "primary_ctas": 3,
            "text_sizes": [9, 11, 13, 15], "interactive_components": ["cta"],
            "states": [], "nav_destinations": 9, "min_touch_target": 32,
            "gradients": ["linear-gradient(135deg,#6366F1,#A855F7)"],
        }
    ],
    "motion_durations": [333, 777, 1500],
    "exit_durations": [1500],
    "reduced_motion_fallback": False,
    "icon_sets": ["lucide", "heroicons"],
    "elevation_language": "mixed",
    "copy_strings": ["Welcome to our platform", "Lorem ipsum dolor", "Unlock the power of AI 🚀"],
    "code": "background: linear-gradient(#6366F1, #A855F7); color: #000;",
    "themes_locked": False,
}


def test_good_design_ships():
    report = audit(DesignSpec(**GOOD_SPEC))
    assert report.verdict == "SHIP", [r.model_dump() for r in report.results if r.status != "PASS"]
    assert report.score >= report.max_score - 1


def test_slop_is_rejected():
    report = audit(DesignSpec(**SLOP_SPEC))
    assert report.verdict in {"FIX-FIRST", "REGENERATE"}
    failed = {r.id for r in report.results if r.status == "FAIL"}
    assert {"spacing", "radius", "fonts", "accent", "cta", "placeholders", "nav"} <= failed
    assert report.banned_hits, "banned patterns must be caught in code"


def test_contrast_maths():
    assert contrast_ratio("#14171A", "#FFFFFF") > 15
    assert contrast_ratio("#7FCF2E", "#FFFFFF") < 2


def test_brain_sections_and_context():
    titles = section_titles()
    assert any(t.startswith("PART 0") for t in titles)
    assert len(titles) >= 10
    ctx = assemble_context("A quiet coffee app", platform="android", screens=["Onboarding"])
    assert "master rules" in ctx.lower()
    assert "A quiet coffee app" in ctx
    # the brief must come last
    assert ctx.strip().endswith("A quiet coffee app")


def test_api_health_and_check():
    assert client.get("/health").json()["ok"] is True
    res = client.post("/api/antislop/check", json=GOOD_SPEC)
    assert res.status_code == 200
    assert res.json()["verdict"] in {"SHIP", "FIX-FIRST"}


def test_generate_without_key_is_honest():
    res = client.post("/api/generate", json={"prompt": "a journaling app", "model": "anthropic/claude-sonnet-4"})
    assert res.status_code == 200
    body = res.json()
    assert body["provider"] in {"offline", "openrouter", "anthropic", "openai"}
    assert body["answer"]
