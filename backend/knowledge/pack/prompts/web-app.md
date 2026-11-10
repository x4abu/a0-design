# Prompt pack — web app (dashboard / tool)

```
Build the <SCREEN> of <PRODUCT>, a web app in React + Tailwind + TypeScript.

Layout: 240px sidebar (never a sidebar and a top nav) · content max-width 1120px ·
12-column grid · section rhythm 32px inside, 48px between groups.
Tokens: bg #F8FBF4 · surface #FFFFFF · ink #14171A · muted #79837C · hairline #E4EADF ·
accent #7FCF2E · radius 16 cards / 10 inputs / pill buttons.
Type: Inter Tight 400/500/600 for UI, one serif for page titles only, Geist Mono for data.

States — define all five: loading (skeleton matching final layout), content, empty,
error, partial (some data failed).
Behaviour: keyboard focus visible, focus trap in dialogs, Escape closes, aria-selected on
tabs, 44px minimum hit area.
Motion: 200–300ms transitions · table rows 40ms stagger on first paint only · sidebar
collapse 250ms settle · no animation on data refresh.

Banned: purple/indigo/blue accents, gradients on data surfaces, three identical cards,
chart colours outside the accent family + one neutral.
```

## Chart palette (data-viz rule)
One accent for the primary series, then neutral steps of the same hue at 70/45/25% ink.
Never more than one hue plus neutrals; never the rainbow default of a chart library.
