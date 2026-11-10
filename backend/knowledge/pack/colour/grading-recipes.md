# Colour grading (how a flat palette becomes "expensive")

Grading = the small, deliberate adjustments that make a palette feel lit rather than filled.
Do these in the token layer, never per-screen.

## 1. Warm the canvas, cool the ink
Never a neutral grey canvas. Shift the bg 2–4% toward yellow/red (`#F8FBF4` has a green
cast; `#F6EFE4` a cream cast). Shift the ink 2% toward blue-black (`#14171A`), never pure.
Result: everything looks lit by one light source.

## 2. One light direction
Pick top-left. Every highlight, glass edge and gradient starts top-left and ends
bottom-right (`135deg`). Shadows fall bottom-right. Mixed light directions are the fastest
way to look amateur.

## 3. Elevation as tint, not black
Shadow colour = ink at 8–14% alpha, large blur, negative spread:
`0 14px 32px -16px rgba(74,106,58,.40)`. Never `rgba(0,0,0,.25)` — that reads as 2014.

## 4. Gradient discipline
Two stops, same hue family, ≤ 25% lightness delta, 135°, only on: the primary CTA, one hero
surface, one text word. Never on cards, never full-bleed backgrounds, never on text below
18sp.

## 5. Tint ramps for depth
For any surface stack: base, +4% white, +8% white. For recessed: −4% ink. This gives you
three elevation levels without a single shadow.

## 6. Photography/art direction (if you use images)
Warm-white balance, desaturated by 8–12%, lifted blacks (+6), no HDR look, no blue shadows.
One treatment for every image in the app — a mismatch between two photos is more visible
than a bad single photo.

## 7. Dark mode is graded, not inverted
Don't invert. Take bg `#1E1916`, surface `#2C2520`, hairline `#3A322C`; drop the accent's
saturation 10%; keep shadows but at 30% of their light-mode strength, and add a 1px top
highlight on raised surfaces.

## 8. Contrast budget per screen
One high-contrast pair (ink on bg), one mid (ink-soft on surface), one accent moment.
If a screen has three high-contrast elements, the eye has nowhere to land.
