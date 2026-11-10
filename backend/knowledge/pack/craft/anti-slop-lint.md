# Anti-slop lint — the seven P0 sins

Adapted from OpenDesign's `craft/anti-ai-slop.md` (Apache-2.0). These are machine-checkable;
failing one is a regression, not a taste opinion. The wider ban list lives in
`backend/knowledge/anti_slop.md` (36 bans + 15 checks).

## P0 — must fix before shipping
1. **Indigo accent.** `#6366f1`, `#4f46e5`, `#4338ca`, `#3730a3`, `#8b5cf6`, `#7c3aed`,
   `#a855f7` used as an accent or a gradient stop.
2. **Two-stop "trust" gradient on the hero.** purple→blue, blue→cyan, indigo→pink.
3. **Emoji as feature icons.** ✨ 🚀 🎯 ⚡ 🔥 💡 inside a heading, button, list item or an
   element with `icon` in its class. Use 1.6–1.8dp monoline SVG with `currentColor`.
4. **Sans-serif on display text when the theme binds a serif.** h1/h2 must use the display
   token, not a hardcoded Inter/Roboto/system-ui.
5. **Rounded card with a coloured left-border accent.** The canonical AI dashboard tile.
   Drop either the radius or the border.
6. **Invented metrics.** "10× faster", "99.9% uptime", "3× more productive" with no source.
7. **Filler copy.** lorem ipsum, "feature one/two/three", "placeholder text", "sample".

## P1 — should fix
- The stock Hero → Features → Pricing → FAQ → CTA sequence with no structural idea.
- Three identical feature cards in a row.
- Centred everything; body text centred.
- Glassmorphism on glassmorphism; glass used for body copy.
- Hover-only affordances on touch surfaces.
- Icon set mixing (two families, two stroke widths).
- Section padding under 96px on desktop; under 32dp between mobile sections.
- A shadow and a border carrying the same elevation on one card.
- Accent used more than twice on a screen.
- A skeleton that does not match the final layout.

## How to run it
Grep-level checks work for most of these: search the codebase for the banned hex list, for
emoji inside JSX/Compose string literals, for `font-family: Inter` on `h1`, for
`border-left` combined with `border-radius`, for the filler-copy list. Wire the same list
into `backend/app/antislop.py` so generation and review share one source of truth.
