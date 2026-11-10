# Motion discipline (grounded)

Adapted from OpenDesign's `craft/animation-discipline.md` (Apache-2.0), which is itself
sourced from primary research. This file decides **whether** motion should run; the
`motion/` folder decides what it looks like.

## Animate only for space, time or state
Tversky, Morrison & Bétrancourt (IJHCS 2002) reviewed every study claiming animation aids
comprehension and found the controls were broken — with equal information, animation does
**not** beat static. The one endorsed use is real-time spatial/temporal reorientation:
page transitions, container morphs, viewpoint changes, progress.

So: animate navigation, container expansion, progress, and gesture follow-through.
Do **not** animate to teach, to decorate, to signal "premium", or to fill silence.

## Durations that are actually converged
150 ms is the cross-system default for state confirmation (Material 3 `short3`, Carbon
`moderate-01`, Polaris 150, Tailwind default, SLDS `duration-fast`).

| Range | Use |
|---|---|
| 50–100 ms | instant feedback: press, toggle commit, hover |
| 150 ms | default state confirmation |
| 200–300 ms | entering UI: sheets, dialogs, dropdowns |
| 300–500 ms | cross-screen transitions, container morphs |
| > 500 ms | only staged/cross-screen or platform-native transitions |

- Frequent animations (seen 50× per session) stay ≤ 200 ms.
- **Mobile runs 20–30% shorter than desktop** — travel distances are shorter.
- Nothing interactive over 500 ms. Ever.

## Curve vs spring
- **Curve** for opacity, colour, and any property moving between two known values.
- **Spring** for position, scale, rotation and gesture-driven motion — anything physical.
- Material 3 standard easing is `cubic-bezier(0.2, 0, 0, 1)` (front-loaded). The symmetric
  `cubic-bezier(0.4, 0, 0.2, 1)` is M2, kept in M3 only as `legacy`. Don't call M2 "M3".
- M3 `emphasized` is a two-segment Bézier, not one cubic — a single cubic silently loses
  its front-loaded character.
- Apple's published SwiftUI default spring: **response 0.5 s, dampingFraction 0.825**.
  The widely quoted `.snappy = 0.25s` is wrong — all three presets share a 0.5 s base and
  differ only in bounce (0 / 0.15 / 0.3).
- Framework "defaults" disagree wildly: motion.dev default is ζ ≈ 0.5 (bouncy), React
  Spring `default` is ζ = 0.997 (critically damped). Pick consciously.

## Reduced motion
Strip motion on an axis (translate, scale, rotate, parallax). Keep opacity/colour
crossfades as the substitute when a state change still needs conveying. View Transitions
does **not** apply `prefers-reduced-motion` for you.

## Flashing and loops
- WCAG 2.3.1 (A): no more than three flashes in any one second. Treat it as a hard floor.
- Any motion running > 5 s needs a pause control (WCAG 2.2.2, Level A).
- Cap loops: carousels 3–5 cycles then pause; shimmer only until content lands.
- Cancel ambient motion on route change. Reward bursts fire once, never on a timer.
- Spinners must not run forever — escalate to progress/cancel by 60 s.

## Platform notes
- **Android**: M3 motion tokens (50–1000 ms), cubic-beziers, predictive back is a
  gesture-progress primitive (`BackEvent.progress` sampled per frame), not a transition.
- **iOS**: spring `(response, dampingFraction)`; HIG documents principles, not numbers.
- **Web**: View Transitions defaults to 0.25 s with no easing specified; author the
  reduced-motion override yourself.

## Lint list (mistakes to catch in review)
- More than 500 ms on a non-cross-screen transition.
- Animation performing a state change instead of confirming one (optimistic UI first).
- Animation as the only signal of change — always pair with colour, position or a label.
- Hero choreography inside a productivity tool; decorative motion in a working canvas.
- Curve easing on a `scale()` that should feel physical.
- Quoting "skeleton screens feel 11% faster" — that study measured determinate progress
  bars (Harrison/Yeo/Hudson, CHI 2010), not skeletons.
