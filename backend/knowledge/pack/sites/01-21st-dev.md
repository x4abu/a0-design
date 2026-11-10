# 21st.dev — component registry (12,000+ community components)

**What it is.** A registry, not a library: many authors publishing React + Tailwind +
shadcn components, templates and themes. Each component is real code you copy into your
repo (shadcn CLI / "Copy prompt"), so you own it. Components inherit your design tokens
instead of shipping their own theme.

**Why it matters for a0.** It is the antidote to the "average of every hero section"
problem. Ten heroes there look nothing alike. Use it as a *shape catalogue*: read the
structure, rebuild it with your own tokens.

## Structure to steal (category → what it gives you)
| Category | Take |
|---|---|
| Heros | Scroll-media expansion, container-scroll animation, image-stream hero, scroll-locked video |
| Calls to action | Liquid-glass button, specular edge buttons |
| Pricing | Single card, pricing module, "squishy" pricing, us-vs-them comparison |
| Testimonials | 3D testimonial stacks, alternating rows with stats |
| Text | Handwriting SVG, streaming text, hover preview |
| Shaders | Liquid glass logo reveal, Siri wave, voice orb, water-ripple image |
| AI surfaces | AI chat input, agent trace, streaming text |

## Reusable patterns worth building
1. **Container-scroll hero** — a laptop/phone mockup rotates from 20° to 0° while the
   page scrolls; content scales 0.8 → 1.0. Compose: `graphicsLayer { rotationX = ... }`
   driven by scroll offset, `cameraDistance` set.
2. **Scroll media expansion** — a small image expands to full-bleed as it enters the
   viewport. Compose: `animateFloatAsState` on `LazyListState` item offset.
3. **Liquid glass button** — a translucent pill with a specular highlight that follows
   the pointer. Compose: `drawWithContent` + radial highlight at the pointer position.
4. **Agent trace** — stacked step rows that light up in sequence with a hairline rail.

**Do not copy** the shader/WebGL pieces into Android; approximate with gradients and
blur, or skip them. Glass + spring beats fake 3D on a phone.

**Licence / attribution.** Community authors, per-component licences; code is copied
into your repo. Link: https://21st.dev/community/components
