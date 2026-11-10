# Component catalogue — from the reference sites to Compose

Every row: what it is, where it comes from, what it becomes on Android. Use this as the
build menu. `priority` = P0 (ship in v1), P1 (next), P2 (nice).

## Navigation
| Component | Source | Compose translation | Priority |
|---|---|---|---|
| Expanding bottom nav pill | a0 / React Bits PillNav | Row + animated weight + label fade | P0 |
| Card nav (nav opens into cards) | React Bits `CardNav` | `AnimatedVisibility` + staggered cards | P2 |
| Gooey active indicator | React Bits `GooeyNav` | Custom `Canvas` + `Path` blob between targets | P2 |
| Floating drawer with scale-behind | 60fps shared element | `AnimatedVisibility` + `graphicsLayer` scale | P0 |
| Collapsing large title | Mobbin (iOS convention) | Scroll offset → crossfade two titles | P0 |
| Tabs with sliding underline | Magic UI / Mobbin | `animateDpAsState` on an underline bar | P1 |

## Text
| Component | Source | Compose translation | Priority |
|---|---|---|---|
| Split word entrance | React Bits `SplitText` | per-word `AnimatedVisibility` + delay | P0 |
| Blur resolve | React Bits `BlurText` | `Modifier.blur` 12→0 (API 31+) | P0 |
| Typewriter + caret | React Bits `TextType` | coroutine reveal + blinking caret | P0 |
| Rotating words | React Bits `RotatingText` | `graphicsLayer.rotationY` flip | P1 |
| Count up / number ticker | Magic UI `NumberTicker` | `animateIntAsState` + tabular figures | P0 |
| Shiny sweep | Magic UI `ShinyText` | `Brush.linearGradient` as text brush | P1 |
| True focus (blur siblings) | React Bits `TrueFocus` | per-word blur + alpha | P2 |
| Split-flap counter | React Bits `SplitFlapText` | per-digit vertical `Animatable` | P2 |

## Controls
| Component | Source | Compose translation | Priority |
|---|---|---|---|
| Spring toggle | Uiverse switches | `animateDpAsState` + colour crossfade | P0 |
| Segmented pill | a0 / Magic UI | BoxWithConstraints + animated thumb | P0 |
| Real glass toggle | Vibrant "Real Glass Light Toggle" | gradient track + specular highlight | P1 |
| Specular / liquid-glass button | 21st liquid-glass button | `drawWithContent` + pointer highlight | P1 |
| Pulsating CTA | Magic UI `PulsatingButton` | infinite ring scale + fade | P2 |
| Clamped slider | Vibrant clamped slider | custom thumb + bubble clamp | P1 |
| Option wheel | React Bits `OptionWheel` | `LazyColumn` with rotation per item | P2 |

## Surfaces
| Component | Source | Compose translation | Priority |
|---|---|---|---|
| Glass card | a0 | white 82% + 1dp white border + ink shadow | P0 |
| Spotlight card | React Bits `SpotlightCard` | radial gradient at pointer | P1 |
| Tilt card | React Bits `TiltedCard` | `rotationX/Y` ≤ 8° + spring return | P1 |
| Bento grid | Magic UI `BentoGrid` | `LazyVerticalGrid` with spans | P0 |
| Card stack | React Bits `Stack` / `CardSwap` | `graphicsLayer` z-stack + fling | P2 |
| Progressive blur edge | Magic UI / iOS | gradient scrim + blurred strip | P0 |
| Noise / grain overlay | Magic UI `NoiseTexture` | tiled 64px PNG at 4% alpha | P2 |

## Feedback & states
| Component | Source | Compose translation | Priority |
|---|---|---|---|
| Shimmer skeleton | Uiverse loaders | animated `Brush.linearGradient` | P0 |
| Success burst | 60fps success state | scale pop + 6–8 particles | P0 |
| Coachmark pulse ring | 60fps | infinite ring scale 1→1.5 + fade | P1 |
| Inline field error | Mobbin forms | shake + red text + icon | P0 |
| Undo snackbar | Mobbin delete flows | row collapse + 4s snackbar | P1 |
| Pull-to-refresh arc | 60fps / React Bits | custom indicator, no stock spinner | P2 |

## Layout archetypes (from Land-book / Landdding / Mobbin)
| Archetype | Where seen | Structure |
|---|---|---|
| Big-type hero | Land-book "Big Type" | 96–160px display, 2–3 lines, one CTA, generous air |
| Poster footer | Land-book "Big Type in Footer" | brand at 20vw, cropped, legal links tiny |
| Bento features | 21st / Magic UI | 5–7 tiles, 2 spans, one tile holds the product shot |
| Editorial split | Landdding studio sites | full-bleed image + 720px text column alternating |
| Phone-in-hero | Landdding fintech | real device frame showing the actual flow |
| Stat-led landing | Landdding | one huge number as the hero, three supporting lines |
