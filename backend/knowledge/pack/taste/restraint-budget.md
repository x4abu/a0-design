# Restraint budget

Premium is subtraction with a reason. These are hard budgets per screen; going over is a
bug, not a style choice.

| Resource | Budget per screen | Why |
|---|---|---|
| Accent uses | **2** | one for action, one for state. A third means decoration. |
| Gradients | **1** | only on the CTA, one hero surface, or one word |
| Blurred / glass surfaces | **1** | glass on glass reads as 2021 |
| Shadowed surfaces | **3** | beyond that, use a hairline or a tint step |
| Radius values | **2** | e.g. 28 cards + pill; nested corners stay concentric |
| Font families | **2** (+ mono for code) | one display voice, one UI voice |
| Weights | **4** | 400/500/600/700 |
| Type sizes | **5** | display, title, body, small, caption |
| Motion effects | **2** | one entrance, one interaction |
| Ambient loops | **1** | and it must be the mascot or nothing |
| Animated elements per viewport | **1** | the rest stay still |
| Icons | **1 family**, 1 stroke width | mixing sets is visible instantly |
| Colours | **1 accent + neutrals + semantic** | no second chromatic family |
| Text on a tinted surface | 1 ink step | never black on pastel |

## Budget accounting (do this while building)
Write the budget at the top of the file as a comment and tick items off:
```
// budget: accent 2/2 · gradient 0/1 · blur 1/1 · shadows 2/3 · radii 2/2 · motion 2/2
```
When you want a third accent use, the answer is not "it's small" — it is "what can I remove
instead".

## Where to spend instead
If a screen feels flat after the budget is spent, do not add decoration. Add one of:
1. **Scale** — make the headline 20% bigger and cut a section.
2. **Space** — double the padding above the primary action.
3. **Contrast** — one element at full ink while everything else sits at 60%.
4. **One image or one illustration** at proper size.
5. **One better sentence** — copy is the cheapest premium signal there is.
