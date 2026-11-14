"""
a0 design — backend.

Core capabilities:
  1. Universal Model Provider Engine (Google Gemini, Groq, DeepSeek, OpenRouter, OpenAI, Anthropic, Custom Base URLs);
  2. Real-time Token & State Streaming (SSE) for instant design feedback;
  3. Deterministic anti-slop audit (15 checks + banned-pattern scan);
  4. Multi-format export engine (Jetpack Compose, React Tailwind, Flutter, HTML/CSS, DESIGN.md, Tokens);
  5. Built-in interactive Web Design Studio served at / and /studio;
  6. Serve Design Brain, taste layer, and reference packs.

Run locally:  uvicorn app.main:app --reload --port 8080
"""
from __future__ import annotations

import json
import os
import re
from pathlib import Path
from typing import AsyncGenerator

from fastapi import FastAPI, HTTPException, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import HTMLResponse, StreamingResponse
from pydantic import BaseModel, Field

from . import brain, export, pack, prompts, providers, taste
from .antislop import AuditReport, ColorTokens, DesignSpec, Screen, audit
from .studio_html import STUDIO_HTML

KNOWLEDGE_DIR = Path(__file__).resolve().parent.parent / "knowledge"

app = FastAPI(
    title="a0 design API",
    version="1.2.0",
    description="Universal Design Brain + Real-Time Engine + Anti-Slop Audit for Android & Web.",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=[o.strip() for o in os.getenv("ALLOWED_ORIGINS", "*").split(",")],
    allow_credentials=False,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------
def _skills_index() -> list[dict]:
    path = KNOWLEDGE_DIR / "skills.json"
    if not path.exists():
        return []
    return json.loads(path.read_text(encoding="utf-8"))


def parse_json_spec(raw: str) -> dict | None:
    """Robust JSON extraction from LLM response (handles markdown fences and trailing text)."""
    clean = raw.strip()
    if clean.startswith("```"):
        clean = re.sub(r"^```(?:json)?\n?", "", clean)
        clean = re.sub(r"\n?```$", "", clean)
    clean = clean.strip()

    try:
        return json.loads(clean)
    except Exception:
        # Try finding outer curly braces
        match = re.search(r"(\{.*\})", clean, re.DOTALL)
        if match:
            try:
                return json.loads(match.group(1))
            except Exception:
                pass
    return None


def spec_to_design_spec(spec_dict: dict, platform: str = "android") -> DesignSpec:
    """Converts a parsed LLM spec dict into an auditable DesignSpec for antislop checks."""
    theme = spec_dict.get("theme", {})
    screens_in = spec_dict.get("screens", [])
    copy_strings = []
    if "name" in spec_dict:
        copy_strings.append(spec_dict["name"])
    if "tagline" in spec_dict:
        copy_strings.append(spec_dict["tagline"])

    screens_out = []
    for s in screens_in:
        s_name = s.get("title") or s.get("id") or "Screen"
        button_count = 0
        for b in s.get("blocks", []):
            if b.get("type") == "button":
                button_count += 1
            if "text" in b:
                copy_strings.append(b["text"])
            if "title" in b:
                copy_strings.append(b["title"])
            if "meta" in b:
                copy_strings.append(b["meta"])
            for it in b.get("items", []):
                if isinstance(it, dict):
                    if "title" in it:
                        copy_strings.append(it["title"])
                    if "meta" in it:
                        copy_strings.append(it["meta"])
        screens_out.append(
            Screen(
                name=s_name,
                spacing=[4, 8, 12, 16, 24, 32],
                radii=[float(theme.get("radius", 24))],
                primary_ctas=button_count or 1,
                text_sizes=[14, 16, 20, 28],
                states=["idle", "loading", "pressed"],
                min_touch_target=48.0,
            )
        )

    return DesignSpec(
        platform=platform if platform in ("android", "web", "webapp") else "android",  # type: ignore
        colors=ColorTokens(
            bg=theme.get("bg", "#F8FBF4"),
            surface=theme.get("surface", "#FFFFFF"),
            ink=theme.get("ink", "#14171A"),
            accent=theme.get("accent", "#7FCF2E"),
            accent_ink=theme.get("ink", "#14210A"),
        ),
        screens=screens_out,
        motion_durations=[200, 300],
        exit_durations=[150],
        copy_strings=copy_strings,
    )


# ---------------------------------------------------------------------------
# Studio UI & Health
# ---------------------------------------------------------------------------
@app.get("/", response_class=HTMLResponse)
@app.get("/studio", response_class=HTMLResponse)
def studio() -> str:
    """Serve the minimal-premium interactive Web Studio."""
    return STUDIO_HTML


@app.get("/health")
def health() -> dict:
    return {
        "ok": True,
        "service": "a0-design-api",
        "version": "1.2.0",
        "brain_parts": len(brain.section_titles()),
        "skills": len(_skills_index()),
        "providers_supported": ["gemini", "groq", "deepseek", "openrouter", "openai", "anthropic", "custom"],
        "streaming_supported": True,
    }


# ---------------------------------------------------------------------------
# Models Catalogue
# ---------------------------------------------------------------------------
@app.get("/api/models")
def get_models() -> dict:
    return {
        "providers": {
            "gemini": {
                "label": "Google Gemini",
                "default": "gemini-2.5-flash",
                "models": ["gemini-2.5-flash", "gemini-2.0-flash", "gemini-1.5-pro", "gemini-1.5-flash"],
            },
            "groq": {
                "label": "Groq (Ultra-Fast)",
                "default": "llama-3.3-70b-versatile",
                "models": ["llama-3.3-70b-versatile", "llama-3.1-8b-instant", "mixtral-8x7b-32768", "qwen-2.5-coder-32b"],
            },
            "openrouter": {
                "label": "OpenRouter",
                "default": "anthropic/claude-3.5-sonnet",
                "models": [
                    "anthropic/claude-3.5-sonnet",
                    "google/gemini-2.5-flash",
                    "openai/gpt-4o",
                    "meta-llama/llama-3.3-70b-instruct",
                    "deepseek/deepseek-chat",
                ],
            },
            "openai": {
                "label": "OpenAI",
                "default": "gpt-4o",
                "models": ["gpt-4o", "gpt-4o-mini", "o3-mini", "o1"],
            },
            "anthropic": {
                "label": "Anthropic",
                "default": "claude-3-5-sonnet-20241022",
                "models": ["claude-3-5-sonnet-20241022", "claude-3-5-haiku-20241022"],
            },
            "deepseek": {
                "label": "DeepSeek",
                "default": "deepseek-chat",
                "models": ["deepseek-chat", "deepseek-coder"],
            },
        }
    }


# ---------------------------------------------------------------------------
# Design Brain
# ---------------------------------------------------------------------------
@app.get("/api/brain/sections")
def brain_sections() -> dict:
    titles = brain.section_titles()
    return {"count": len(titles), "sections": "|".join(titles), "list": titles}


@app.get("/api/brain")
def brain_full() -> dict:
    return {
        "sections": {k: v for k, v in brain.load_brain().items()},
        "themes": brain.themes(),
        "tokens": brain.tokens(),
    }


@app.get("/api/brain/{part}")
def brain_part(part: str) -> dict:
    body = brain.get_part(part)
    if not body:
        raise HTTPException(status_code=404, detail=f"No such part: {part}")
    return {"part": part, "content": body}


class ContextRequest(BaseModel):
    prompt: str = ""
    platform: str = "android"
    theme: str = "pastel-soft"
    screens: list[str] = Field(default_factory=list)
    system_prompt: str = ""


@app.post("/api/brain/context")
def brain_context(req: ContextRequest) -> dict:
    text = brain.assemble_context(
        req.prompt, req.platform, req.theme, req.screens, req.system_prompt
    )
    return {"chars": len(text), "context": text}


# ---------------------------------------------------------------------------
# Anti-slop
# ---------------------------------------------------------------------------
@app.get("/api/antislop/rules")
def antislop_rules() -> dict:
    path = KNOWLEDGE_DIR / "anti_slop.md"
    return {
        "bans": 36,
        "checks": 15,
        "document": path.read_text(encoding="utf-8") if path.exists() else "",
    }


@app.post("/api/antislop/check", response_model=AuditReport)
def antislop_check(spec: DesignSpec) -> AuditReport:
    return audit(spec)


# ---------------------------------------------------------------------------
# Generation (Synchronous & Real-Time SSE Stream)
# ---------------------------------------------------------------------------
class GenerateRequest(BaseModel):
    prompt: str
    model: str = "gemini-2.5-flash"
    provider: str | None = None
    base_url: str | None = None
    api_key: str | None = None
    mode: str = "Auto"
    platform: str = "android"
    theme: str = "pastel-soft"
    screens: list[str] = Field(default_factory=list)
    system_prompt: str = ""
    design_spec: DesignSpec | None = None


@app.post("/api/generate")
async def generate(req: GenerateRequest, request: Request) -> dict:
    if not req.prompt.strip():
        raise HTTPException(status_code=422, detail="prompt is required")

    user_key = req.api_key
    auth = request.headers.get("authorization", "")
    if auth.lower().startswith("bearer "):
        user_key = auth.split(" ", 1)[1].strip() or user_key

    provider_header = request.headers.get("x-provider") or req.provider
    base_url_header = request.headers.get("x-base-url") or req.base_url

    context = brain.assemble_context(
        req.prompt, req.platform, req.theme, req.screens, req.system_prompt
    )
    system = (
        prompts.SPEC_CONTRACT
        + "\n\n"
        + prompts.recipe_for(req.platform)
        + "\n\nThe design rules below are binding; the JSON contract above wins on format.\n"
    )

    result = await providers.complete(
        system=system,
        prompt=context,
        model=req.model,
        user_key=user_key,
        provider=provider_header,
        base_url=base_url_header,
    )
    if result is None:
        raise HTTPException(
            status_code=409,
            detail=(
                "No API key. Send your provider key as 'Authorization: Bearer <key>', in body 'api_key', "
                "or set GEMINI_API_KEY / GROQ_API_KEY / OPENROUTER_API_KEY / OPENAI_API_KEY on the server."
            ),
        )

    parsed_spec = parse_json_spec(result.text)
    audit_report = None
    if parsed_spec:
        ds = spec_to_design_spec(parsed_spec, req.platform)
        audit_report = audit(ds).model_dump()
    elif req.design_spec:
        audit_report = audit(req.design_spec).model_dump()

    return {
        "answer": result.text,
        "spec": parsed_spec,
        "provider": result.provider,
        "model": result.model,
        "mode": req.mode,
        "antislop": audit_report,
        "usage": result.usage,
    }


@app.post("/api/generate/stream")
async def generate_stream(req: GenerateRequest, request: Request) -> StreamingResponse:
    """
    Streams tokens and structured design updates in real time using Server-Sent Events (SSE).
    """
    if not req.prompt.strip():
        raise HTTPException(status_code=422, detail="prompt is required")

    user_key = req.api_key
    auth = request.headers.get("authorization", "")
    if auth.lower().startswith("bearer "):
        user_key = auth.split(" ", 1)[1].strip() or user_key

    provider_header = request.headers.get("x-provider") or req.provider
    base_url_header = request.headers.get("x-base-url") or req.base_url

    context = brain.assemble_context(
        req.prompt, req.platform, req.theme, req.screens, req.system_prompt
    )
    system = (
        prompts.SPEC_CONTRACT
        + "\n\n"
        + prompts.recipe_for(req.platform)
        + "\n\nThe design rules below are binding; the JSON contract above wins on format.\n"
    )

    async def event_generator() -> AsyncGenerator[str, None]:
        yield f"data: {json.dumps({'event': 'status', 'message': 'Consulting Design Brain...'})}\n\n"
        full_text = []

        async for chunk in providers.stream_complete(
            system=system,
            prompt=context,
            model=req.model,
            user_key=user_key,
            provider=provider_header,
            base_url=base_url_header,
        ):
            full_text.append(chunk)
            yield f"data: {json.dumps({'event': 'chunk', 'delta': chunk})}\n\n"

        complete_content = "".join(full_text)
        parsed = parse_json_spec(complete_content)
        if parsed:
            yield f"data: {json.dumps({'event': 'design', 'spec': parsed})}\n\n"
            ds = spec_to_design_spec(parsed, req.platform)
            audit_rep = audit(ds).model_dump()
            yield f"data: {json.dumps({'event': 'antislop', 'audit': audit_rep})}\n\n"

        yield f"data: {json.dumps({'event': 'done', 'characters': len(complete_content)})}\n\n"

    return StreamingResponse(
        event_generator(),
        media_type="text/event-stream",
        headers={"Cache-Control": "no-cache", "X-Accel-Buffering": "no"},
    )


# ---------------------------------------------------------------------------
# Multi-Format Export & Code Generation
# ---------------------------------------------------------------------------
class ExportRequest(BaseModel):
    spec: dict
    format: str = "compose"  # compose | react | flutter | html | markdown | tokens


@app.post("/api/export")
def export_design(req: ExportRequest) -> dict:
    fmt = req.format.lower().strip()
    if fmt == "compose":
        code = export.export_compose(req.spec)
        filename = f"{req.spec.get('name', 'Design')}.kt"
    elif fmt == "react":
        code = export.export_react(req.spec)
        filename = f"{req.spec.get('name', 'Design')}.tsx"
    elif fmt == "flutter":
        code = export.export_flutter(req.spec)
        filename = f"{req.spec.get('name', 'Design')}.dart"
    elif fmt == "html":
        code = export.export_standalone_html(req.spec)
        filename = "preview.html"
    elif fmt == "tokens":
        code = export.export_tokens_json(req.spec)
        filename = "tokens.json"
    else:
        code = export.export_design_markdown(req.spec)
        filename = "DESIGN.md"

    return {"format": fmt, "filename": filename, "code": code}


# ---------------------------------------------------------------------------
# Style Variants
# ---------------------------------------------------------------------------
class VariantRequest(BaseModel):
    spec: dict


@app.post("/api/variants")
def generate_variants(req: VariantRequest) -> dict:
    """Generates 3 curated theme variants: Minimal Dark, Pastel Soft, Editorial Warm."""
    base = req.spec.copy()
    variants = [
        {
            "id": "dark-minimal",
            "name": "Dark Obsidian & Lime",
            "theme": {
                "bg": "#0D0E11",
                "surface": "#16181D",
                "ink": "#FFFFFF",
                "accent": "#C7FF2E",
                "accent2": "#A5DF15",
                "radius": 24,
                "font": "sans",
            },
        },
        {
            "id": "pastel-soft",
            "name": "Pastel Clean & Sage",
            "theme": {
                "bg": "#F8FBF4",
                "surface": "#FFFFFF",
                "ink": "#14171A",
                "accent": "#7FCF2E",
                "accent2": "#C7FF2E",
                "radius": 26,
                "font": "sans",
            },
        },
        {
            "id": "warm-editorial",
            "name": "Editorial Warm & Terracotta",
            "theme": {
                "bg": "#FAF6F0",
                "surface": "#FFFFFF",
                "ink": "#1E1A17",
                "accent": "#C0623B",
                "accent2": "#E0835D",
                "radius": 20,
                "font": "serif",
            },
        },
    ]
    results = []
    for v in variants:
        v_spec = json.loads(json.dumps(base))
        v_spec["theme"] = v["theme"]
        v_spec["variant_name"] = v["name"]
        results.append(v_spec)

    return {"variants": results}


# ---------------------------------------------------------------------------
# Steer & Review
# ---------------------------------------------------------------------------
class ReviewRequest(BaseModel):
    spec: DesignSpec
    screenshots_note: str = ""


@app.post("/api/review")
def review(req: ReviewRequest) -> dict:
    report = audit(req.spec)
    return {
        "deterministic": report.model_dump(),
        "llm_prompt": prompts.REVIEW_PROMPT,
        "next_step": (
            "SHIP"
            if report.verdict == "SHIP"
            else "Run up to 2 steer commands from /api/steer, then re-check."
        ),
    }


class SteerRequest(BaseModel):
    command: str
    spec: DesignSpec | None = None


@app.post("/api/steer")
def steer(req: SteerRequest) -> dict:
    instruction = prompts.steer_instruction(req.command)
    if instruction is None:
        raise HTTPException(
            status_code=404,
            detail=f"Unknown steer command '{req.command}'. "
            f"Use one of: {', '.join(prompts.STEER_COMMANDS)}",
        )
    return {
        "command": req.command,
        "instruction": instruction,
        "rule": "Max two steer commands per round — beyond that, regenerate.",
        "before": audit(req.spec).model_dump() if req.spec else None,
    }


# ---------------------------------------------------------------------------
# Design knowledge pack
# ---------------------------------------------------------------------------
@app.get("/api/pack")
def pack_index() -> dict:
    items = pack.index()
    return {"count": len(items), "documents": items}


@app.get("/api/pack/motion")
def pack_motion() -> dict:
    return {"motion": pack.motion_tokens()}


@app.get("/api/pack/palettes")
def pack_palettes() -> dict:
    return {"palettes": pack.palettes()}


@app.get("/api/pack/components")
def pack_components() -> dict:
    return {"components": pack.components()}


@app.get("/api/pack/{path:path}")
def pack_file(path: str) -> dict:
    body = pack.read(path)
    if body is None:
        raise HTTPException(status_code=404, detail=f"No such document: {path}")
    return {"path": path, "content": body}


# ---------------------------------------------------------------------------
# Taste layer
# ---------------------------------------------------------------------------
@app.get("/api/taste/deal")
def taste_deal(project: str, platform: str = "android") -> dict:
    if not project.strip():
        raise HTTPException(status_code=422, detail="project is required")
    return {"hand": taste.deal(project, platform)}


@app.get("/api/taste/budget")
def taste_budget() -> dict:
    return {"budget": taste.BUDGET, "document": taste.budget_doc()}


@app.get("/api/taste/tests")
def taste_tests() -> dict:
    return {"tests": taste.TESTS, "rubric": taste.RUBRIC, "document": taste.tests_doc()}


@app.get("/api/themes")
def themes() -> dict:
    return {"themes": brain.themes()}


@app.get("/api/tokens")
def tokens() -> dict:
    return brain.tokens()
