# Open design: tokens as truth

Source: penpot/penpot (MPL-2.0) and onlook-dev/onlook (Apache-2.0). Referenced for
the token model and the DOM↔code editing loop.

Penpot's lesson: the design file is not the source of truth — the tokens are. A design
is a set of named decisions (colour roles, type scale, spacing, radius, motion) that
compile to every target: Compose, CSS variables, Tailwind theme, Android XML.

Onlook's lesson: visual editing works when the DOM and the code are the same tree.
a0's Style tab is the same idea at a smaller scale — accent, radius, typeface, mode
and fidelity are live variables that the preview reads.

Practical rules:
- Generated projects ship a DESIGN.md (tokens + decisions) and a PRODUCT.md.
- Never hardcode a hex outside the token file.
- One token file, many outputs: this is what keeps Android and web in sync.
