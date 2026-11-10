# Magic UI — 150+ animated components for design engineers

**What it is.** Free, open-source animated components built with React, TypeScript,
Tailwind and Motion, designed to sit beside shadcn/ui. Focus: *landing-page and marketing
motion* done tastefully.

## The full component list (use as a menu)
Android · Animated beam · Animated circular progress bar · Animated gradient text ·
Animated grid pattern · Animated list · Animated shiny text · Theme toggler · Aurora text ·
Avatar circles · Backlight · Bento grid · Blur fade · Border beam · Code comparison ·
Comic text · Confetti · Cool mode · Dia text reveal · Dither field · Dock · Dot pattern ·
Dotted map · File tree · Flickering grid · Floating 3D particles · Glare hover · Globe ·
Glyph matrix · Grid pattern · Hero video dialog · Hexagon pattern text · Highlighter ·
Hyper text · Icon cloud · Interactive grid pattern · Interactive hover button · iPhone ·
Kinetic text · Lens · Light rays · Line shadow text · Magic card · Marquee · Meteors ·
Morphing text · Neon gradient card · Noise texture · Number ticker · Orbiting circles ·
Particles · Pixel image · Pointer · Progressive blur · Pulsating button · Rainbow button ·
Retro grid · Ripple button · Ripple · Safari · Scroll based velocity · Scroll progress ·
Shimmer button · Shine border · Shiny button · Smooth cursor · Sparkles text · Spinning
text · Striped pattern · Terminal · Text 3D flip · Text animate · Text reveal · Tweet card ·
Typing animation · Video text · Warp background · Word rotate.

## The six that make an app feel expensive
1. **Progressive blur** — a gradient blur that ramps toward a screen edge. This is the
   iOS nav-bar-under-scroll effect; port it first. Compose: a 3-stop vertical gradient
   scrim over content, plus `Modifier.blur` on a copy of the top strip (API 31+).
2. **Number ticker / animated circular progress** — numbers that roll, rings that fill.
3. **Blur fade** — the universal entrance: opacity 0→1 + translateY 8dp + blur 8→0.
4. **Shine border / glare hover** — a 1px light that travels the border of a card.
   Use on the *one* primary card per screen, never on all.
5. **Bento grid** — asymmetric feature grid; kills the "three identical cards" cliché.
6. **Text reveal / word rotate** — headline arrives word by word, 40ms apart.

## What to avoid
Rainbow button, neon gradient card, meteors, warp background, comic text — these are
demo-bait and violate the palette rules. Confetti only for a genuine success moment.

**Licence.** MIT. Link: https://magicui.design/docs/components
