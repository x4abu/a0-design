# Motion for native interfaces

Source: emilkowalski/skills (MIT) — motion layer, animate-expo and the mobile-native
checklist, plus DESIGN BRAIN Part 8.

Rules:
- Durations from the scale only: 50–200 feedback (~100), 250–400 transitions (most at
  200–300), 450–600 large surfaces, 1000ms ceiling.
- Enter = decelerate; exit = accelerate and faster than enter (~70%).
- Every tap gets feedback within 100ms; any wait over 400ms gets a skeleton that
  matches the final layout exactly.
- One orchestrated moment per screen. Ambient loops must not compete with content.
- Respect reduced motion: `@media (prefers-reduced-motion: reduce)` on web, and in
  Compose check the animator duration scale / provide a static fallback.
- Stress test: break the UI deliberately — long names, 100+ items, slow network — and
  fix what breaks.
