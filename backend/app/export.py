"""
Multi-format code and design system exporter for a0 design.
Translates design specs into:
  - Jetpack Compose (Kotlin)
  - React (TypeScript + Tailwind CSS)
  - Flutter (Dart)
  - Standalone Single-File HTML/CSS
  - DESIGN.md specification
  - Tokens JSON (Figma / W3C format)
"""
from __future__ import annotations

import json
from typing import Any


def export_compose(spec: dict) -> str:
    name = spec.get("name", "A0Design")
    pascal_name = "".join(w.capitalize() for w in name.replace("-", " ").replace("_", " ").split())
    theme = spec.get("theme", {})
    bg = theme.get("bg", "#F8FBF4")
    surface = theme.get("surface", "#FFFFFF")
    ink = theme.get("ink", "#14171A")
    accent = theme.get("accent", "#7FCF2E")
    accent2 = theme.get("accent2", "#C7FF2E")
    radius = theme.get("radius", 24)

    screens = spec.get("screens", [])
    screens_code = []

    for screen in screens:
        s_id = screen.get("id", "home")
        s_title = screen.get("title", "Screen")
        s_func = "".join(w.capitalize() for w in s_id.split("_")) + "Screen"

        blocks_code = []
        for b in screen.get("blocks", []):
            b_type = b.get("type")
            if b_type == "sub":
                blocks_code.append(f"""
            Text(
                text = "{b.get('text', '')}".uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp
                ),
                color = AccentColor
            )""")
            elif b_type == "headline":
                txt = b.get("text", "").replace("\n", "\\n")
                blocks_code.append(f"""
            Text(
                text = "{txt}",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    lineHeight = 38.sp
                ),
                color = InkColor
            )""")
            elif b_type == "hero":
                blocks_code.append(f"""
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape({radius}.dp),
                color = SurfaceColor,
                shadowElevation = 2.dp
            ) {{
                Column(Modifier.padding(20.dp)) {{
                    Text("{b.get('title', '')}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = InkColor)
                    Spacer(Modifier.height(6.dp))
                    Text("{b.get('meta', '')}", style = MaterialTheme.typography.bodyMedium, color = InkColor.copy(alpha = 0.7f))
                }}
            }}""")
            elif b_type == "button":
                blocks_code.append(f"""
            Button(
                onClick = {{ /* CTA action */ }},
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape({radius}.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor, contentColor = InkColor)
            ) {{
                Text("{b.get('text', 'Continue')}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }}""")
            elif b_type == "cards":
                items = b.get("items", [])
                items_code = []
                for it in items:
                    items_code.append(f"""
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape({radius - 4}.dp),
                    color = SurfaceColor,
                    shadowElevation = 1.dp
                ) {{
                    Column(Modifier.padding(16.dp)) {{
                        Text("{it.get('title', '')}", fontWeight = FontWeight.SemiBold, color = InkColor)
                        Text("{it.get('meta', '')}", style = MaterialTheme.typography.bodySmall, color = InkColor.copy(alpha = 0.6f))
                    }}
                }}""")
                blocks_code.append("\n".join(items_code))
            elif b_type == "rows":
                items = b.get("items", [])
                items_code = []
                for it in items:
                    items_code.append(f"""
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {{
                    Column {{
                        Text("{it.get('title', '')}", fontWeight = FontWeight.Medium, color = InkColor)
                        Text("{it.get('meta', '')}", style = MaterialTheme.typography.bodySmall, color = InkColor.copy(alpha = 0.6f))
                    }}
                    Text("{it.get('trail', '')}", fontWeight = FontWeight.SemiBold, color = AccentColor)
                }}""")
                blocks_code.append("\n".join(items_code))
            elif b_type == "stat":
                blocks_code.append(f"""
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape({radius}.dp),
                color = AccentColor.copy(alpha = 0.15f)
            ) {{
                Column(Modifier.padding(20.dp)) {{
                    Text("{b.get('text', '')}", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = InkColor)
                    Text("{b.get('meta', '')}", style = MaterialTheme.typography.labelMedium, color = InkColor.copy(alpha = 0.7f))
                }}
            }}""")

        content = "\n".join(blocks_code)
        screens_code.append(f"""
@Composable
fun {s_func}() {{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {{{content}
    }}
}}""")

    all_screens = "\n".join(screens_code)

    return f"""package design.a0.generated

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------------------------------------------------------------------------
// Design Tokens for {name}
// ---------------------------------------------------------------------------
val BgColor = Color(android.graphics.Color.parseColor("{bg}"))
val SurfaceColor = Color(android.graphics.Color.parseColor("{surface}"))
val InkColor = Color(android.graphics.Color.parseColor("{ink}"))
val AccentColor = Color(android.graphics.Color.parseColor("{accent}"))
val Accent2Color = Color(android.graphics.Color.parseColor("{accent2}"))
val CardCornerRadius = {radius}.dp

// ---------------------------------------------------------------------------
// Screens
// ---------------------------------------------------------------------------
{all_screens}
"""


def export_react(spec: dict) -> str:
    name = spec.get("name", "A0Design")
    theme = spec.get("theme", {})
    bg = theme.get("bg", "#F8FBF4")
    surface = theme.get("surface", "#FFFFFF")
    ink = theme.get("ink", "#14171A")
    accent = theme.get("accent", "#7FCF2E")
    screens = spec.get("screens", [])

    return f"""import React, {{"useState"}} from 'react';

// Design Spec: {name}
// Theme: bg={bg}, surface={surface}, ink={ink}, accent={accent}

export default function {name.replace(" ", "")}Preview() {{
  const [activeScreen, setActiveScreen] = useState(0);
  const screens = {json.dumps(screens, indent=2)};
  const current = screens[activeScreen] || screens[0];

  return (
    <div className="min-h-screen p-4 sm:p-8 flex items-center justify-center font-sans" style={{{{ backgroundColor: '{bg}', color: '{ink}' }}}}>
      <div className="w-full max-w-sm rounded-[32px] p-6 shadow-2xl border border-black/5" style={{{{ backgroundColor: '{surface}' }}}}>
        {/* Screen switcher */}
        <div className="flex gap-2 mb-6 p-1 bg-black/5 rounded-full overflow-x-auto">
          {{screens.map((s, idx) => (
            <button
              key={{s.id}}
              onClick={{() => setActiveScreen(idx)}}
              className={{`px-4 py-1.5 rounded-full text-xs font-medium transition-all ${{activeScreen === idx ? 'bg-black text-white shadow-sm' : 'text-zinc-600'}}`}}
            >
              {{s.title}}
            </button>
          ))}}
        </div>

        {/* Screen Content */}
        <div className="space-y-4">
          {{current.blocks?.map((b, i) => (
            <div key={{i}}>
              {{b.type === 'sub' && (
                <span className="text-[11px] font-semibold tracking-wider uppercase" style={{{{ color: '{accent}' }}}}>
                  {{b.text}}
                </span>
              )}}
              {{b.type === 'headline' && (
                <h1 className="text-2xl font-bold tracking-tight whitespace-pre-line leading-tight">
                  {{b.text}}
                </h1>
              )}}
              {{b.type === 'hero' && (
                <div className="p-5 rounded-2xl shadow-sm border border-black/5" style={{{{ backgroundColor: '{bg}' }}}}>
                  <h3 className="font-semibold text-lg">{{b.title}}</h3>
                  <p className="text-sm opacity-70 mt-1">{{b.meta}}</p>
                </div>
              )}}
              {{b.type === 'button' && (
                <button
                  className="w-full py-3.5 rounded-2xl font-semibold text-sm transition-transform active:scale-[0.98] shadow-sm"
                  style={{{{ backgroundColor: '{accent}', color: '{ink}' }}}}
                >
                  {{b.text}}
                </button>
              )}}
              {{b.type === 'cards' && (
                <div className="space-y-2">
                  {{b.items?.map((it, j) => (
                    <div key={{j}} className="p-3.5 rounded-xl border border-black/5 bg-white/60">
                      <div className="font-medium text-sm">{{it.title}}</div>
                      <div className="text-xs opacity-60">{{it.meta}}</div>
                    </div>
                  ))}}
                </div>
              )}}
              {{b.type === 'stat' && (
                <div className="p-4 rounded-2xl" style={{{{ backgroundColor: '{accent}22' }}}}>
                  <div className="text-3xl font-extrabold">{{b.text}}</div>
                  <div className="text-xs opacity-75 font-medium">{{b.meta}}</div>
                </div>
              )}}
            </div>
          ))}}
        </div>
      </div>
    </div>
  );
}}
"""


def export_flutter(spec: dict) -> str:
    name = spec.get("name", "A0Design")
    theme = spec.get("theme", {})
    bg = theme.get("bg", "#F8FBF4").lstrip("#")
    surface = theme.get("surface", "#FFFFFF").lstrip("#")
    ink = theme.get("ink", "#14171A").lstrip("#")
    accent = theme.get("accent", "#7FCF2E").lstrip("#")
    radius = theme.get("radius", 24)

    return f"""import 'package:flutter/material.dart';

// Generated by a0 design studio for Flutter
class {name.replace(" ", "")}Screen extends StatelessWidget {{
  const {name.replace(" ", "")}Screen({{super.key}});

  static const Color bgColor = Color(0xFF{bg});
  static const Color surfaceColor = Color(0xFF{surface});
  static const Color inkColor = Color(0xFF{ink});
  static const Color accentColor = Color(0xFF{accent});

  @override
  Widget build(BuildContext context) {{
    return Scaffold(
      backgroundColor: bgColor,
      appBar: AppBar(
        title: const Text('{name}', style: TextStyle(color: inkColor, fontWeight: FontWeight.bold)),
        backgroundColor: Colors.transparent,
        elevation: 0,
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(20),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'AI DESIGN ENGINE',
                style: TextStyle(color: accentColor, fontWeight: FontWeight.w600, letterSpacing: 1.2),
              ),
              const SizedBox(height: 8),
              const Text(
                '{spec.get("tagline", "Designed with precision")}',
                style: TextStyle(color: inkColor, fontSize: 28, fontWeight: FontWeight.bold, height: 1.2),
              ),
              const SizedBox(height: 24),
              ElevatedButton(
                style: ElevatedButton.styleFrom(
                  backgroundColor: accentColor,
                  foregroundColor: inkColor,
                  minimumSize: const Size.fromHeight(52),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular({radius})),
                  elevation: 0,
                ),
                onPressed: () {{}},
                child: const Text('Get Started', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
              ),
            ],
          ),
        ),
      ),
    );
  }}
}}
"""


def export_tokens_json(spec: dict) -> str:
    theme = spec.get("theme", {})
    tokens = {
        "color": {
            "background": {"value": theme.get("bg", "#F8FBF4"), "type": "color"},
            "surface": {"value": theme.get("surface", "#FFFFFF"), "type": "color"},
            "ink": {"value": theme.get("ink", "#14171A"), "type": "color"},
            "accent": {"value": theme.get("accent", "#7FCF2E"), "type": "color"},
            "accent2": {"value": theme.get("accent2", "#C7FF2E"), "type": "color"},
        },
        "borderRadius": {
            "card": {"value": f"{theme.get('radius', 24)}px", "type": "borderRadius"},
            "pill": {"value": "999px", "type": "borderRadius"},
        },
        "typography": {
            "fontFamily": {"value": theme.get("font", "sans"), "type": "fontFamilies"}
        },
    }
    return json.dumps(tokens, indent=2)


def export_design_markdown(spec: dict) -> str:
    name = spec.get("name", "Untitled")
    tagline = spec.get("tagline", "")
    theme = spec.get("theme", {})
    screens = spec.get("screens", [])

    lines = [
        f"# {name}",
        f"*{tagline}*" if tagline else "",
        "",
        "## Design Tokens",
        f"- **Background:** `{theme.get('bg', '#F8FBF4')}`",
        f"- **Surface:** `{theme.get('surface', '#FFFFFF')}`",
        f"- **Ink:** `{theme.get('ink', '#14171A')}`",
        f"- **Accent:** `{theme.get('accent', '#7FCF2E')}`",
        f"- **Radius:** `{theme.get('radius', 24)}px`",
        f"- **Typography:** `{theme.get('font', 'sans')}`",
        "",
        "## Screens Architecture",
    ]

    for s in screens:
        lines.append(f"### {s.get('title', 'Screen')} (`{s.get('id', 'id')}`)")
        for b in s.get("blocks", []):
            b_type = b.get("type", "")
            if b_type == "sub":
                lines.append(f"- **Kicker:** {b.get('text', '')}")
            elif b_type == "headline":
                lines.append(f"- **Headline:** {b.get('text', '').replace(chr(10), ' ')}")
            elif b_type == "hero":
                lines.append(f"- **Hero Banner:** {b.get('title', '')} — {b.get('meta', '')}")
            elif b_type == "button":
                lines.append(f"- **Primary Action:** `{b.get('text', '')}`")
            elif b_type == "cards":
                lines.append(f"- **Cards List:** {len(b.get('items', []))} items")
            elif b_type == "rows":
                lines.append(f"- **Data Rows:** {len(b.get('items', []))} items")
            elif b_type == "stat":
                lines.append(f"- **Metric Stat:** {b.get('text', '')} ({b.get('meta', '')})")
        lines.append("")

    return "\n".join(lines)


def export_standalone_html(spec: dict) -> str:
    name = spec.get("name", "a0 Design")
    theme = spec.get("theme", {})
    bg = theme.get("bg", "#F8FBF4")
    surface = theme.get("surface", "#FFFFFF")
    ink = theme.get("ink", "#14171A")
    accent = theme.get("accent", "#7FCF2E")
    accent2 = theme.get("accent2", "#C7FF2E")
    radius = theme.get("radius", 24)
    screens_json = json.dumps(spec.get("screens", []))

    return f"""<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<title>{name} — a0 Design Studio Preview</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=DM+Sans:ital,opsz,wght@0,9..40,400..700;1,9..40,400..700&family=Geist+Mono:wght@400;600&family=Instrument+Serif:ital@0;1&display=swap" rel="stylesheet">
<style>
  :root {{
    --bg: {bg};
    --surface: {surface};
    --ink: {ink};
    --accent: {accent};
    --accent2: {accent2};
    --radius: {radius}px;
  }}
  * {{ box-sizing: border-box; margin: 0; padding: 0; -webkit-tap-highlight-color: transparent; }}
  body {{
    background-color: var(--bg);
    color: var(--ink);
    font-family: 'DM Sans', -apple-system, BlinkMacSystemFont, sans-serif;
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 16px;
  }}
  .phone-frame {{
    width: 100%;
    max-width: 380px;
    background: var(--surface);
    border-radius: 40px;
    box-shadow: 0 24px 60px rgba(0, 0, 0, 0.08), 0 0 0 1px rgba(0, 0, 0, 0.04);
    overflow: hidden;
    display: flex;
    flex-direction: column;
    min-height: 680px;
    position: relative;
  }}
  .phone-notch {{
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 24px;
    font-size: 12px;
    font-weight: 600;
    opacity: 0.6;
  }}
  .tabs-bar {{
    display: flex;
    gap: 8px;
    padding: 8px 16px;
    overflow-x: auto;
    border-bottom: 1px solid rgba(0,0,0,0.04);
  }}
  .tab-btn {{
    padding: 6px 14px;
    border-radius: 999px;
    border: none;
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;
    background: rgba(0,0,0,0.05);
    color: var(--ink);
    transition: all 0.2s ease;
  }}
  .tab-btn.active {{
    background: var(--ink);
    color: var(--surface);
  }}
  .screen-body {{
    padding: 20px;
    display: flex;
    flex-direction: column;
    gap: 16px;
    flex: 1;
    overflow-y: auto;
  }}
  .kicker {{
    font-size: 12px;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 1px;
    color: var(--accent);
  }}
  .headline {{
    font-size: 28px;
    font-weight: 700;
    line-height: 1.2;
    letter-spacing: -0.5px;
    white-space: pre-line;
  }}
  .hero-card {{
    background: var(--bg);
    border-radius: var(--radius);
    padding: 18px;
    border: 1px solid rgba(0,0,0,0.05);
  }}
  .hero-title {{
    font-size: 18px;
    font-weight: 700;
    margin-bottom: 4px;
  }}
  .hero-meta {{
    font-size: 14px;
    opacity: 0.7;
  }}
  .btn-cta {{
    width: 100%;
    padding: 16px;
    border-radius: var(--radius);
    border: none;
    background: var(--accent);
    color: var(--ink);
    font-size: 16px;
    font-weight: 700;
    cursor: pointer;
    box-shadow: 0 4px 14px rgba(0,0,0,0.06);
    transition: transform 0.1s ease;
  }}
  .btn-cta:active {{ transform: scale(0.98); }}
  .cards-list {{
    display: flex;
    flex-direction: column;
    gap: 10px;
  }}
  .card-item {{
    background: var(--bg);
    padding: 14px 16px;
    border-radius: calc(var(--radius) - 6px);
    border: 1px solid rgba(0,0,0,0.03);
  }}
  .card-title {{ font-size: 15px; font-weight: 600; }}
  .card-meta {{ font-size: 13px; opacity: 0.65; margin-top: 2px; }}
  .stat-block {{
    background: rgba(127, 207, 46, 0.12);
    padding: 16px;
    border-radius: var(--radius);
  }}
  .stat-num {{ font-size: 36px; font-weight: 800; }}
  .stat-label {{ font-size: 13px; opacity: 0.75; font-weight: 500; }}
</style>
</head>
<body>

<div class="phone-frame">
  <div class="phone-notch">
    <span>9:41</span>
    <span>5G · 100%</span>
  </div>
  <div class="tabs-bar" id="tabsBar"></div>
  <div class="screen-body" id="screenBody"></div>
</div>

<script>
  const screens = {screens_json};
  let activeIndex = 0;

  function renderTabs() {{
    const bar = document.getElementById('tabsBar');
    bar.innerHTML = screens.map((s, i) => `
      <button class="tab-btn ${{i === activeIndex ? 'active' : ''}}" onclick="switchScreen(${{i}})">
        ${{s.title || 'Screen'}}
      </button>
    `).join('');
  }}

  function switchScreen(idx) {{
    activeIndex = idx;
    renderTabs();
    renderContent();
  }}

  function renderContent() {{
    const body = document.getElementById('screenBody');
    const screen = screens[activeIndex] || {{ blocks: [] }};
    body.innerHTML = (screen.blocks || []).map(b => {{
      if (b.type === 'sub') return `<div class="kicker">${{b.text}}</div>`;
      if (b.type === 'headline') return `<div class="headline">${{b.text}}</div>`;
      if (b.type === 'hero') return `
        <div class="hero-card">
          <div class="hero-title">${{b.title}}</div>
          <div class="hero-meta">${{b.meta || ''}}</div>
        </div>`;
      if (b.type === 'button') return `<button class="btn-cta">${{b.text}}</button>`;
      if (b.type === 'stat') return `
        <div class="stat-block">
          <div class="stat-num">${{b.text}}</div>
          <div class="stat-label">${{b.meta || ''}}</div>
        </div>`;
      if (b.type === 'cards') return `
        <div class="cards-list">
          ${{(b.items || []).map(it => `
            <div class="card-item">
              <div class="card-title">${{it.title}}</div>
              <div class="card-meta">${{it.meta || ''}}</div>
            </div>
          `).join('')}}
        </div>`;
      if (b.type === 'rows') return `
        <div class="cards-list">
          ${{(b.items || []).map(it => `
            <div class="card-item" style="display:flex; justify-content:space-between; align-items:center;">
              <div>
                <div class="card-title">${{it.title}}</div>
                <div class="card-meta">${{it.meta || ''}}</div>
              </div>
              <span style="font-weight:700; color:var(--accent); font-size:14px;">${{it.trail || ''}}</span>
            </div>
          `).join('')}}
        </div>`;
      return '';
    }}).join('');
  }}

  renderTabs();
  renderContent();
</script>
</body>
</html>
"""
