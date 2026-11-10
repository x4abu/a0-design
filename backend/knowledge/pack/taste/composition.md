# Composition — the part rules can't teach

Most "AI look" problems are compositional, not chromatic. These are the mechanics.

## Grid
- Mobile: 4-column grid, 20dp gutters, 16–24dp side margins. Align to it; break it once.
- Web: 12 columns, max-width 1120–1280, gutters 24. Text column max 720px (65–75 characters).
- Baseline: all vertical spacing in multiples of 4; line-height lands text on a 4dp rhythm.

## Optical alignment (mechanical alignment is not enough)
- Icons next to text: nudge 1dp toward the text's optical centre.
- Circular shapes need ~2% overshoot to look the same size as squares.
- A left-aligned block with a rounded container needs ~2dp extra left padding.
- Quotation marks, bullets and parentheses hang outside the text edge (hanging punctuation).
- Cap-height alignment beats bounding-box alignment for buttons and chips.

## Whitespace ratios (the cheapest premium signal)
- Screen rest state: **≥ 40% empty** on mobile, 60–70% on a marketing page.
- Above the primary CTA: ≥ 48dp. Between sections: 32–48dp mobile, 120–200px web.
- Inside a card: 16–24dp, and it must exceed the gap between cards (inside > outside is
  what makes a card feel solid).
- One "big air" moment per screen — a single gap at 2× the rhythm.

## Density
- Product UI: 8–12 elements per viewport. Marketing: 3–5.
- If a screen has more than 12 discrete elements, it needs a second screen or a filter.

## Image and media direction
- One treatment for every image: warm white balance, desaturated 8–12%, lifted blacks,
  consistent crop ratio (4:5 or 3:2), never mixed.
- Product shots: real UI, straight-on, on the palette's canvas — not floating 3D blobs.
- Illustrations: one style, one stroke weight, one palette subset.
- If you have no image, use type and space. Never a stock gradient as a placeholder.

## Rhythm and tension
- Alternate density: dense block → airy block → dense block.
- One element may break the grid per page (an image bleeding off the edge, a headline
  overlapping). Once.
- Repeat a motif at three scales (icon → card → hero) to make the page feel composed.

## The composition checklist
1. Does every element align to something? (grid line, text edge, optical centre)
2. Is there one clear focal point per viewport?
3. Is the inside-padding > outside-gap in every card?
4. Is at least 40% of the screen empty at rest?
5. Is there exactly one grid break?
6. Do the images share one treatment?
