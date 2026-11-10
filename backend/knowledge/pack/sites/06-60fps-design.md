# 60fps.design — a taxonomy of delightful details

**What it is.** A curated collection of interaction details from best-in-class apps
(Apple, Revolut, CRED, Duolingo, Threads, Circle, Linear-adjacent products), indexed by
**gesture**, **pattern**, **effect** and **element**. This taxonomy is the most useful
thing on the site: it is the checklist of what "delightful" is made of.

## Gestures
Drag · flick · flip · gyroscope · hold · long-press · pan · pinch · pull · scroll · scrub ·
slide · swipe · tap · typing.

## Patterns
3D · assistant · card stack · carousel · chat · delete · empty state · featured · gallery ·
gamification · get started · goal · idle animation · intro · invite · loading · logo ·
mascot · onboarding · pricing · quick actions · reaction · recap · reward · setup ·
shared element · showcase · splash · stats · streak · success state · theme · timer ·
voice · weather · workout.

## Effects
Blur · bounce · colour · confetti · draw · fade · glow · grow · morph · orbit · parallax ·
particles · pulse · reveal · sequence · shimmer · show/hide · shrink · skeuomorphic ·
sound · spin · spring physics · stagger · ticker · wiggle · zoom.

## Elements
Badge · bottom sheet · button · calendar · card · counter · easter egg · graph · icon ·
illustration · input · liquid glass · map · menu · permissions · picker · player ·
progress · search · slider · story · tabs · text · toast · tooltip · video · widget.

## The ten details that separate a $1 app from a $1B app
1. **Shared element** — the card you tap *becomes* the detail screen. (Compose:
   `SharedTransitionLayout` + `Modifier.sharedElement`.)
2. **Spring physics everywhere** — no linear, no ease-in-out defaults; overshoot of 2–4%.
3. **Liquid glass** — translucent surfaces that refract, with a 1px specular top edge.
4. **Counter/ticker** — numbers that roll rather than swap.
5. **Success state** — a 700ms celebration (scale pop + particle burst), then settle.
6. **Splash choreography** — logo draws, then content fades under it.
7. **Coachmark pulse** — a soft ring pulse on the one thing to notice, twice, then stops.
8. **Idle animation** — a mascot that blinks and breathes; it makes the app feel alive.
9. **Delete** — the row shrinks, collapses and the list re-flows with a spring; undo toast.
10. **Pull to refresh** — custom arc that fills with progress, not a generic spinner.

**Licence.** Reference/inspiration only — watch and learn, do not copy assets.
Link: https://60fps.design
