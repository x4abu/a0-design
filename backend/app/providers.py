"""
Model providers. One small async surface so the app can point at OpenRouter,
OpenAI or Anthropic with the user's own key — and degrade honestly when no key
is configured (the route returns the offline design plan instead of fake output).
"""
from __future__ import annotations

import os
from dataclasses import dataclass

import httpx

TIMEOUT = httpx.Timeout(60.0, connect=10.0)


@dataclass
class ProviderResult:
    text: str
    provider: str
    model: str


def _openrouter_key() -> str | None:
    return os.getenv("OPENROUTER_API_KEY") or None


def _openai_key() -> str | None:
    return os.getenv("OPENAI_API_KEY") or None


def _anthropic_key() -> str | None:
    return os.getenv("ANTHROPIC_API_KEY") or None


def resolve_provider(model: str, user_key: str | None = None) -> str | None:
    """Pick the provider that has a key for this model id."""
    if user_key:
        if model.startswith("anthropic/") or model.startswith("claude"):
            return "openrouter" if _openrouter_key() else "anthropic"
        return "openrouter"
    if model.startswith("anthropic/") or "/" in model:
        return "openrouter" if _openrouter_key() else None
    if model.startswith("claude"):
        return "anthropic" if _anthropic_key() else None
    if model.startswith("gpt"):
        return "openai" if _openai_key() else None
    return "openrouter" if _openrouter_key() else None


async def complete(
    system: str,
    prompt: str,
    model: str,
    user_key: str | None = None,
    max_tokens: int = 2200,
) -> ProviderResult | None:
    provider = resolve_provider(model, user_key)
    if provider is None:
        return None

    if provider == "openrouter":
        key = user_key or _openrouter_key()
        if not key:
            return None
        async with httpx.AsyncClient(timeout=TIMEOUT) as client:
            res = await client.post(
                "https://openrouter.ai/api/v1/chat/completions",
                headers={"Authorization": f"Bearer {key}", "HTTP-Referer": "https://a0.design",
                         "X-Title": "a0 design"},
                json={
                    "model": model,
                    "max_tokens": max_tokens,
                    "messages": [
                        {"role": "system", "content": system},
                        {"role": "user", "content": prompt},
                    ],
                },
            )
            res.raise_for_status()
            data = res.json()
            text = data["choices"][0]["message"]["content"]
            return ProviderResult(text=text, provider="openrouter", model=model)

    if provider == "anthropic":
        key = user_key or _anthropic_key()
        if not key:
            return None
        async with httpx.AsyncClient(timeout=TIMEOUT) as client:
            res = await client.post(
                "https://api.anthropic.com/v1/messages",
                headers={"x-api-key": key, "anthropic-version": "2023-06-01"},
                json={
                    "model": model if model.startswith("claude") else "claude-sonnet-4-20250514",
                    "max_tokens": max_tokens,
                    "system": system,
                    "messages": [{"role": "user", "content": prompt}],
                },
            )
            res.raise_for_status()
            data = res.json()
            text = "".join(block.get("text", "") for block in data.get("content", []))
            return ProviderResult(text=text, provider="anthropic", model=model)

    if provider == "openai":
        key = user_key or _openai_key()
        if not key:
            return None
        async with httpx.AsyncClient(timeout=TIMEOUT) as client:
            res = await client.post(
                "https://api.openai.com/v1/chat/completions",
                headers={"Authorization": f"Bearer {key}"},
                json={
                    "model": model,
                    "max_tokens": max_tokens,
                    "messages": [
                        {"role": "system", "content": system},
                        {"role": "user", "content": prompt},
                    ],
                },
            )
            res.raise_for_status()
            data = res.json()
            text = data["choices"][0]["message"]["content"]
            return ProviderResult(text=text, provider="openai", model=model)

    return None
