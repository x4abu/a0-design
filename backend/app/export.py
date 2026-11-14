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


def export_compose(spec: dict) -> str:
    name = spec.get("name", "A0Design")
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
        s_func = "".join(w.capitalize() for w in s_id.split("_")) + "Screen"

        blocks_code = []
        for b in screen.get("blocks", []):
            b_type = b.get("type")
            if b_type == "sub":
                blocks_code.append(
                    f'            Text(\n'
                    f'                text = "{b.get("text", "")}".uppercase(),\n'
                    f'                style = MaterialTheme.typography.labelMedium.copy(\n'
                    f'                    fontWeight = FontWeight.SemiBold,\n'
                    f'                    letterSpacing = 1.2.sp\n'
                    f'                ),\n'
                    f'                color = AccentColor\n'
                    f'            )'
                )
            elif b_type == "headline":
                txt = b.get("text", "").replace("\n", "\\n")
                blocks_code.append(
                    f'            Text(\n'
                    f'                text = "{txt}",\n'
                    f'                style = MaterialTheme.typography.headlineLarge.copy(\n'
                    f'                    fontWeight = FontWeight.Bold,\n'
                    f'                    fontSize = 32.sp,\n'
                    f'                    lineHeight = 38.sp\n'
                    f'                ),\n'
                    f'                color = InkColor\n'
                    f'            )'
                )
            elif b_type == "hero":
                blocks_code.append(
                    f'            Surface(\n'
                    f'                modifier = Modifier.fillMaxWidth(),\n'
                    f'                shape = RoundedCornerShape({radius}.dp),\n'
                    f'                color = SurfaceColor,\n'
                    f'                shadowElevation = 2.dp\n'
                    f'            ) {{\n'
                    f'                Column(Modifier.padding(20.dp)) {{\n'
                    f'                    Text("{b.get("title", "")}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = InkColor)\n'
                    f'                    Spacer(Modifier.height(6.dp))\n'
                    f'                    Text("{b.get("meta", "")}", style = MaterialTheme.typography.bodyMedium, color = InkColor.copy(alpha = 0.7f))\n'
                    f'                }}\n'
                    f'            }}'
                )
            elif b_type == "button":
                blocks_code.append(
                    f'            Button(\n'
                    f'                onClick = {{ /* CTA action */ }},\n'
                    f'                modifier = Modifier.fillMaxWidth().height(54.dp),\n'
                    f'                shape = RoundedCornerShape({radius}.dp),\n'
                    f'                colors = ButtonDefaults.buttonColors(containerColor = AccentColor, contentColor = InkColor)\n'
                    f'            ) {{\n'
                    f'                Text("{b.get("text", "Continue")}", fontWeight = FontWeight.Bold, fontSize = 16.sp)\n'
                    f'            }}'
                )
            elif b_type == "cards":
                items = b.get("items", [])
                items_code = []
                for it in items:
                    items_code.append(
                        f'                Surface(\n'
                        f'                    modifier = Modifier.fillMaxWidth(),\n'
                        f'                    shape = RoundedCornerShape({radius - 4}.dp),\n'
                        f'                    color = SurfaceColor,\n'
                        f'                    shadowElevation = 1.dp\n'
                        f'                ) {{\n'
                        f'                    Column(Modifier.padding(16.dp)) {{\n'
                        f'                        Text("{it.get("title", "")}", fontWeight = FontWeight.SemiBold, color = InkColor)\n'
                        f'                        Text("{it.get("meta", "")}", style = MaterialTheme.typography.bodySmall, color = InkColor.copy(alpha = 0.6f))\n'
                        f'                    }}\n'
                        f'                }}'
                    )
                blocks_code.append("\n".join(items_code))
            elif b_type == "rows":
                items = b.get("items", [])
                items_code = []
                for it in items:
                    items_code.append(
                        f'                Row(\n'
                        f'                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),\n'
                        f'                    horizontalArrangement = Arrangement.SpaceBetween,\n'
                        f'                    verticalAlignment = Alignment.CenterVertically\n'
                        f'                ) {{\n'
                        f'                    Column {{\n'
                        f'                        Text("{it.get("title", "")}", fontWeight = FontWeight.Medium, color = InkColor)\n'
                        f'                        Text("{it.get("meta", "")}", style = MaterialTheme.typography.bodySmall, color = InkColor.copy(alpha = 0.6f))\n'
                        f'                    }}\n'
                        f'                    Text("{it.get("trail", "")}", fontWeight = FontWeight.SemiBold, color = AccentColor)\n'
                        f'                }}'
                    )
                blocks_code.append("\n".join(items_code))
            elif b_type == "stat":
                blocks_code.append(
                    f'            Surface(\n'
                    f'                modifier = Modifier.fillMaxWidth(),\n'
                    f'                shape = RoundedCornerShape({radius}.dp),\n'
                    f'                color = AccentColor.copy(alpha = 0.15f)\n'
                    f'            ) {{\n'
                    f'                Column(Modifier.padding(20.dp)) {{\n'
                    f'                    Text("{b.get("text", "")}", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = InkColor)\n'
                    f'                    Text("{b.get("meta", "")}", style = MaterialTheme.typography.labelMedium, color = InkColor.copy(alpha = 0.7f))\n'
                    f'                }}\n'
                    f'            }}'
                )

        content = "\n".join(blocks_code)
        screens_code.append(
            f'@Composable\n'
            f'fun {s_func}() {{\n'
            f'    Column(\n'
            f'        modifier = Modifier\n'
            f'            .fillMaxSize()\n'
            f'            .background(BgColor)\n'
            f'            .verticalScroll(rememberScrollState())\n'
            f'            .padding(20.dp),\n'
            f'        verticalArrangement = Arrangement.spacedBy(16.dp)\n'
            f'    ) {{\n'
            f'{content}\n'
            f'    }}\n'
            f'}}'
        )

    all_screens = "\n\n".join(screens_code)

    return (
        f'package design.a0.generated\n\n'
        f'import androidx.compose.foundation.background\n'
        f'import androidx.compose.foundation.layout.*\n'
        f'import androidx.compose.foundation.rememberScrollState\n'
        f'import androidx.compose.foundation.shape.RoundedCornerShape\n'
        f'import androidx.compose.foundation.verticalScroll\n'
        f'import androidx.compose.material3.*\n'
        f'import androidx.compose.runtime.Composable\n'
        f'import androidx.compose.ui.Alignment\n'
        f'import androidx.compose.ui.Modifier\n'
        f'import androidx.compose.ui.graphics.Color\n'
        f'import androidx.compose.ui.text.font.FontWeight\n'
        f'import androidx.compose.ui.unit.dp\n'
        f'import androidx.compose.ui.unit.sp\n\n'
        f'// Design Tokens for {name}\n'
        f'val BgColor = Color(android.graphics.Color.parseColor("{bg}"))\n'
        f'val SurfaceColor = Color(android.graphics.Color.parseColor("{surface}"))\n'
        f'val InkColor = Color(android.graphics.Color.parseColor("{ink}"))\n'
        f'val AccentColor = Color(android.graphics.Color.parseColor("{accent}"))\n'
        f'val Accent2Color = Color(android.graphics.Color.parseColor("{accent2}"))\n'
        f'val CardCornerRadius = {radius}.dp\n\n'
        f'{all_screens}\n'
    )


def export_react(spec: dict) -> str:
    name = spec.get("name", "A0Design")
    theme = spec.get("theme", {})
    bg = theme.get("bg", "#F8FBF4")
    surface = theme.get("surface", "#FFFFFF")
    ink = theme.get("ink", "#14171A")
    accent = theme.get("accent", "#7FCF2E")
    screens = spec.get("screens", [])
    screens_json = json.dumps(screens, indent=2)
    component_name = "".join(w.capitalize() for w in name.replace("-", " ").replace("_", " ").split()) or "DesignPreview"

    template = """import React, { useState } from 'react';

// Design Spec: __NAME__
// Theme: bg=__BG__, surface=__SURFACE__, ink=__INK__, accent=__ACCENT__

export default function __COMP_NAME__() {
  const [activeScreen, setActiveScreen] = useState(0);
  const screens = __SCREENS__;
  const current = screens[activeScreen] || screens[0];

  return (
    <div className="min-h-screen p-4 sm:p-8 flex items-center justify-center font-sans" style={{ backgroundColor: '__BG__', color: '__INK__' }}>
      <div className="w-full max-w-sm rounded-[32px] p-6 shadow-2xl border border-black/5" style={{ backgroundColor: '__SURFACE__' }}>
        {/* Navigation tabs */}
        <div className="flex gap-2 mb-6 p-1 bg-black/5 rounded-full overflow-x-auto">
          {screens.map((s, idx) => (
            <button
              key={s.id || idx}
              onClick={() => setActiveScreen(idx)}
              className={`px-4 py-1.5 rounded-full text-xs font-medium transition-all ${activeScreen === idx ? 'bg-black text-white shadow-sm' : 'text-zinc-600'}`}
            >
              {s.title}
            </button>
          ))}
        </div>

        {/* Screen Content */}
        <div className="space-y-4">
          {current.blocks?.map((b, i) => (
            <div key={i}>
              {b.type === 'sub' && (
                <span className="text-[11px] font-semibold tracking-wider uppercase" style={{ color: '__ACCENT__' }}>
                  {b.text}
                </span>
              )}
              {b.type === 'headline' && (
                <h1 className="text-2xl font-bold tracking-tight whitespace-pre-line leading-tight">
                  {b.text}
                </h1>
              )}
              {b.type === 'hero' && (
                <div className="p-5 rounded-2xl shadow-sm border border-black/5" style={{ backgroundColor: '__BG__' }}>
                  <h3 className="font-semibold text-lg">{b.title}</h3>
                  <p className="text-sm opacity-70 mt-1">{b.meta}</p>
                </div>
              )}
              {b.type === 'button' && (
                <button
                  className="w-full py-3.5 rounded-2xl font-semibold text-sm transition-transform active:scale-[0.98] shadow-sm"
                  style={{ backgroundColor: '__ACCENT__', color: '__INK__' }}
                >
                  {b.text}
                </button>
              )}
              {b.type === 'cards' && (
                <div className="space-y-2">
                  {b.items?.map((it, j) => (
                    <div key={j} className="p-3.5 rounded-xl border border-black/5 bg-white/60">
                      <div className="font-medium text-sm">{it.title}</div>
                      <div className="text-xs opacity-60">{it.meta}</div>
                    </div>
                  ))}
                </div>
              )}
              {b.type === 'stat' && (
                <div className="p-4 rounded-2xl" style={{ backgroundColor: '__ACCENT__22' }}>
                  <div className="text-3xl font-extrabold">{b.text}</div>
                  <div className="text-xs opacity-75 font-medium">{b.meta}</div>
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
"""
    return (
        template
        .replace("__NAME__", name)
        .replace("__COMP_NAME__", component_name)
        .replace("__BG__", bg)
        .replace("__SURFACE__", surface)
        .replace("__INK__", ink)
        .replace("__ACCENT__", accent)
        .replace("__SCREENS__", screens_json)
    )


def export_flutter(spec: dict) -> str:
    name = spec.get("name", "A0Design")
    theme = spec.get("theme", {})
    bg = theme.get("bg", "#F8FBF4").lstrip("#")
    surface = theme.get("surface", "#FFFFFF").lstrip("#")
    ink = theme.get("ink", "#14171A").lstrip("#")
    accent = theme.get("accent", "#7FCF2E").lstrip("#")
    radius = theme.get("radius", 24)
    tagline = spec.get("tagline", "Designed with precision")
    class_name = "".join(w.capitalize() for w in name.replace("-", " ").replace("_", " ").split()) or "DesignScreen"

    return (
        f"import 'package:flutter/material.dart';\n\n"
        f"class {class_name} extends StatelessWidget {{\n"
        f"  const {class_name}({{super.key}});\n\n"
        f"  static const Color bgColor = Color(0xFF{bg});\n"
        f"  static const Color surfaceColor = Color(0xFF{surface});\n"
        f"  static const Color inkColor = Color(0xFF{ink});\n"
        f"  static const Color accentColor = Color(0xFF{accent});\n\n"
        f"  @override\n"
        f"  Widget build(BuildContext context) {{\n"
        f"    return Scaffold(\n"
        f"      backgroundColor: bgColor,\n"
        f"      appBar: AppBar(\n"
        f"        title: const Text('{name}', style: TextStyle(color: inkColor, fontWeight: FontWeight.bold)),\n"
        f"        backgroundColor: Colors.transparent,\n"
        f"        elevation: 0,\n"
        f"      ),\n"
        f"      body: SafeArea(\n"
        f"        child: SingleChildScrollView(\n"
        f"          padding: const EdgeInsets.all(20),\n"
        f"          child: Column(\n"
        f"            crossAxisAlignment: CrossAxisAlignment.start,\n"
        f"            children: [\n"
        f"              Text(\n"
        f"                'AI DESIGN ENGINE',\n"
        f"                style: TextStyle(color: accentColor, fontWeight: FontWeight.w600, letterSpacing: 1.2),\n"
        f"              ),\n"
        f"              const SizedBox(height: 8),\n"
        f"              const Text(\n"
        f"                '{tagline}',\n"
        f"                style: TextStyle(color: inkColor, fontSize: 28, fontWeight: FontWeight.bold, height: 1.2),\n"
        f"              ),\n"
        f"              const SizedBox(height: 24),\n"
        f"              ElevatedButton(\n"
        f"                style: ElevatedButton.styleFrom(\n"
        f"                  backgroundColor: accentColor,\n"
        f"                  foregroundColor: inkColor,\n"
        f"                  minimumSize: const Size.fromHeight(52),\n"
        f"                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular({radius})),\n"
        f"                  elevation: 0,\n"
        f"                ),\n"
        f"                onPressed: () {{}},\n"
        f"                child: const Text('Get Started', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),\n"
        f"              ),\n"
        f"            ],\n"
        f"          ),\n"
        f"        ),\n"
        f"      ),\n"
        f"    );\n"
        f"  }}\n"
        f"}}\n"
    )


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

    return (
        f'<!DOCTYPE html>\n'
        f'<html lang="en">\n'
        f'<head>\n'
        f'<meta charset="utf-8">\n'
        f'<meta name="viewport" content="width=device-width, initial-scale=1.0">\n'
        f'<title>{name} — Preview</title>\n'
        f'<style>\n'
        f'  :root {{\n'
        f'    --bg: {bg};\n'
        f'    --surface: {surface};\n'
        f'    --ink: {ink};\n'
        f'    --accent: {accent};\n'
        f'    --accent2: {accent2};\n'
        f'    --radius: {radius}px;\n'
        f'  }}\n'
        f'  * {{ box-sizing: border-box; margin: 0; padding: 0; }}\n'
        f'  body {{ background: var(--bg); color: var(--ink); font-family: sans-serif; display: flex; justify-content: center; padding: 24px; }}\n'
        f'  .frame {{ max-width: 380px; width: 100%; background: var(--surface); border-radius: 36px; padding: 20px; box-shadow: 0 20px 50px rgba(0,0,0,0.1); }}\n'
        f'  .btn {{ background: var(--accent); color: var(--ink); border: none; padding: 14px; border-radius: var(--radius); width: 100%; font-weight: bold; cursor: pointer; }}\n'
        f'</style>\n'
        f'</head>\n'
        f'<body>\n'
        f'<div class="frame">\n'
        f'  <h2 style="margin-bottom: 8px;">{name}</h2>\n'
        f'  <p style="opacity: 0.7; margin-bottom: 24px;">{spec.get("tagline", "")}</p>\n'
        f'  <button class="btn">Explore Design</button>\n'
        f'</div>\n'
        f'</body>\n'
        f'</html>\n'
    )
