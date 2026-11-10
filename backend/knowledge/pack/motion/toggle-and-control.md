# Toggles, switches, segmented controls, chips, sliders

## Toggle / switch
Anatomy: track 52×32, radius pill, knob 26dp with a 2dp ink-tinted shadow.
- Track colour crossfade: off `#D5DDD0` → on `#7FCF2E`, 300ms standard curve.
- Knob travel: x 3dp → 22dp, **450ms settle spring** (damping 0.85, stiffness 380).
- Overshoot: 2–4% maximum. Anything bouncier reads as a toy.
- Press: whole control scales to .97 for 120ms, released on tap-up.
- Optional icon morph inside the knob (sun→moon) crossfades at the midpoint of the travel.
- Haptic: `KEYBOARD_TAP` on state change, only if haptics are enabled.
Compose sketch:
```kotlin
val x by animateDpAsState(if (on) 22.dp else 3.dp, spring(0.85f, 380f))
Box(Modifier.size(52.dp, 32.dp).clip(CircleShape)
    .background(animateColorAsState(if (on) Accent else Track, tween(300)).value, CircleShape)) {
  Box(Modifier.offset(x = x, y = 3.dp).size(26.dp).shadow(2.dp, CircleShape).background(Color.White, CircleShape))
}
```

## Segmented control
- Track: `#E6EDE0`, 4dp padding, pill.
- Thumb: ink fill, slides on a 450ms settle spring, width = track / segments.
- Label: colour crossfade 200ms; the active label goes white and 600 weight.
- Tap on the already-active segment: no animation (never re-trigger).
- Optional: 3dp scale on the thumb when a new segment is chosen (spring back in 200ms).

## Chips
- Height 36–44dp, pill, pastel tint or white with a hairline.
- Selected: ink fill + white text, or accent tint + accent-ink text. One of the two, always.
- Add/remove: `scaleIn(.9f)` + fade 180ms; removal collapses width first (200ms) then fades.

## Slider
- Track 8dp, pill; active portion is a gradient of the single accent family.
- Thumb 22dp white with a 1dp hairline + shadow; grows to 26dp while dragging (150ms).
- Value bubble clamps inside the track bounds at the ends (never overflows).
- Step marks: 2dp dots at 30% ink, only when the range is discrete.
- Haptics: one tick per step crossed, not per pixel.

## Checkbox / radio
- 22dp box, radius 7, 1.5dp hairline; checked = accent fill + white tick that draws in
  200ms (`PathMeasure`), scale 1→1.08→1 on completion.

## Buttons
- Primary: pill, 54dp, accent gradient, ink text; press scale .96 + 120ms; no ripple.
- Secondary: white pill, 1dp hairline; press = 6% ink overlay, no scale.
- Text button: label + chevron that slides 3dp right on press.
- Disabled: 38% opacity, no shadow, no press animation.
- Loading: label fades out, a 16dp arc spinner fades in (180ms), width unchanged.
