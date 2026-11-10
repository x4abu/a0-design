# Vibrant.design — curated sections, shaders and UI shots

**What it is.** "Design inspiration from the best of the internet": curated websites,
*sections*, launch videos and UI shots, with moodboards. The section-level curation is the
useful part — it shows how a single hero, menu or toggle is built, not just whole pages.

## Named references worth studying (from their current board)
Pixel Play interactive assets · electronic-music graphic posters · Genie-style frosted
menu · Bloom sun shader website · AI spreadsheet prompt input · MindCloud hero · **real
glass light toggle** · segmented chart selection · agentic Vercel hero · illustrated
footer · fintech currency-convert dashboard · clamped slider control · mechanical counter
password setup · interactive 3D postcard rack.

## Transferable recipes
1. **Real glass light toggle** — a toggle whose track is a translucent surface with a
   specular highlight that shifts as the knob passes. Compose: draw a `Brush.linearGradient`
   across the track with the highlight centred on the knob's animated x.
2. **Genie-style frosted menu** — a menu that appears with a blur + scale from the anchor,
   not a slide. Compose: `AnimatedVisibility` + `scaleIn(0.92f)` + `Modifier.blur`.
3. **Segmented chart selection** — segmented control whose thumb is a mini chart preview.
4. **Clamped slider** — the value bubble clamps to the edges instead of overflowing.
5. **Mechanical counter** — digits roll on a vertical axis, one column per digit.
6. **AI prompt input** — a composer that grows, shows attachments as chips, and morphs the
   send button from mic to arrow when text exists.

**Licence.** Reference only. Link: https://vibrant.design
