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
- Bring your own model key. Keys stay on the device; nothing is proxied without consent.
- Honest states: when the backend is unreachable the app says so and keeps working
  offline; when the server has no key it returns a real design plan, not fake output.
- Tokens only. No hex, radius or duration exists outside `Tokens.kt`.
- Android-native: Material 3 shapes, 48dp targets, thumb-zone primary actions.

**Out of scope (v1).** Multi-user sync, Figma import beyond the attach flow, billing.
