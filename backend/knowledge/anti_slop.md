# Anti-AI-slop contract

Slop is the statistical average of the training data — the most predictable output.
Every rule below is enforced twice: as prose for the model, and as code in
`backend/app/antislop.py` (deterministic, no API cost).

## The 36 hard bans (summary by group)

**Colour & gradients** — blue-purple gradients anywhere; indigo #6366F1 as accent;
pure #000 text or #FFF page; neon/coloured glows; more than two chromatic colours;
colour as the only signal for errors or status.

**Layout & spacing** — values off the 4pt scale; three identical feature cards in a row;
card-in-card nesting; centred everything on non-hero screens; screens under ~40% empty
space; more than two radii per screen.

**Typography** — Inter/Roboto as the only voice of a hero; more than two families or four
weights; all-caps body; body below 16px web / 14sp mobile; placeholder-only labels;
generic hero copy ("Welcome to…", "Your all-in-one solution", "Unlock the power of…").

**Components & content** — lorem ipsum and invented statistics; emoji as icons; mixed icon
sets; competing primary CTAs; destructive action styled primary; toast-only error handling;
modals for non-blocking information; infinite carousels as the primary pattern.

**Web** — abstract gradient/blob art instead of the real product; fake "trusted by" strips;
stock-photo people pointing at nothing; cookie-cutter 3-column pricing.

**Motion** — linear easing on position/scale; bounce on productivity UI; transitions over
1000ms; skeletons that don't match the final layout; no reduced-motion fallback.

**Android** — iOS glassmorphism cloned onto Android; iOS segmented controls copied verbatim;
touch targets under 48dp; primary CTA outside the thumb zone; fake numbers when a permission
is missing.

**Process** — quoting rule text in the UI; shipping a first draft without the review loop;
changing theme tokens mid-generation ("drift").

## The 15 deterministic checks

spacing · radius · fonts · accent · contrast · CTA · targets · placeholders · type scale ·
states · nav · motion · icons · shadows · content

Each returns PASS / WARN / FAIL with a specific reason. A FAIL means FIX-FIRST; more than
45% of checks failing means REGENERATE.

## The three AI-default aesthetics to actively avoid

1. Warm cream + serif + terracotta (pleasant, now the most copied "tasteful" look).
2. Near-black + acid green/vermilion accent (the "edgy dev tool" default).
3. Broadsheet hairlines, zero radius, dense columns (the "editorial" default).

Any of them is allowed when the brief genuinely calls for it — never as reflex.
