# Loaders, skeletons, progress

## Skeleton
- Must match the final layout exactly: same rows, same heights, same radii. A skeleton that
  shifts the layout on load is worse than no skeleton.
- Shimmer: 1.4s linear loop, a 40%-wide highlight sweeping left→right, colours
  `#E6EDE0 → #FFFFFF → #E6EDE0`. Never a pulsing opacity (that reads as a broken screen).
- Show after 300ms of waiting, not immediately (instant flashes are noise).
- Compose: `rememberInfiniteTransition` on a `Brush.linearGradient` start offset.

## Spinner (inline, in a button)
- 16dp arc, 2dp stroke, 270° sweep, 800ms linear rotation, accent colour.
- The button keeps its width; the label crossfades to the spinner in 150ms.

## Progress
- Determinate: 8dp track, pill, accent gradient fill, width animates on the enter curve.
- Indeterminate: a 30%-wide segment travelling a track in 1.1s linear — never a spinning
  full ring for anything longer than a second.
- Long tasks (> 4s): show named steps with check marks (real steps, real order), not a bar.

## Success / error states
- Success: a 700ms celebration — icon scales 0→1.15→1 on a pop spring, a single ring
  expands and fades, 6–8 particles max, then everything settles. Haptic `CONFIRM`.
- Error: shake 2 cycles, 4dp amplitude, 220ms, plus a red-tinted inline message. Never a
  toast for a field-level error.

## Ambient / idle
- A mascot or logo may breathe (scale 1↔1.03) over 4s and blink every ~5s.
- One ambient loop per screen. If two things move at rest, the screen feels nervous.
