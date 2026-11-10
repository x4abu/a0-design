"""
a0 design — backend.

Three jobs:
  1. serve the Design Brain (the knowledge base) so the app and any agent can
     assemble context the way Part 0 §8 prescribes;
  2. run the deterministic anti-slop audit (15 checks + banned-pattern scan);
  3. generate, review and steer designs through the user's own model key.

Run locally:  uvicorn app.main:app --reload
"""
from __future__ import annotations

import json
import os
from pathlib import Path

from fastapi import FastAPI, HTTPException, Request
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

from . import brain, pack, prompts, providers
from .antislop import AuditReport, DesignSpec, audit

KNOWLEDGE_DIR = Path(__file__).resolve().parent.parent / "knowledge"

app = FastAPI(
    title="a0 design API",
    version="1.0.0",
    description="Design Brain + anti-slop engine + generation for the a0 Android app.",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=[o.strip() for o in os.getenv("ALLOWED_ORIGINS", "*").split(",")],
    allow_credentials=False,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ---------------------------------------------------------------------------
# Health
# ---------------------------------------------------------------------------
@app.get("/health")
def health() -> dict:
    return {
        "ok": True,
        "service": "a0-design-api",
        "brain_parts": len(brain.section_titles()),
        "skills": len(_skills_index()),
        "provider_configured": any(
            os.getenv(k) for k in ("OPENROUTER_API_KEY", "OPENAI_API_KEY", "ANTHROPIC_API_KEY")
        ),
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
# Skills / knowledge pack
# ---------------------------------------------------------------------------
def _skills_index() -> list[dict]:
    path = KNOWLEDGE_DIR / "skills.json"
    if not path.exists():
        return []
    return json.loads(path.read_text(encoding="utf-8"))


@app.get("/api/skills")
def skills() -> dict:
    items = _skills_index()
    return {"count": len(items), "skills": items}


@app.get("/api/skills/{skill_id}")
def skill_detail(skill_id: str) -> dict:
    for item in _skills_index():
        if item["id"] == skill_id:
            file = KNOWLEDGE_DIR / "skills" / item["file"]
            if not file.exists():
                raise HTTPException(status_code=404, detail="Skill file missing")
            return {**item, "content": file.read_text(encoding="utf-8")}
    raise HTTPException(status_code=404, detail="Unknown skill")


# ---------------------------------------------------------------------------
# Generate / review / steer
# ---------------------------------------------------------------------------
class GenerateRequest(BaseModel):
    prompt: str
    model: str = "anthropic/claude-sonnet-4"
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

    user_key = None
    auth = request.headers.get("authorization", "")
    if auth.lower().startswith("bearer "):
        user_key = auth.split(" ", 1)[1].strip() or None

    context = brain.assemble_context(
        req.prompt, req.platform, req.theme, req.screens, req.system_prompt
    )
    system = (
        prompts.SPEC_CONTRACT
        + "\n\n"
        + prompts.recipe_for(req.platform)
        + "\n\nThe design rules below are binding; the JSON contract above wins on format.\n"
    )

    result = await providers.complete(system, context, req.model, user_key)
    if result is None:
        # No key anywhere: say so plainly. The server never fabricates a design.
        raise HTTPException(
            status_code=409,
            detail=(
                "No API key. Send your provider key as 'Authorization: Bearer <key>' "
                "or set OPENROUTER_API_KEY / OPENAI_API_KEY / ANTHROPIC_API_KEY on the server."
            ),
        )

    payload = {
        "answer": result.text,
        "provider": result.provider,
        "model": result.model,
        "mode": req.mode,
        "antislop": audit(req.design_spec).model_dump() if req.design_spec else None,
    }
    return payload


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
            "SHIP" if report.verdict == "SHIP"
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
# Design knowledge pack — 13 reference sites, distilled
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


@app.get("/api/themes")
def themes() -> dict:
    return {"themes": brain.themes()}


@app.get("/api/tokens")
def tokens() -> dict:
    return brain.tokens()
