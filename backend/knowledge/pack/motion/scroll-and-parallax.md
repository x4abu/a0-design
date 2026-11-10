# Scroll, parallax and depth

## Scroll-edge blur (progressive blur)
The single most "iOS" detail: content blurs as it approaches the top bar.
Spec: a 64dp strip at the top, blur ramping 0→10dp across it, plus a 0→18% ink scrim.
Compose: draw a vertical gradient scrim over the list; for real blur, render the top strip
in a second composable with `Modifier.blur(radius)` and a gradient mask (API 31+).

## Parallax header
- Header image translates at 0.4× scroll speed; the title moves at 1× until it pins.
- Scale: 1.0 → 1.08 as you pull down (overscroll), never on scroll up.
- Compose: `graphicsLayer { translationY = scroll * 0.4f }` inside a `LazyColumn` header.

## Sticky section headers
- Pin at 0, background turns from transparent to 92% surface with a 200ms crossfade as it
  pins; the previous header's title fades out simultaneously.

## Reveal on scroll
- Threshold: 20% visible; translateY 24dp, opacity 0→1, 480ms enter curve; once per item
  (do not re-animate on scroll up).
- Stagger within a group: 50ms, max 6 items.

## Velocity-linked effects (use sparingly)
- Marquee speed ×1.4 and skew ±3° while scrolling fast, settling in 300ms.
- Never distort body text; only rails, dividers and decorative strips.

## Depth / 3D
- Card tilt: max 8° on both axes, `cameraDistance = 12 * density`, spring return 400ms.
- Container scroll: rotateX 20° → 0° across the hero's scroll range, with `cameraDistance`
  set high enough to avoid clipping.
- Anything more than 8° reads as a toy on a phone.

## Pull to refresh
- Custom arc: 40dp ring, accent stroke 2.5dp, fills with pull progress (0→1 at 80dp),
  then spins 900ms while loading, then completes to 100% and fades 200ms.
- Haptic tick at the threshold. Never a stock spinner in a premium app.
