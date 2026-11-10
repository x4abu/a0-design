# Prompt pack — motion only

Use these when the layout is fine and only the feel is wrong. One move per prompt.

```
Motion pass, single change: <COMPONENT>. Apply exactly:
- duration <ms>, easing <settle|standard|spring>
- transform: <translate / scale / blur / colour> from <a> to <b>
- trigger: <tap | mount | scroll 20% | state change>
- nothing else on the screen may animate
- reduced motion: instant final state
```

Library of one-liners that work:
- "Give the bottom nav the expanding-pill treatment: active item grows to fill the row on a
  450ms settle spring, label fades in after 80ms, others stay icon-only."
- "Make the sheet arrive with the 600ms settle curve, scrim to 40% ink with a 3dp blur,
  content staggering in 40ms after it lands."
- "Headline: split by word, 60ms stagger, 420ms enter, 14dp rise. No blur."
- "Toggle: knob travels 450ms on the settle spring, track colour crossfades 300ms,
  2–4% overshoot maximum."
- "Numbers count up over 900ms on the enter curve, tabular figures, no bounce."
- "Add a progressive blur to the top 64dp of the list: 0→10dp blur with an 18% ink scrim."
