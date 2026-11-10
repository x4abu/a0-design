# Motion tokens

One scale for the whole product. A duration outside this table is a bug, not a choice.

## Durations (ms)
| Token | ms | Used for |
|---|---|---|
| instant | 0 | reduced-motion fallback, theme swap |
| feedback | 120 | press, tap ripple, haptic-coupled scale |
| micro | 150 | icon swap, checkbox tick, chip select |
| short | 200 | tooltip, small popover, colour crossfade |
| base | 250 | most state transitions |
| medium | 300 | card expand, list reorder |
| large | 450 | sheet, drawer, nav pill expansion |
| xlarge | 600 | full-screen presentation, modal |
| ambient | 900–4000 | idle loops (bob, breathe, shimmer, marquee) |
| ceiling | 1000 | nothing interactive exceeds this |

## Easings
| Name | Curve | Use |
|---|---|---|
| standard | `cubic-bezier(.2,.8,.2,1)` | general enter, screen push |
| settle | `cubic-bezier(.32,.72,0,1)` | sheets, drawers, nav — the iOS feel |
| enter | `cubic-bezier(0,0,0,1)` | decelerate, content arriving |
| exit | `cubic-bezier(.3,0,1,1)` | accelerate, content leaving |
| spring | `cubic-bezier(.34,1.2,.5,1)` | toggles, chips, playful controls |
| pop | `cubic-bezier(.34,1.4,.4,1)` | badges, success ticks, card arrival |
| linear | `linear` | progress bars, opacity-only fades, marquees |

## Compose springs (preferred over tween for anything touched)
```kotlin
val Settle  = spring<Float>(dampingRatio = 0.85f, stiffness = 380f)   // sheets, nav
val Snappy  = spring<Float>(dampingRatio = 0.9f,  stiffness = 900f)   // toggles, chips
val Gentle  = spring<Float>(dampingRatio = 1.0f,  stiffness = 220f)   // large surfaces
val Overshoot = spring<Float>(dampingRatio = 0.55f, stiffness = 600f) // playful, 2–4% only
```

## Rules
1. Enter decelerates, exit accelerates, and exit is ~70% of the enter duration.
2. Nothing interactive exceeds 1000ms; feedback must land inside 100ms.
3. Stagger 40–70ms per item, never more than 6 items in one sequence.
4. One orchestrated moment per screen. Ambient loops must never compete with content.
5. Animate transform and opacity (and blur on API 31+). Never animate layout bounds in a
   list — use `animateItemPlacement()`.
6. Honour the system animation scale: at 0, render final states instantly.
7. Blur budget: one blurred surface at a time, radius ≤ 16dp on mobile (cost).
