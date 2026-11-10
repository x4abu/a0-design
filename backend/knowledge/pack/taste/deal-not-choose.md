# Deal, don't choose

The failure mode after a rulebook is *sameness*: the model picks the same favourite palette,
the same blur-text entrance, the same expanding nav for every brief. Taste includes
variation; a house style applied to every product is a template.

## The mechanism
Before designing, deal a hand from a vetted deck: **palette × type pairing × layout
archetype × motion motif**. Every combination in the deck is designed (no bad hands), so the
deal is safe, but the output varies between projects and cannot drift into a single look.

Run it:
```bash
python scripts/deal.py --project "moss coffee" --platform android
# → palette: warm-minimal · type: serif-display/sans-ui · layout: editorial-split
#   motion: blur-resolve + expanding-nav · one bold move: oversized headline
```
Deterministic: the same project name always deals the same hand, so a project stays
coherent across sessions. Change the project name to re-deal.

## Then justify it
The model must write **one line** explaining why this hand suits this brief:
> "Warm Minimal + serif display, because the product is craft coffee and the audience reads
> packaging; editorial-split layout because the story is the bean, not the feature list."

If the justification is generic ("clean and modern"), the hand is wrong — re-deal once, then
commit. **Never mix two hands.**

## The deck (see `json/deal.json`)
- **Palettes (7):** pastel-soft, warm-minimal, quiet-luxury, soft-brutalist, warm-dark,
  sage-bone, clay-paper.
- **Type pairings (6):** serif-display/sans-ui, grotesk-display/sans-ui,
  mono-display/sans-ui, sans-display/serif-ui (rare, editorial), heavy-display/sans-ui,
  all-sans-tight (only for dense product UI).
- **Layout archetypes (7):** big-type hero, editorial split, bento features, stat-led,
  phone-in-hero, poster footer, quiet list.
- **Motion motifs (6):** blur-resolve, split-entrance, expanding-nav, progressive-blur,
  card-stack, count-up.

## Rules
1. One hand per project. Locked before the first screen.
2. One **bold move** per project (oversized type / one big image / one unusual layout /
   one colour block) — named in the justification, used once, never repeated on every
   screen.
3. Two motion motifs maximum per project, applied consistently.
4. A second project by the same user should deal a *different* hand — variety is part of
   the product.
5. If the brief pins the aesthetic ("make it look like a Swiss poster"), the brief wins;
   skip the deal and follow the brief exactly.
