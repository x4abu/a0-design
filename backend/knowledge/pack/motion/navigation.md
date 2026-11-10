# Navigation motion

## Bottom bar (the a0 signature)
- Container: floating glass pill, 14dp side margins, 16dp bottom + safe area, height 68dp.
- Destinations: 3–5, icon 22dp + 11dp label.
- **Active expansion**: the active item grows from 54dp to fill the remaining width on a
  450ms settle spring; its label fades in over 200ms after an 80ms delay; the others stay
  icon-only. This single move is what makes the bar feel custom.
- Switching: the previous item collapses on the same spring (no separate animation).
- Never: a bouncing indicator, a moving highlight behind icons, or a label that jumps.

## Top bar
- Large title (34sp) that collapses into a 17sp inline title as content scrolls under it.
  Crossfade 200ms at the 40–60% scroll threshold; the bar gains a blurred scrim
  (`progressive blur`, 8dp) rather than a hard background.
- Back chevron: 44dp glass circle; on tap the screen slides right 14dp + fades in 220ms.

## Drawer
- Width 304dp, radius on the trailing edge only (34dp), slides in 600ms settle, scrim
  fades to 40% ink with a 3dp blur.
- Items stagger 40ms, translateX −12dp → 0.
- Content behind the drawer scales to .98 and dims — depth without a shadow.

## Tabs
- Underline variant: 2dp bar that slides between labels on a 300ms spring; label colour
  crossfade; no scale on the label.
- Segmented variant: see `toggle-and-control.md`.
- Page content: horizontal slide 24dp + fade 220ms in the direction of travel.

## Breadcrumb / back stack
- Never animate a full-screen slide for a modal-style screen; present it as a sheet.
- Shared element: the tapped card's image and title morph into the detail header
  (`SharedTransitionLayout`, 380ms settle). If the morph is not possible, crossfade the
  header and slide the body.

## Accessibility
- Every destination keeps a text label (icon-only is a last resort), 48dp targets,
  focus order follows visual order, and the active state is announced
  (`selected = true` semantics).
