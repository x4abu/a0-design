# a0 design system

The theme below is the exact cascade winner of the reference prototype's CSS —
the lime + white light theme. Every value is mirrored in
`android/app/src/main/java/design/a0/app/ui/theme/Tokens.kt`.

## Theme
Pastel Soft, lime edition. Warm off-white canvas with a soft green wash on the top
third, frosted white surfaces, near-black ink, one lime accent. Quiet, premium,
generous. Locked before generation — drift is a reject.

## Colour (roles only)
| Role | Value |
|---|---|
| bg | `#F8FBF4` → `#E7F0DE` (vertical) |
| mesh washes | `#D6F59A` (top-left), `#FFFFFF` (right), `#C9EDA8` (bottom-left) |
| surface / glass | `#FFFFFF` at 80 % |
| ink | `#14171A` |
| ink-soft | `#3D4640` |
| muted | `#79837C` |
| hairline | `#E4EADF` |
| accent | `#7FCF2E` |
| accent-2 | `#C6F36A` |
| CTA gradient | `#CBF56F` → `#9BDD40`, text `#14210A` |
| pastel chips | p1 `#F7EFC9→#F1E3A6` · p2 `#E3F6CE→#CDEEA9` · p3 `#FBE3EA→#F6D0DC` · p4 `#ECE7FB→#DCD4F5` · p5 `#DDF2EF→#C9E8E4` |
| track / segment | `#E6EDE0`, thumb `#14171A` |
| success | `#6BD39B` |

Two chromatic families maximum. Gradients only in the CTA and the hero wash.

## Type
- UI: **DM Sans** 400/500/600/700
- Display (inside the design preview): **Instrument Serif** 400
- Mono (code, numerals, meta): **Geist Mono** 400
- Scale (sp): display 42 · title 34 · h2 26 · h3 21 · card 17 · body 15 · small 14 ·
  chip 14 · caption 12 · eyebrow 11 (+0.12em tracking)
- Tracking: −0.035em on display, −0.04em on page titles. Line height 1.08 display,
  1.55 body.

## Shape
Two radii per screen: cards/sheets **28**, inner tiles and fields **14–20**; pills are
999. Sheets 34 (top corners), the device frame 38. Nested corners stay concentric.

## Spacing
4pt grid: 4 · 8 · 12 · 16 · 24 · 32 · 48 · 64. Screen side padding 16–22, section
rhythm 26 between groups, 14 inside, ≥48 of air above a primary CTA.

## Components
- **Primary CTA** — pill, 54dp, lime gradient, ink text, soft lime shadow.
- **Secondary** — white pill, 1dp hairline.
- **Glass surface** — white 80 %, 1dp white border, ink-tinted shadow (14dp).
- **Chip** — pill, pastel gradient or ink when selected.
- **Segmented** — track `#E6EDE0`, ink thumb that slides on a spring, white label when on.
- **Toggle** — 52×32, track `#D5DDD0` → lime when on, white 26dp knob.
- **Icon button** — 44dp glass circle, 22dp stroke icon at 1.6.
- **Bottom nav / preview tabs** — floating glass capsule; the active destination expands
  into an ink pill with its label.
- **Mascot** — glossy lime sphere, ink bar eyes, red cheeks when happy, canvas-drawn.

## Motion
Feedback 120ms · short 200 · medium 300 · long 450 · sheets 600.
Easings: ease `(.2,.8,.2,1)`, spring `(.34,1.2,.5,1)`, pill `(.32,.72,0,1)`,
pop `(.34,1.4,.4,1)`. Enter decelerates, exit accelerates at ~70 %. Reduced motion
is honoured by the platform.

## Avoid
- Indigo `#6366F1` and blue-purple gradients — the default AI tell.
- Pure black text or pure white page background.
- More than two radii, more than two font families, more than one accent.
- Emoji as icons; mixed icon sets.
- Placeholder copy, invented statistics, "Welcome to…" hero lines.
