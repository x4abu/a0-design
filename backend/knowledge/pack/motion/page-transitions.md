# Screen transitions (the iOS feel)

## Push (forward)
```
incoming: alpha 0→1, translateX +14dp→0, scale .985→1, blur 8dp→0  · 420ms enter
outgoing: alpha 1→0, translateX 0→−10dp, scale 1→.99, blur 0→6dp      · 220ms exit
```
Compose: `AnimatedContent` with `fadeIn(tween(420, easing = Ease)) + slideInHorizontally { it / 14 } + scaleIn(0.985f)`; the blur term needs API 31+ (`Modifier.blur`) and should be
skipped below it.

## Pop (back)
Reverse of push, but faster: 280ms in, 180ms out, no blur on the incoming screen.

## Present (modal / sheet)
Never slide a modal horizontally. Use the bottom sheet spec (600ms settle) or a
scale-up card (`scaleIn(.92f)` + fade 260ms) with the background scaling to .98.

## Tab switch (same level)
Crossfade 180ms + 6dp slide in the direction of the index delta. No scale, no blur —
same-level movement must feel instant.

## Onboarding step
Horizontal slide 32dp + fade 260ms, dots that interpolate width (8dp → 22dp) on a spring,
back gesture reverses the direction. Never a stack of modals.

## Splash → content
Logo scales 1→1.06 and fades out 240ms while content fades in 300ms with an 80ms delay.
Total under 700ms. If the app is ready sooner, cut the splash short — never pad it.

## Shared element
`SharedTransitionLayout` + `Modifier.sharedElement(rememberSharedContentState(key), animatedVisibilityScope)`.
Keep the shared pair to one image + one title; morphing everything looks like a demo.

## Reduced motion
At animation scale 0: no blur, no translate, no scale — only a 120ms crossfade, or an
instant swap for navigation.
