"""
Universal Model Providers Engine for a0 design.
Supports:
  - Google Gemini (native OpenAI endpoint, Gemini 2.5/2.0 Flash, 1.5 Pro)
  - Groq (ultra fast inference, Llama 3.3 70B, etc.)
  - DeepSeek (deepseek-chat, deepseek-coder)
  - OpenRouter (all models)
  - OpenAI (GPT-4o, o3-mini, etc.)
  - Anthropic (Claude 3.5 Sonnet, Claude 3.5 Haiku)
  - Custom / Local OpenAI-compatible Base URL (Ollama, LM Studio, vLLM, Perplexity, etc.)

Supports both full completion and real-time token streaming (SSE).
"""
from __future__ import annotations

import json
import os
from dataclasses import dataclass
from typing import AsyncGenerator

import httpx

TIMEOUT = httpx.Timeout(90.0, connect=15.0)


@dataclass
class ProviderResult:
    text: str
    provider: str
    model: str
    usage: dict | None = None


# Env key getters
def _openrouter_key() -> str | None:
    return os.getenv("OPENROUTER_API_KEY") or None


def _openai_key() -> str | None:
    return os.getenv("OPENAI_API_KEY") or None


def _anthropic_key() -> str | None:
    return os.getenv("ANTHROPIC_API_KEY") or None


def _gemini_key() -> str | None:
    return os.getenv("GEMINI_API_KEY") or os.getenv("GOOGLE_API_KEY") or None


def _groq_key() -> str | None:
    return os.getenv("GROQ_API_KEY") or None


def _deepseek_key() -> str | None:
    return os.getenv("DEEPSEEK_API_KEY") or None


def auto_detect_provider(
    model: str,
    user_key: str | None = None,
    explicit_provider: str | None = None,
    base_url: str | None = None,
) -> tuple[str, str | None]:
    """
    Returns (provider_name, effective_api_key).
    Supported providers: gemini, groq, deepseek, openrouter, openai, anthropic, custom.
    """
    if explicit_provider:
        prov = explicit_provider.lower().strip()
        if prov in ("gemini", "google"):
            return "gemini", user_key or _gemini_key()
        if prov == "groq":
            return "groq", user_key or _groq_key()
        if prov == "deepseek":
            return "deepseek", user_key or _deepseek_key()
        if prov == "openai":
            return "openai", user_key or _openai_key()
        if prov == "anthropic":
            return "anthropic", user_key or _anthropic_key()
        if prov == "openrouter":
            return "openrouter", user_key or _openrouter_key()
        if prov in ("custom", "local") or base_url:
            return "custom", user_key or "sk-no-key-required"

    if base_url:
        return "custom", user_key or "sk-no-key-required"

    # Auto detect from API key pattern
    if user_key:
        k = user_key.strip()
        if k.startswith("AIzaSy"):
            return "gemini", k
        if k.startswith("gsk_"):
            return "groq", k
        if k.startswith("sk-ant-"):
            return "anthropic", k
        if k.startswith("sk-or-"):
            return "openrouter", k

    # Auto detect from model name
    m = model.lower()
    if "gemini" in m:
        key = user_key or _gemini_key()
        if key:
            return "gemini", key
        if _openrouter_key():
            return "openrouter", _openrouter_key()
    if "claude" in m or m.startswith("anthropic/"):
        key = user_key or _anthropic_key()
        if key and not (user_key and user_key.startswith("sk-or-")):
            return "anthropic", key
        if _openrouter_key() or (user_key and user_key.startswith("sk-or-")):
            return "openrouter", user_key or _openrouter_key()
    if "deepseek" in m:
        key = user_key or _deepseek_key()
        if key and not (user_key and user_key.startswith("sk-or-")):
            return "deepseek", key
        if _openrouter_key():
            return "openrouter", _openrouter_key()
    if "llama" in m or "mixtral" in m or "qwen" in m:
        key = user_key or _groq_key()
        if key and not (user_key and user_key.startswith("sk-or-")):
            return "groq", key
        if _openrouter_key():
            return "openrouter", _openrouter_key()
    if m.startswith("gpt-") or m.startswith("o1") or m.startswith("o3"):
        key = user_key or _openai_key()
        if key and not (user_key and user_key.startswith("sk-or-")):
            return "openai", key

    # Fallback cascade to configured keys
    if user_key:
        return "openrouter", user_key
    if _openrouter_key():
        return "openrouter", _openrouter_key()
    if _gemini_key():
        return "gemini", _gemini_key()
    if _groq_key():
        return "groq", _groq_key()
    if _openai_key():
        return "openai", _openai_key()
    if _anthropic_key():
        return "anthropic", _anthropic_key()
    if _deepseek_key():
        return "deepseek", _deepseek_key()

    return "none", None


def get_endpoint_and_headers(
    provider: str,
    key: str,
    base_url: str | None = None,
) -> tuple[str, dict[str, str]]:
    if provider == "gemini":
        url = "https://generativelanguage.googleapis.com/v1beta/openai/chat/completions"
        headers = {"Authorization": f"Bearer {key}", "Content-Type": "application/json"}
        return url, headers

    if provider == "groq":
        url = "https://api.groq.com/openai/v1/chat/completions"
        headers = {"Authorization": f"Bearer {key}", "Content-Type": "application/json"}
        return url, headers

    if provider == "deepseek":
        url = "https://api.deepseek.com/v1/chat/completions"
        headers = {"Authorization": f"Bearer {key}", "Content-Type": "application/json"}
        return url, headers

    if provider == "openai":
        url = "https://api.openai.com/v1/chat/completions"
        headers = {"Authorization": f"Bearer {key}", "Content-Type": "application/json"}
        return url, headers

    if provider == "anthropic":
        url = "https://api.anthropic.com/v1/messages"
        headers = {
            "x-api-key": key,
            "anthropic-version": "2023-06-01",
            "Content-Type": "application/json",
        }
        return url, headers

    if provider == "custom":
        root = (base_url or "http://localhost:11434/v1").rstrip("/")
        url = f"{root}/chat/completions"
        headers = {"Authorization": f"Bearer {key}", "Content-Type": "application/json"}
        return url, headers

    # Default to OpenRouter
    url = "https://openrouter.ai/api/v1/chat/completions"
    headers = {
        "Authorization": f"Bearer {key}",
        "HTTP-Referer": "https://github.com/x4abu/a0-design",
        "X-Title": "a0 design studio",
        "Content-Type": "application/json",
    }
    return url, headers


def normalize_model_name(provider: str, model: str) -> str:
    m = model.strip()
    if provider == "gemini":
        if "/" in m:
            m = m.split("/")[-1]
        if not m.startswith("gemini"):
            m = "gemini-2.5-flash"
        return m
    if provider == "groq":
        if "/" in m:
            m = m.split("/")[-1]
        if "llama" not in m and "mixtral" not in m and "qwen" not in m:
            m = "llama-3.3-70b-versatile"
        return m
    if provider == "deepseek":
        return "deepseek-chat"
    if provider == "anthropic":
        if "/" in m:
            m = m.split("/")[-1]
        if not m.startswith("claude"):
            m = "claude-3-5-sonnet-20241022"
        return m
    if provider == "openai":
        if "/" in m:
            m = m.split("/")[-1]
        return m
    return m


async def complete(
    system: str,
    prompt: str,
    model: str,
    user_key: str | None = None,
    provider: str | None = None,
    base_url: str | None = None,
    max_tokens: int = 3200,
) -> ProviderResult | None:
    prov, key = auto_detect_provider(model, user_key, provider, base_url)
    if not key or prov == "none":
        return None

    resolved_model = normalize_model_name(prov, model)
    url, headers = get_endpoint_and_headers(prov, key, base_url)

    async with httpx.AsyncClient(timeout=TIMEOUT) as client:
        if prov == "anthropic":
            payload = {
                "model": resolved_model,
                "max_tokens": max_tokens,
                "system": system,
                "messages": [{"role": "user", "content": prompt}],
            }
            res = await client.post(url, headers=headers, json=payload)
            res.raise_for_status()
            data = res.json()
            text = "".join(b.get("text", "") for b in data.get("content", []))
            return ProviderResult(
                text=text,
                provider="anthropic",
                model=resolved_model,
                usage=data.get("usage"),
            )

        # All other providers use OpenAI-compatible format
        payload = {
            "model": resolved_model,
            "max_tokens": max_tokens,
            "messages": [
                {"role": "system", "content": system},
                {"role": "user", "content": prompt},
            ],
        }
        res = await client.post(url, headers=headers, json=payload)
        res.raise_for_status()
        data = res.json()
        choices = data.get("choices", [])
        if not choices:
            return None
        text = choices[0].get("message", {}).get("content", "")
        return ProviderResult(
            text=text,
            provider=prov,
            model=resolved_model,
            usage=data.get("usage"),
        )


async def stream_complete(
    system: str,
    prompt: str,
    model: str,
    user_key: str | None = None,
    provider: str | None = None,
    base_url: str | None = None,
    max_tokens: int = 3200,
) -> AsyncGenerator[str, None]:
    """
    Streams tokens in real time from ANY model provider.
    Yields string text chunks as they arrive.
    """
    prov, key = auto_detect_provider(model, user_key, provider, base_url)
    if not key or prov == "none":
        return

    resolved_model = normalize_model_name(prov, model)
    url, headers = get_endpoint_and_headers(prov, key, base_url)

    async with httpx.AsyncClient(timeout=TIMEOUT) as client:
        if prov == "anthropic":
            payload = {
                "model": resolved_model,
                "max_tokens": max_tokens,
                "system": system,
                "messages": [{"role": "user", "content": prompt}],
                "stream": True,
            }
            async with client.stream("POST", url, headers=headers, json=payload) as response:
                response.raise_for_status()
                async for line in response.aiter_lines():
                    if not line or not line.startswith("data: "):
                        continue
                    data_str = line[6:].strip()
                    if not data_str:
                        continue
                    try:
                        chunk_json = json.loads(data_str)
                        c_type = chunk_json.get("type")
                        if c_type == "content_block_delta":
                            delta = chunk_json.get("delta", {}).get("text", "")
                            if delta:
                                yield delta
                    except Exception:
                        continue
            return

        # OpenAI-compatible streaming (OpenRouter, Gemini, Groq, DeepSeek, OpenAI, Custom)
        payload = {
            "model": resolved_model,
            "max_tokens": max_tokens,
            "messages": [
                {"role": "system", "content": system},
                {"role": "user", "content": prompt},
            ],
            "stream": True,
        }
        async with client.stream("POST", url, headers=headers, json=payload) as response:
            response.raise_for_status()
            async for line in response.aiter_lines():
                if not line or not line.startswith("data: "):
                    continue
                data_str = line[6:].strip()
                if data_str == "[DONE]":
                    break
                try:
                    chunk_json = json.loads(data_str)
                    choices = chunk_json.get("choices", [])
                    if choices:
                        delta = choices[0].get("delta", {}).get("content", "")
                        if delta:
                            yield delta
                except Exception:
                    continue
