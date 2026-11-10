# Text animations — 12 that belong in a premium product

All timings assume the tokens in `motion-tokens.md`. Each recipe is given as
**name → intent → spec → Compose sketch**.

## 1. Split entrance (React Bits `SplitText`)
Intent: a headline that arrives with intent, not a fade.
Spec: per word (not per char — chars read as a demo), translateY 14dp→0, opacity 0→1,
60ms stagger, 420ms enter curve. Cap at 12 words.
Compose: `AnimatedVisibility` per word with `delay(60L * i)`, or one `Animatable` driving
an index and `graphicsLayer { alpha; translationY }`.

## 2. Blur resolve (`BlurText`)
Intent: soft-focus reveal, the most "expensive" entrance there is.
Spec: blur 12dp→0, opacity 0→1, 520ms enter. Optional 60ms stagger per line.
Compose: `Modifier.blur(radius)` (API 31+) inside `graphicsLayer`-friendly composable; below
API 31 fall back to opacity + translateY (never a fake blur).

## 3. Typewriter (`TextType`)
Intent: a live, honest "the model is writing" feel.
Spec: 12–18ms per character, caret 1px, blinking at 530ms on/off, caret disappears 400ms
after completion. Never type more than 3 lines.
Compose: coroutine revealing `text.take(n)`; caret = `AnimatedVisibility` with an infinite
alpha keyframe.

## 4. Rotating words (`RotatingText` / Magic UI `WordRotate`)
Intent: one slot, several truths ("design / ship / refine").
Spec: 3D flip on Y: rotateY 0→-90 (exit 200ms) then 90→0 (enter 260ms), 1.8s dwell.
Compose: `graphicsLayer { rotationY; cameraDistance = 12 * density }`.

## 5. Count up (`CountUp` / `NumberTicker`)
Intent: numbers that feel measured.
Spec: 900ms, enter curve, tabular figures, no bounce; format with locale separators.
Compose: `animateIntAsState` or `Animatable` + `Text` with `fontFeatureSettings = "tnum"`.

## 6. Shiny sweep (`ShinyText`)
Intent: attention on a single label (a badge, a live state).
Spec: a 40%-wide highlight crossing the text in 2.4s, linear, 3s pause, max 3 cycles.
Compose: `Brush.linearGradient` as the text brush, animated `start` offset.

## 7. Gradient drift (`GradientText`)
Intent: one hero word with life. Use once per app, text only.
Spec: two stops from the same accent family, 8s linear alternate, 20% drift.
Banned: blue→purple, rainbow, any second hue family.

## 8. True focus (`TrueFocus`)
Intent: emphasis on one word of a sentence at a time.
Spec: non-active words blur 4dp + alpha .45; the active word unblurs over 300ms.
Compose: per-word `Modifier.blur` + alpha, driven by a ticker index.

## 9. Scroll reveal (`ScrollReveal`)
Intent: content arrives as you read.
Spec: opacity 0→1, translateY 24dp→0, blur 6→0, triggered at 20% visibility, 480ms.
Compose: `LazyListState` offset check or `Modifier.onGloballyPositioned` + `derivedStateOf`.

## 10. Character shuffle (`Shuffle`)
Intent: a state change worth noticing (a value recomputing).
Spec: 3 random glyph frames at 40ms, then settle 180ms. Max 12 characters, max once per 5s.
Compose: coroutine swapping a random char, then the real one.

## 11. Stroke draw (`StrokeText`)
Intent: a wordmark or a single hero word, once, on first launch.
Spec: path draw 700ms linear, then fill fades in 200ms.
Compose: `PathMeasure` on a `Canvas` — do not animate this on every recomposition.

## 12. Marquee / velocity (`ScrollVelocity`)
Intent: a single strip (a category rail), never the main content.
Spec: 28s linear loop; on fast scroll, speed ×1.4 and skew ±3° for 300ms.
Compose: `rememberInfiniteTransition` + `graphicsLayer { translationX }`.

## Never
Glitch, ASCII, particle text, warp text, comic text, "decrypted" hacker text — these read
as a portfolio demo. A premium product uses at most three of the twelve above.
