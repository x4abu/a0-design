# Prompt pack — Android app (Jetpack Compose)

Use with the a0 project instructions. Copy, fill the brackets, send.

## Screen build
```
Build the <SCREEN NAME> screen for <PRODUCT>, in Kotlin + Jetpack Compose (Material 3).

Structure: <one sentence: what the screen is for and the single decision it makes>.
Blocks in order: <e.g. sub-line · headline · stat · 2 cards · primary CTA>.

Use only these tokens (no other colour, size, radius or duration):
  bg #F8FBF4→#E7F0DE · surface #FFFFFF · ink #14171A · muted #79837C · hairline #E4EADF
  accent #7FCF2E · accent-ink #14210A · radius card 28 / field 20 / pill
  spacing 4·8·12·16·24·32·48 · type display 34/37 · body 15/23 · caption 12
  motion: feedback 120ms, transitions 250ms, sheets 600ms settle spring

Motion (exactly these, nothing else):
  - screen enter: fade + 14dp rise + blur 8→0 over 420ms
  - primary CTA: press scale .96 on a snappy spring
  - list rows: 50ms stagger, 300ms settle

Constraints: one primary CTA · 48dp targets · loading/empty/error states defined ·
no purple/blue/neon · no emoji icons · real copy only.

Output: one file, one @Composable, a Tokens object, no inline hex outside it, plus the
AVOID list and self-check.
```

## Interaction build
```
Add <INTERACTION> to <SCREEN>. Spec: <duration> <easing/spring>, <transform>, <what
triggers it>. Keep everything else static. Respect reduced motion: at animation scale 0
render the final state instantly.
```

## Audit pass
```
Audit this screen against the a0 contract and fix, in place:
1 colour accent · 1 primary CTA · 2 radii · spacing on the 4pt scale · body ≥4.5:1 ·
targets ≥48dp · all states · no placeholder copy · no banned hue.
List each failure with the exact value before and after.
```
