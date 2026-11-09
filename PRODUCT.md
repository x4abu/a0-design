# a0 design — product

**Audience.** Designers and developers who want a designed, production-shaped UI from a
sentence, on a phone, without opening a laptop. Comfortable with API keys; allergic to
templates.

**Job.** "Describe what you want to design" → get a coherent, editable design system
plus the code, then refine it by tapping the thing you want changed.

**What makes it different.** The anti-slop contract. Most generators produce the
statistical average of their training data — indigo gradients, three identical feature
cards, placeholder copy. a0 assembles a locked theme, a screen archetype and a component
vocabulary from the Design Brain, then runs fifteen mechanical checks before anything
is shown. Failures are named, not smoothed over.

**Core loop.**
1. Type a brief (or tap a suggestion). Auto decides everything; Plan asks two questions
   and shows the plan before building.
2. Watch the six-step build (understanding → planning → palette → screens → code →
   polish), with a stop control.
3. Open the preview: tap any element to make it rounder, bigger, recolour it, or
   annotate the change in words.
4. Retune the system in the Style tab — accent, typeface, roundness, light/dark,
   wireframe — and see it live.
5. Compare or restore any earlier version in History.

**Constraints we hold.**
- Bring your own model key. It is verified with the provider, then encrypted into the
  Android Keystore; nothing is proxied without consent.
- No seeded content. Projects, usage and history are empty until you actually generate
  something, and every number on screen came from a real API response.
- Honest failure: no key, a 401, a rate limit or unreadable JSON are all shown verbatim
  with the action that fixes them. The app never substitutes a demo design.
- Tokens only. No hex, radius or duration exists outside `Tokens.kt`.
- Android-native: Material 3 shapes, 48dp targets, thumb-zone primary actions.

**Out of scope (v1).** Multi-user sync, Figma import beyond the attach flow, billing.
