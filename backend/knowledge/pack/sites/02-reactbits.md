# React Bits — animated components you own

**What it is.** Open-source collection of memorable UI elements in five families —
**Text Animations (33)**, **Animations (38)**, **Components (48)**, **Backgrounds**,
**Micro** interactions — each shipped in 4 variants (JS/TS × CSS/Tailwind). Install via
CLI (`npx shadcn@latest add https://reactbits.dev/r/SplitText-JS-CSS`) or copy the source.

**Why it matters.** This is the single best vocabulary of *named* motion effects. Use the
names as a shared language with an AI model: "Split Text entrance, 60ms stagger,
decelerate" is unambiguous.

## Text animations (all 33, with the intent)
ASCII text · Blur text (blur → crisp resolve) · Circular text · Count up · Curved loop ·
Decrypted text · Depth text (extruded parallax) · Echo text (ghosts settle) · Falling
text · Fold text (creases unfold) · Fuzzy text · Glitch text · Gradient text (sweep /
mesh / conic) · Masked heading · Particle text · Rotating text (3D flip) · Scrambled
text (cursor distortion) · Scroll float · Scroll reveal (unblur) · Scroll velocity
marquee · Shiny text (light sweep) · Shuffle (chars shuffle then settle) · Split-flap
text · Split text (char/word stagger) · Stroke text (draw then fill) · Tech text (letters
become dashed vectors) · Text cursor trail · Text loop (curved marquee) · Text pressure
(warp by pointer) · Text type (typewriter + caret) · True focus (blur/clarity over a
series) · Variable proximity (letter styling by pointer distance) · Warp text.

**Which of these belong in a premium mobile app.** Blur text, Split text, Text type,
Rotating text, Count up, Shiny text, True focus, Scroll reveal, Gradient text (text
only, one hue family). Avoid glitch, ASCII, particle and warp on Android — they read as
demo effects, not product.

## Animations & components worth porting
Animated content (scroll reveal wrapper) · Fade content · Gradual blur (edge blur for
headers/footers — the iOS scroll-edge effect) · Animated list (rows spring in, selection
glides) · Card nav (nav springs open into cards) · Pill nav (fill rises through the
link, label rolls, sliding active dot) · Gooey nav · Stepper (gliding step indicator) ·
Bubble menu (radial expand) · Stack / Card swap (3D card stack) · Tilted card · Spotlight
card · Pixel card · Dock (macOS magnification) · Option wheel · Accordion gallery.

## Compose translation table
| React Bits | Compose equivalent |
|---|---|
| SplitText stagger | `AnimatedVisibility` per char + `delay(60L * i)` |
| BlurText | `Modifier.blur(radius)` animating 12dp → 0dp (API 31+) |
| TextType | `rememberInfiniteTransition` or a coroutine that reveals `text.take(n)` |
| CountUp | `animateIntAsState` + `Text` with tabular figures |
| GradualBlur | `Brush.verticalGradient` scrim over a `LazyColumn` edge |
| PillNav | Row of pills, `animateFloatAsState` weight + label fade |
| AnimatedList | `LazyColumn` + `animateItemPlacement()` + per-row enter spring |
| TiltedCard | `graphicsLayer { rotationX/Y }` from pointer drag |
| SpotlightCard | radial gradient drawn at pointer offset |

**Licence.** Free and open source (MIT-style per component). Link: https://reactbits.dev
