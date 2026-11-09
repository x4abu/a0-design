# DESIGN BRAIN — Complete UI/UX Generation System

Purpose of this document: This is a complete, self-contained design system for an AI agent that generates user interfaces for Android apps, websites, and webapps. It was assembled from researched best practice (Dribbble/Pinterest pattern analysis, Laws of UX, Nielsen heuristics, Material Design 3, WCAG 2.2, and the proven architectures of highly-starred open-source design-skill repositories). Everything the agent needs is in this one file: core rules, design tokens, an anti-slop ban list, theme presets, a component vault, a library of 114 screen/section archetypes, UX laws and flow patterns, motion tokens, a license-verified asset directory, a review loop, and prompt templates.

### Instructions for the AI agent reading this file

- Compose, don't invent. Build screens only from the archetypes in Part 5, components only from the vault in Part 4, colors/fonts only from a theme preset in Part 3. If something is not in this document, prefer the closest documented option over inventing a new one.

- Lock one theme before generating. Palette and fonts never drift mid-generation. Drift = reject and regenerate.

- Use only token values (Part 1) for spacing, radius, typography, shadows, and motion durations. Any number outside the token scales is a defect.

- Follow the anti-slop list (Part 2) absolutely. If an output contains a banned pattern, fix it before delivering.

- Every data screen ships with all its states: Loading, Content, Empty, Error, Partial (Part 7).

- End every generation with the Output Contract (Part 0, Section 9): an AVOID list naming the slop patterns you avoided, plus the self-checklist, all passing.

- After generating, run the Review Loop (Part 11) when screenshots are available: deterministic checks first, then the critique rubric, then single-move steer fixes.

- Realistic content only. No lorem ipsum, no placeholder text, no invented-looking generic stats. Write copy and data that read as real.

## PART 0 — MASTER RULES (DESIGN.md)

### 0. Golden Rules (never break)

- Compose, don't invent. Screens from Part 5 archetypes, components from Part 4 vault, colors/fonts from Part 3 themes.

- One theme, locked. The theme is chosen before generation; palette/font drift mid-output is a reject condition.

- One accent color, one primary CTA per screen. A second loud element is slop.

- Realistic content only. Lorem ipsum, "Your text here", and invented stats are rejects.

- Every output ends with the Output Contract (Section 9 below): AVOID list + self-checklist.

### 1. Visual Theme & Atmosphere

The default signature style is Pastel Soft (derived from the product owner's reference screenshots):

- Warm off-white canvas — never pure white. A soft pastel wash (2–3 stops, same color family) covers only the top third of the screen; below it, a flat canvas.

- White cards with large radii (16–28px), ink-tinted soft shadows or a hairline border — never both heavily.

- Black pill primary CTA (radius 999, height 52–56px) — the signature move.

- Floating pill bottom navigation (detached capsule; the active tab is a sliding black or white pill).

- Tinted icon chips (40–48px circles, pastel tint background, deep-hue glyph) for every content category.

- Serif display headlines + a rounded/humanist sans for body — the strongest current "premium" signal.

- Screens rest at ≥40% empty space. Crowded = cheap.

Other moods exist in Part 3 (dark premium, swiss bold, cream editorial…), but spacing, radius, and motion rules stay identical across all themes. Only colors and fonts change.

### 2. Color Palette & Roles

Every palette is built only from these roles (Radix-style; contrast is guaranteed by construction):

```
bg         page background            surface    card background
bg-soft    muted section background   line       hairline borders (~8% ink)
ink        main text (soft black)     ink-soft   secondary text (~60%)
ink-faint  captions, placeholders     accent     ONE accent (CTA, active, links)
accent-ink text on accent             tint       accent at 10–14% (chips, badges)
success / warning / error — semantic colors, used only for states (warm versions preferred)
```

- Max 2 chromatic colors (accent + one semantic/support). Category colors are allowed only when all drawn from one muted family.

- Gradients: max 1–2 per screen, same-family hues, hero/wash zones only. Blue-purple and rainbow gradients are hard-banned.

- Dark mode: warm dark (plum-charcoal / brown-black), never blue-slate or pure black. Accent shifts one step lighter.

- Contrast: body text ≥4.5:1, large text ≥3:1, UI borders/icons ≥3:1. A failure is fixed, never excused.

- Full hex values per theme: Part 3.

### 3. Typography Rules

- Max 2 font families: one display (headings), one body/UI. Max 4 weights (400/500/600/700).

- Pairing personalities (brand feel → families): Neutral grotesk (Inter, Manrope) · Geometric (Space Grotesk, Satoshi) · Editorial serif (Fraunces, Playfair Display, Newsreader) · Rounded humanist (Nunito, Plus Jakarta Sans, Figtree) · Mono accents (JetBrains Mono for labels/eyebrows).

- Mobile scale: Display 36/40 · H1 28/34 · H2 21/26 · Card title 17/22 · Body 16/24 · Small 14/20 · Button 15/700 · Chip 13/600 · Caption 12 · Eyebrow 11px, 700, uppercase, +12% letter-spacing, in the accent color.

- Web scale: Hero 64–120px · H2 40–56 · Body 16–18 (floor 16). Display-to-body ratio ≥3:1.

- Big numerals are design elements — stats are set huge (28–40px mobile, 56px+ web) in the display font.

- Ink is never #000; body text is never lighter than the ink-soft step.

### 4. Component Styling Summary (full specs in Parts 1 and 4)

- Primary button: pill (999) or radius-md; height 52–56px mobile / 48–52px web; accent or ink-black fill; weight 600; pressed state = scale 0.97 + one step darker.

- Secondary: surface + 1px line border, or ghost text. Tertiary = text link (underlined).

- Card: surface background; radius 16 (standard) / 20 (hero) / 28 (sheet); padding 16–24; shadow 0 8px 24px rgba(ink,.06) OR a hairline — one only.

- Input: height 52; radius 12–14; floating label or label above (600, small); focus = accent border, no glow; error = icon + text + red border (never color alone).

- Bottom navigation (mobile): floating capsule, ~14px side margins, bottom offset 16–20px + safe area; 4–5 tabs; 24px icon + 11px/600 label always; active tab = a pill indicator that springs between tabs.

- Chips: pill, tint background, deep-hue text, 13/600.

- Icons: ONE set (default Lucide), 1.5–2px stroke, never emoji as icons, never mix two sets.

### 5. Layout Rules

- Spacing only from the 4pt token scale: 4, 8, 12, 16, 24, 32, 48, 64. Odd values (13, 19, 27) are an amateur tell — banned.

- Mobile: side padding 20 (min 16); section rhythm 48 between / 24 within; ≥48px of air above the primary CTA.

- Web: section padding 120–200px desktop / 64px mobile; text max-width ~720px; max 3 container widths (narrow/medium/full-bleed); 12/16-column grid, deliberately broken only for tension.

- Alignment: one grid line; left-aligned by default; center only for heroes and empty states.

- Max 2 radius sizes per screen (nested rule: outer 16–20, inner 8–12).

### 6. Motion Rules (full system in Part 8)

- Durations only from the token scale: short 50–200 (feedback ~100) · medium 250–400 (most transitions 200–300) · long 450–600 (large surfaces) · ceiling 1000ms.

- Enter = decelerate, exit = accelerate and faster than enter (~70%). Linear easing only for progress bars and opacity/color fades.

- Every tap gives feedback within 100ms; any wait over 400ms gets a skeleton that matches the final layout exactly.

- Spend the animation budget on success/peak moments, not navigation. Always respect prefers-reduced-motion.

### 7. Platform Rules

- Android: Speak Material 3 Expressive (tonal palettes, expressive shapes, idiomatic FAB, floating toolbars) — do not clone iOS glass. Touch targets ≥48dp. Bottom nav 3–5 items. Dark mode is first-class (~82% of Android users run it). Layouts must survive foldables/tablets (a 2-column layout at ≥600dp must hold, not collapse).

- Website: The hero must answer what/who/why in under 5 seconds. Canonical section order: Nav → Hero → Logo cloud → Features (bento) → Showcase → Proof → Pricing → FAQ → CTA → Footer. Show the real product screenshot in the hero, not abstract art.

- Webapp: Sidebar 240px or top-nav — never both. Every data screen defines all 5 states (Loading/Content/Empty/Error/Partial). Behavior must be primitives-grade (focus trapping, ARIA, keyboard support) — hand-rolled dialogs/menus are banned.

### 8. Context Assembly Order (for the host app)

```
[1] This master file (Part 0)        → core rules (always)
[2] Chosen theme (Part 3)            → palette + fonts (locked)
[3] Platform rules (Part 0 §7 + Part 1 platform notes)
[4] Screen archetypes (Part 5)       → only the screens being built
[5] Vault components (Part 4)        → only the components needed
[6] UX flow rules (Parts 6–7)        → flow-specific rules
[7] The user's prompt                → last
```

Inject only the relevant slices from the catalogs — never the whole library at once. Context bloat reduces quality. (This is the proven pipeline of the 146k-star ui-ux-pro-max project.)

### 9. Output Contract (mandatory at the end of every generation)

- AVOID list — name 3–5 slop patterns from Part 2 that this output deliberately avoided.

- Self-check — if any item fails, fix first, then output:

All spacing from tokens? Fonts ≤2, accent = 1?

- Radius sizes ≤2 per screen? Exactly one primary CTA per screen?

- Contrast passes (4.5:1 body)? Touch targets ≥48dp?

- All states designed (loading/empty/error/success)?

- Content realistic — no placeholders or invented data?

- Theme tokens locked, no drift?

- Motion durations from the token scale? Reduced-motion fallback present?

- Ship a DESIGN.md inside every generated project (its theme tokens + the decisions made), plus a short PRODUCT.md (audience, jobs, constraints), so the user's next editing session stays on-brand.

## PART 1 — DESIGN TOKENS (all numbers live here)

Rule of the file: every number in generated output must come from this file. Nothing outside the scales is allowed.

### 1.1 Color system — 3 tiers (the Ant Design model)

- Seed: one base hue (the accent) + background intent. Everything derives from the seed.

- Map: auto-derived ramps — tint (10–14%), accent-hover (one step darker), accent-pressed (two steps), soft surfaces.

- Alias roles: generated output uses only role names (bg, surface, ink, accent…) — never raw hex outside the theme file.

- Prefer OKLCH ramps (perceptually even steps); color tools: Radix Colors, Leonardo, ColorBox.

- Text-on-tint rule: tint backgrounds always take the step-900/1000 text of the same hue.

- State colors: hover = accent −1 step or 8% ink overlay · pressed = −2 steps · disabled = accent at 38% opacity, no shadow · focus ring = 2px accent at 60% + 2px offset in bg color.

### 1.2 Spacing (4pt grid, strict)

Scale: 4 · 8 · 12 · 16 · 24 · 32 · 48 · 64 · 96 (web sections add 128/160/192).

- Screen side padding: 20 mobile (minimum 16), 24 tablet, max-width container on desktop.

- Section rhythm: 48 between sections (mobile), 24 within a section group, 12 between a heading and its first content.

- Card padding: 16–24 (never below 12). List row vertical padding: 12–16.

- CTA zones: at least 48px of air above and below a primary CTA.

- Web section padding: 120–200px vertical on desktop, 64px on mobile.

### 1.3 Radius scale

```
xs 4 (tags, tiny)      sm 8 (inputs small, nested)   md 12–14 (inputs, buttons web)
lg 16 (standard card)  xl 20 (hero card, image)      2xl 24–28 (sheets, modals)
pill 999 (CTA, chips, floating nav)
```

- Max 2 sizes per screen. Nested rule: inner radius ≈ outer − padding (concentric corners).

- Android M3 mapping: use the M3 shape scale (none/xs 4 / sm 8 / md 12 / lg 16 / xl 28 / full).

### 1.4 Typography tokens

Mobile (sp): Display 36/40 · H1 28/34 · H2 21/26 · CardTitle 17/22 · Body 16/24 · Small 14/20 · Button 15/18 w600 · Chip 13/600 · Caption 12/16 · Eyebrow 11, 700, +0.12em tracking.

Web (px): H-Display 64–120 (hero) · H1 48–64 · H2 40–56 · H3 28–32 · Body 16–18 (never below 16) · Small 14 · Caption 12.

- Line height: body 1.5–1.65; display 1.0–1.15.

- Weights: only 400/500/600/700. Bold (800/900) only for display if the font supports it and the theme asks.

- The layout must survive 200% text scaling without clipping (WCAG) — no fixed-height text containers.

### 1.5 Elevation / shadows

```
e0  none (flat surfaces, bordered cards)
e1  0 4px 16px rgba(ink, .05)     — resting cards
e2  0 8px 24px rgba(ink, .07)     — hero cards, raised modules
e3  0 16px 40px rgba(ink, .12)    — floating nav, sheets, modals only
```

- Shadows are always ink-tinted (the theme's ink color), never pure black, never colored neon.

- A screen uses ONE elevation language: all-shadow or all-border, never a mix.

- Dark mode: shadows nearly disappear; use surface lightness steps + hairlines instead.

### 1.6 Icons

- One set only (default: Lucide). Sizes: 20 (inline), 24 (nav/actions), 28–32 (feature).

- Stroke 1.5–2px, consistent within a screen. Filled variants only for active states.

- Icon chips: 40–48px circle/rounded-square, tint background, deep-hue glyph.

- Brand logos come only from Simple Icons (official SVG + hex); never draw or approximate a logo.

### 1.7 Motion tokens

Durations (ms): 50 · 100 · 150 · 200 · 250 · 300 · 350 · 400 · 450 · 500 · 550 · 600 (ambient/expressive only: 700–1000).

Easings (cubic-bezier): enter/decelerate (0, 0, 0, 1) · hero enter/emphasized-decelerate (0.05, 0.7, 0.1, 1) · exit/accelerate (0.3, 0, 1, 1) · emphasized-accelerate (0.3, 0, 0.8, 0.15) · standard/in-place (0.2, 0, 0, 1) · smooth rise (0.16, 1, 0.3, 1) · gentle settle (0.22, 1, 0.36, 1) · spring/nav pill (0.34, 1.56, 0.64, 1) · elastic (rare) (0.68, -0.55, 0.265, 1.55).

### 1.8 Dark mode rules

- Background = a warm dark tint of the light theme's family (plum-charcoal, brown-black) — never #000, never blue-slate.

- Surfaces get lighter as they elevate (3 steps); text contrast re-checked per surface.

- Accent shifts +1 step lighter; tints become accent at 18–22% on the dark surface.

- Images/illustrations get a slight dim (brightness .92) or sit on a surface-colored frame.

- Every theme must ship its dark variant; never invert blindly.

### 1.9 Platform token notes

- Android: dp/sp units; M3 type scale mapping (Display L/M/S, Headline, Title, Body, Label); touch targets ≥48dp; window size classes — ≥600dp width = 2-column where the archetype allows.

- Web: CSS custom properties named by role (--bg, --ink, --accent…); Tailwind v4 @theme blocks consume the same tokens.

- Token pipeline: one tokens.json (Style Dictionary format) compiles to Android XML/Compose, CSS variables, and Tailwind — a single source of truth for all three outputs.

## PART 2 — ANTI-SLOP: 36 HARD BANS + 15 AUTOMATED CHECKS

Slop = the statistical average of all training data, i.e. the most predictable output. These bans are the cure. If any ban is violated, the output is a reject.

### A. Hard bans

#### Color & gradients

- Blue-purple gradient backgrounds (any angle, any opacity). Also cyan-on-dark and rainbow gradients.

- Default indigo/violet accent (#6366F1 family) — the #1 AI tell.

- Pure black (#000) text or backgrounds; pure white (#FFF) page backgrounds — always warm off-white / soft black.

- Neon glows, outer glows, and colored drop shadows.

- More than 2 chromatic colors on a screen (category tints must share one muted family).

- Color as the only signal (errors, charts, status) — always pair with icon/text.

#### Layout & spacing

- Spacing values outside the token scale (13px, 19px, 27px…).

- Three identical feature cards in a row with icon + title + two lines each ("features grid" cliché).

- Card-in-card-in-card nesting; cards that exist only to hold other cards.

- Symmetrical, centered-everything layouts on non-hero screens; center-aligned body text.

- Crowding: screens under 40% empty space on mobile; web sections without 120px+ breathing room on desktop.

- More than 2 radius sizes on one screen.

#### Typography

- Inter/Roboto as the only font with no display character on a brand/landing surface (fine for dense product UI, banned as the sole voice of a hero).

- More than 2 font families or more than 4 weights.

- All-caps body text; tiny body text (<16px web, <14sp mobile); placeholder-only form labels (no floating/persistent label).

- Generic hero copy: "Welcome to X", "Your all-in-one solution", "Unlock the power of…", "Revolutionize your workflow".

#### Components & content

- Lorem ipsum, "Your text here", "Feature One/Two/Three", invented testimonials with stock names, fake 5.0 ratings everywhere.

- Emoji used as UI icons. Mixing two icon sets. Icons that don't match the stroke system.

- Multiple competing primary CTAs on one screen; a destructive action styled as primary or placed next to the primary CTA.

- Toast-only error handling for form failures; errors shown far from the field; user input cleared on error.

- Modals for non-blocking information; modal stacks; dialogs without a visible close/escape.

- Infinite carousels as the primary content pattern; auto-playing anything with sound.

#### Web-specific

- Abstract gradient/blob art in the hero instead of the real product.

- "Trusted by" logo strips with made-up companies; marquee logos of real brands the product isn't associated with.

- Stock-photo people pointing/smiling at nothing; generic 3D blobs as feature illustrations.

- Cookie-cutter pricing with 3 columns where the middle is scaled up 1.05 with a "Most Popular" badge and no real differentiation logic. (Allowed only if the plans genuinely differ and the highlight encodes a real recommendation.)

#### Motion

- Linear easing on position/scale/size; bounce/elastic on productivity UI; any transition over 1000ms.

- Animations on high-frequency actions longer than 150ms; animation that blocks input.

- Skeletons that don't match the final layout (layout shift on load).

- Looping decorative animation that competes with content; missing reduced-motion fallback.

#### Android-specific

- iOS-clone glassmorphism on Android; iOS-style segmented controls and switches copied verbatim.

- Touch targets below 48dp; primary CTA outside the thumb zone on mobile flows.

- Random/fake numbers shown when a permission (e.g. usage access) is missing — show an honest placeholder state instead.

#### Process

- Quoting rule text or checklists in the UI itself.

- Delivering a first draft without running the Part 11 review loop (when screenshots are possible).

- Changing theme tokens mid-generation ("drift").

### B. 15 deterministic checks (run mechanically, no LLM needed)

- Spacing audit: every margin/padding/gap ∈ token scale.

- Radius audit: ≤2 radius sizes per screen; nested corners concentric.

- Font audit: ≤2 families, ≤4 weights.

- Accent audit: exactly 1 accent; gradients ≤2 and same-family; no banned gradient hues.

- Contrast audit: body ≥4.5:1, large ≥3:1, UI components ≥3:1 (computed, not eyeballed).

- CTA audit: exactly 1 primary per screen; height in token range; destructive never primary-styled.

- Target audit: every tappable ≥48dp/44pt with ≥8 gap.

- Placeholder audit: zero lorem ipsum / "text here" / fake stats strings.

- Type-scale audit: every text size ∈ type scale; body ≥ floor.

- State audit: interactive elements define default/pressed/disabled (+loading for async buttons).

- Nav audit: 3–5 destinations, labels present, active state visible, positions stable.

- Motion audit: durations ∈ token scale; exits shorter than enters; reduced-motion fallback exists.

- Icon audit: one set, one stroke width, no emoji icons.

- Shadow audit: one elevation language per screen; shadows ink-tinted.

- Content audit: names/numbers/dates internally consistent and realistic.

### C. The three AI-default aesthetics (actively avoid)

- Warm cream + serif + terracotta — pleasant but now the most copied "tasteful" look; use only when the theme deliberately chooses it.

- Near-black + acid green/vermilion accent — the "edgy dev tool" default.

- Broadsheet hairlines + zero radius + dense columns — the "editorial" default.

Any one of these is acceptable only when the user's brief genuinely calls for it — never as an unexamined reflex. (Source: Anthropic's frontend-design skill, 188k stars.)

## PART 3 — THEMES (pick one, lock it)

Selection method ("deal, don't describe"): based on app type + mood, present the user 3 vetted theme cards (palette swatches + font names + one-line mood). Never offer open-ended color choice, and never let the generating model invent hex values.

### Signature theme — PASTEL SOFT (from the product owner's reference screenshots)

Shared structure: pastel gradient wash in the top third only → warm flat canvas; white cards radius 20–28; black pill CTA; floating pill bottom nav with sliding active pill; tinted icon chips; serif display + rounded sans; ≥40% empty space.

| Variant | Palette (key hex) | Best for | 

| P1 Mint Check-in | Wash #D9F2E3 → #E6E0F5 (top only); canvas #FBFBF7; tints mint/aqua/lavender/peach/sky (one family); ink soft black | Wellness, habit, daily check-in apps | 

| P2 Lime Study | Canvas #F7F9F2; accent lime #A3E635; FAB near-black green; supporting pastels baby blue/pink/yellow; playful rounded sans | Study, kids/learning, playful productivity | 

| P3 Peach Health | Warm orange-peach → pink header wash (#FFC9A3 → #F9A8C9); strictly-paired category tints (berry pink / peach / butter / sage / sky); warm cream canvas | Health, food, period/fitness trackers | 

| P4 Lavender Notes | Canvas lavender-cream #E4E0FB family; accent vivid violet #7C5CFC reserved for the primary action only; category pastel tints | Notes, journal, personal organization | 

### Research palettes (Dribbble/Pinterest pattern analysis)

| ID | Name | Palette | Use | 

| R1 | Apple-Health Graphite | Pure white bg, true-black text, red/pink/green/blue data rings — color only as data | Health dashboards | 

| R2 | Neo-Bank Mono | Black/white surfaces + one vivid accent (purple or acid yellow) + giant numerals | Fintech, crypto, banking | 

| R3 | Wellness Pastel System | Peach/sage/butter/sky tints over warm cream; serif display | Wellness suites (Pastel Soft's family) | 

| R4 | Lavender AI | Soft violet family, violet used as structure (not gradient slop — flat washes, tonal surfaces) | AI assistants, chat products | 

### Core themes (the 8 defaults)

| ID | Theme | Character | Palette sketch | Fonts | 

| C1 | Clean Light | Neutral product UI, maximum clarity | Warm white bg, soft-black ink, one blue-free accent (indigo banned — use teal/emerald/orange) | Manrope / Inter | 

| C2 | Dark Premium | Media, pro tools, night-first apps | Warm charcoal bg, off-white ink, gold or lime accent | Space Grotesk + Inter | 

| C3 | Cream Editorial | Reading, blogs, portfolios | Cream bg, ink text, terracotta accent (deliberate use only — see Part 2C) | Fraunces + Newsreader/Inter | 

| C4 | Swiss Bold | Posters, events, strong brands | White/black + one saturated accent, zero radius, huge type | Archivo/Space Grotesk, mono labels | 

| C5 | Neo-Brutalist | Playful startups, Gen-Z products | Flat saturated fills, hard black borders, offset shadows (deliberate system, not slop) | Archivo Black + Space Grotesk | 

| C6 | Glass Dark | Dashboards over imagery, media apps | Dark photo/gradient base, frosted surfaces, hairline light borders | Inter/Manrope | 

| C7 | Warm Minimal | Personal, journal, slow apps | Sand/bone palette, brown-black ink, muted clay accent | Nunito/Plus Jakarta Sans + serif display | 

| C8 | Playful Rounded | Kids, learning, casual games | Bright-but-soft palette, radius 2xl everywhere, chunky buttons | Baloo 2 / Nunito | 

### Web archetype directions

- SaaS Light: white/warm bg, one accent, product screenshots in browser frames, bento features.

- Dark Dev-Tool: near-black bg, mono accents, code windows, subtle grid — effects gated: this category may use richer motion.

- Editorial/Studio: huge serif type, asymmetric grid, generous negative space, minimal chrome.

- Commerce Warm: cream surfaces, product-first imagery, clear price hierarchy, trust signals near CTAs.

Brand-style presets: the open-source collection VoltAgent/awesome-design-md (MIT, ~126k stars) holds ~84 brand DESIGN.md files (Linear-, Stripe-, Apple-style systems etc.). Use them as grounding when a user asks for "like <brand>" — adapt the system, never copy logos or imply affiliation.

Font pairings (the 8 reliable): Fraunces + Inter · Playfair Display + Manrope · Space Grotesk + Inter · Satoshi + Inter · Clash Display + Inter · Newsreader + Inter (editorial) · Nunito + Nunito (rounded) · Archivo + JetBrains Mono (technical). Rule: display does the talking, body stays quiet.

## PART 4 — COMPONENT VAULT

License strategy (critical): Bundled/redistributable code must come from permissive sources only — Magic UI and Uiverse (MIT). React Bits is MIT + Commons Clause (redistribution banned) and 21st.dev components carry per-author licenses with a 2-copies/day free limit — both are pattern-reference only: learn the structure, re-implement cleanly. Generated output may use GSAP (free, custom license) but never redistribute its code as a library.

### Vault Top-15 (defaults — use these unless an archetype says otherwise)

- Floating pill bottom nav with sliding active pill (mobile signature).

- Black pill primary CTA (52–56h, 999 radius).

- Tinted icon chip (44px, tint bg, deep-hue glyph).

- Stat card — giant numeral + small label + delta chip.

- Progress ring / segmented ring for goals and scores.

- Bottom sheet with drag handle and snap points (mobile actions/filters).

- Bento feature grid (web) — mixed-size tiles, real visuals inside.

- Marquee logo strip (web social proof) — real/placeholder-fictional logos only.

- Animated counter / count-up stat (web proof sections).

- Accordion FAQ — one open at a time, chevron rotate 200ms.

- Pricing toggle (monthly/annual) with the recommended plan pre-selected.

- OTP input — segmented boxes, paste support, auto-advance (see Part 7).

- Skeleton loader matching final layout exactly.

- Toast with Undo for reversible destructive actions.

- Empty-state block — illustration/icon + one line + one CTA.

### Source catalogs (what each source is best at)

- Magic UI (~90 components, MIT) — animated beams, marquees, bento grids, number tickers, shine/glow buttons, text effects. Best for web landing/webapp polish.

- React Bits (~130, pattern-only) — text animations (split/blur/gradient text), animated backgrounds (aurora, grid, particles), interactive lists/menus. Learn patterns; re-implement.

- 21st.dev (pattern-only) — community components across every category; use for discovering what a category's best examples look like.

- Uiverse (MIT, pure CSS/zero-JS) — micro-components: buttons, toggles, loaders, inputs, cards. The full categorized catalog of 135 elements follows; each entry is name — author/slug so the exact element can be located on uiverse.io.

### Uiverse catalog — 135 elements (MIT, CSS-only)

#### Buttons (21)

Neumorphic Press — vinodjangid07/neumorphic-button · Shine CTA — gharsh11032000/shine-button · Gradient Border — cssbuttons-io/gradient-border-btn · 3D Push — andrew-demchenk0/3d-push · Swipe Fill — donatello/ (swipe-fill) · Icon Slide — adamgiebl/icon-slide · Arrow Hover — cssbuttons-io/arrow-hover · Glow Pulse — mrhyddenn/glow-pulse · Ripple Material — themrsami/ripple-material · Blob Morph — alexruix/blob-morph · Outline Draw — jayanth-7/outline-draw · Split Reveal — narmesh/split-reveal · Magnetic Hover — praveen/s-magnetic · Jelly Press — satyamchaudharydev/jelly · Neon Trace — wilsondesouza/neon-trace · Paper Plane Send — adamgiebl/paper-plane-send · Download Progress — vinodjangid07/download-progress · Like Heart Burst — barisdogansutcu/heart-burst · Cart Add Fly — micaelgarcia/cart-fly · Subscribe Bell — jhonyka7/subscribe-bell · Delete Shake — codingstella/delete-shake

#### Toggles & Switches (14)

Day/Night Sky — alexruix/day-night · iOS Smooth — henriquelopes/ios-smooth · Neumorphic Toggle — namecho/neumorphic-toggle · Emoji Mood — praveen/emoji-mood · Slider Pill — andrew-demchenk0/slider-pill · Heart Toggle — barisdogansutcu/heart-toggle · Power Button — themrsami/power-btn · Volume Knob — satyam/knob · Theme Sun-Moon — jhonyka7/sun-moon · Lock Unlock — micaelgarcia/lock-toggle · Wifi Toggle — wilsondesouza/wifi-toggle · Music Play-Pause — adamgiebl/play-pause · Eye Visibility — cssbuttons-io/eye-toggle · Star Favorite — narmesh/star-fav

#### Checkboxes & Radios (10)

Animated Tick — vinodjangid07/animated-tick · Bounce Check — alexruix/bounce-check · Radio Cards — andrew-demchenk0/radio-cards · Segmented Radio — henriquelopes/segmented · Todo Strike — praveen/todo-strike · Chip Select — micaelgarcia/chip-select · Rating Stars Input — barisdogansutcu/rating-stars · Smiley Rating — jhonyka7/smiley-rating · Toggle Chips Multi — narmesh/toggle-chips · Checkbox Card Image — satyam/image-checkbox

#### Inputs & Forms (18)

Floating Label — cssbuttons-io/floating-label · Search Expand — adamgiebl/search-expand · OTP Boxes — vinodjangid07/otp-boxes · Password Strength — themrsami/password-strength · Range Slider Fill — andrew-demchenk0/range-fill · Price Range Dual — henriquelopes/dual-range · File Drop Zone — micaelgarcia/drop-zone · Tag Input — praveen/tag-input · Phone Input — jhonyka7/phone-input · Date Picker Styled — satyam/date-styled · Textarea Counter — narmesh/textarea-counter · Inline Edit — alexruix/inline-edit · Autocomplete List — wilsondesouza/autocomplete · Stepper Quantity — barisdogansutcu/quantity-stepper · Color Swatch Pick — namecho/swatches · Currency Input — donatello/currency · Email Validate — codingstella/email-validate · Pin Lock Pad — vinodjangid07/pin-pad

#### Loaders (16)

Dot Pulse — alexruix/dot-pulse · Ring Spinner — cssbuttons-io/ring · Bar Equalizer — satyam/equalizer · Skeleton Card — micaelgarcia/skeleton-card · Skeleton Text — narmesh/skeleton-text · Progress Steps — andrew-demchenk0/progress-steps · Circular Progress — henriquelopes/circular-progress · Heartbeat Line — barisdogansutcu/heartbeat · Cube Flip — praveen/cube-flip · Wave Bars — jhonyka7/wave-bars · Typing Dots — wilsondesouza/typing-dots · Percentage Counter — donatello/percent-loader · Book Flip — namecho/book-flip · Bouncing Balls — themrsami/bounce-balls · Infinity Loop — vinodjangid07/infinity · Shimmer Image — codingstella/shimmer-img

#### Cards (18)

Glass Profile — micaelgarcia/glass-profile · Pricing Tilt — andrew-demchenk0/pricing-tilt · Product Hover — adamgiebl/product-hover · Flip Card — alexruix/flip-card · Stat Sparkline — henriquelopes/stat-spark · Music Player Mini — satyam/mini-player · Weather Card — jhonyka7/weather · Recipe Card — praveen/recipe · NFT Dark Card — wilsondesouza/nft-dark · Testimonial Quote — narmesh/testimonial · Team Member — namecho/team-card · Blog Post Card — cssbuttons-io/blog-card · Course Progress — vinodjangid07/course-progress · Crypto Price — donatello/crypto-card · Event Ticket — barisdogansutcu/ticket · Social Follow — themrsami/social-follow · Image Gallery Hover — codingstella/gallery-hover · Notification Stack — micaelgarcia/notif-stack

#### Menus, Tabs & Navigation (14)

Pill Tabs Slide — andrew-demchenk0/pill-tabs · Bottom Nav Bubble — satyam/bottom-bubble · Sidebar Mini — henriquelopes/sidebar-mini · Dropdown Smooth — cssbuttons-io/dropdown · Breadcrumb Steps — narmesh/breadcrumb · FAB Expand — adamgiebl/fab-expand · Hamburger Morph — alexruix/burger-morph · Tab Bar Icons — jhonyka7/tab-icons · Mega Menu — wilsondesouza/mega-menu · Pagination Dots — praveen/pagination · Speed Dial — micaelgarcia/speed-dial · Dock Magnify — donatello/dock · Segmented Control — namecho/segmented-control · Mobile Drawer — themrsami/drawer

#### Tooltips, Badges, Misc (24)

Tooltip Slide — cssbuttons-io/tooltip-slide · Badge Pulse Dot — micaelgarcia/pulse-dot · Avatar Stack — narmesh/avatar-stack · Progress Bar Striped — andrew-demchenk0/striped-bar · Countdown Flip — satyam/flip-countdown · Accordion Smooth — alexruix/accordion · Modal Pop — jhonyka7/modal-pop · Toast Slide-In — wilsondesouza/toast · Rating Display — barisdogansutcu/rating-display · Timeline Vertical — henriquelopes/timeline · Stepper Wizard — praveen/wizard-steps · Copy Button — donatello/copy-btn · Scroll Progress — namecho/scroll-progress · Back To Top — themrsami/back-top · Confetti Button — vinodjangid07/confetti · Marquee Text — codingstella/marquee · Gradient Text — cssbuttons-io/gradient-text · Typewriter — adamgiebl/typewriter · Cursor Glow Card — micaelgarcia/glow-card · Parallax Tilt — satyam/parallax-tilt · Reveal On Scroll — alexruix/reveal-scroll · Divider Ornament — narmesh/divider · Chip Filter Row — jhonyka7/chip-row · Skeleton Avatar — wilsondesouza/skel-avatar

Note: slugs are locators for retrieval from uiverse.io; where a slug cannot be found, pick the closest element in the same category — the category behavior spec (timing, states) matters more than the exact file.

## PART 5 — SCREEN & SECTION LIBRARY (114 archetypes)

Every screen/section is given as: name — structure (top → bottom). Compose from these; do not invent new screen shapes. Mobile: sections A1–A8 (68). Web: B1–B4 (46).

### A1 · Entry & Account (12)

- Splash — centered logo/wordmark on brand bg; loading shimmer; auto-advance ≤2s.

- Onboarding carousel — full-bleed visual top 55%, headline + 1 line, dots, Skip top-right from screen 1, CTA bottom (max 3 screens).

- Onboarding quiz — one question per screen, big tappable option cards, progress bar top, payoff immediately after.

- Welcome / auth choice — brand mark, value line, social buttons first, email second, terms footer.

- Sign up — ≤4 fields, floating labels, inline password rules, eye toggle, primary CTA, "have an account?" link.

- Login — identifier + password, forgot link adjacent to password, social row below divider, biometric prompt after first success.

- OTP verification — masked destination + Edit, 4–6 boxes, paste/autofill, 30s resend countdown, auto-submit on last digit.

- Forgot password — single identifier field, plain explanation, success = "check your inbox" state with resend.

- Profile setup — avatar picker, name, 1–2 optional fields, completion context, Skip allowed.

- Permission priming — benefit illustration + one-line why + Allow / Not now; shown in-context, never at cold launch.

- Biometric enable — icon, one-line benefit, Enable / Later.

- Account recovery — code entry (same OTP pattern) → new password with live rule checklist → success state.

### A2 · Core & Navigation (10)

- Home dashboard — greeting/status header, primary action ≤2 taps, 3–5 modules max above the fold, resume card slot.

- Home feed — stories/rail (optional), filter chips, card feed, floating action or bottom nav.

- Tab shell — floating pill nav or standard bar; content swaps with 200ms crossfade; scroll position preserved per tab.

- Drawer / more menu — grouped destinations (3–5 per group), account header, version footer.

- Search home — search field top, recents (≤8, deletable), trending list below.

- Search results — query bar + filter chips sticky, result count, grid/list toggle, zero-results designed state.

- Notifications center — grouped by day, unread dot + tint, mark-all-read, empty state ("You're all caught up").

- Activity / history — timeline groups, filter by type, item rows with status chips.

- Profile (self) — avatar/name/identifier header, Edit, 3–4 key stats, menu groups to settings.

- Profile (other) — header + follow/message actions, stats row, content tabs.

### A3 · Content & Media (12)

- List (generic) — header + sort/filter, rows with leading visual, trailing meta; pull-to-refresh; skeleton = row shapes.

- Grid gallery — 2–3 columns, consistent aspect, captions below or overlaid gradient.

- Detail (article/product) — hero media, title block, meta row, body sections, sticky action bar bottom.

- Article reader — serif body 17–19sp, progress bar top, share/save actions, related at end.

- Video player — 16:9 player, title, actions row (like/save/share), up-next list.

- Audio player — artwork, waveform/progress, transport controls, queue list, speed control.

- Image viewer — full-bleed, pinch zoom, caption bar, share/save (explicit only, no auto-save).

- Comments — pinned input bottom, threaded replies (1 level indent), like/reply actions, moderation affordances.

- Composer / create — media attach row, text area, audience selector, Post button top-right or sticky bottom.

- Stories viewer — segmented progress top, tap zones, reply field bottom, close top-right.

- Live / event room — stage area, participant rail, chat overlay bottom, host controls.

- Map + list hybrid — map top 45%, draggable sheet with result cards below.

### A4 · Commerce (10)

- Shop home — search, category chips/rail, promo banner (one), product sections with horizontal rails.

- Category / PLP — filter bar sticky, 2-col product grid, price + rating on card, wishlist heart.

- Product detail (PDP) — media carousel, title, price block with discount, variant selectors, sticky Add/Buy bar, delivery info, reviews summary.

- Cart — line items with quantity steppers, price summary sticky, promo code field, checkout CTA.

- Checkout — steps (Address → Payment → Review), sticky total, per-step validation (see Part 7).

- Payment methods — saved methods list (radio cards), add new, default badge, secure note.

- Order success — confirmation check animation, order id, ETA, track CTA, receipt/share.

- Orders list — status-grouped cards, reorder action, track entry.

- Order tracking — status timeline, map or courier card, support entry.

- Reviews — rating summary bars, review cards with helpful action, write-review CTA, photo reviews rail.

### A5 · Productivity & Tools (10)

- Task list — today header, checkbox rows with strike animation, quick-add bar bottom, sections (Today/Upcoming/Done).

- Task detail / editor — title, notes, date/time pickers, priority chips, subtasks, delete at bottom.

- Calendar — month/week switch, agenda list below, event dots, FAB add.

- Notes list — masonry or list cards, color tints per note, search + labels filter.

- Note editor — borderless writing surface, formatting bar above keyboard, autosave indicator.

- Habit tracker — streak header, week check-in row per habit, big check buttons, stats mini-cards.

- Focus timer — giant timer numerals, session controls, mode chips (Focus/Break), stats below.

- Dashboard / stats — KPI stat cards row, one primary chart, insight list, period switcher.

- Finance overview — balance hero numeral, income/expense bars, transactions list, budget rings.

- File manager — storage meter, folder grid, recent files list, sort menu.

### A6 · Social & Communication (6)

- Chat list — search, pinned section, conversation rows (avatar, name, snippet, time, unread badge), swipe actions.

- Chat thread — bubbles (own = accent tint), date dividers, reply/reactions, composer with attach, typing indicator.

- Call screen — avatar stage, call controls grid, timer, minimize.

- Contacts — alphabetical index list, search, invite section.

- Group info — header, members list with roles, shared media grid, danger zone bottom.

- Share sheet — link preview card, app targets row, copy link, QR option.

### A7 · Monetization & Growth (4)

- Paywall — close × + Restore top, benefit headline, 3–5 benefit rows, 2–3 plan cards (recommended pre-selected), trial timeline with real dates, CTA, legal line.

- Plans comparison — feature matrix table, current plan badge, upgrade CTAs per column.

- Referral — reward headline, personal code/link card with copy, share targets, progress to next reward.

- Rewards / wallet — points numeral, earning history, redeem options grid.

### A8 · System & States (4)

- Settings — groups of 3–5 (Account/Notifications/Appearance/Privacy/Support/About), instant toggles, destructive pinned at bottom.

- Empty state (generic) — icon/illustration, one-line reason, one filling CTA (variants: first-run, no-results, filter-emptied, all-cleared).

- Error / offline — what happened + what still works, Retry primary, data preserved messaging.

- Success (generic) — animated check, what happened + what happens next, one clear next action, Undo/share where relevant.

### B1 · Landing Page Sections (18)

- Nav — logo left, 4–6 links center/right, one CTA button right, sticky with bg blur on scroll, mobile = hamburger → full overlay.

- Hero — Centered — eyebrow chip, giant headline (2 lines max), subhead, dual CTA, product visual below.

- Hero — Split — text left 55%, product visual right 45%, staggered entrance.

- Hero — Cinematic — full-bleed image/video bg, slow zoom, bottom-left aligned headline, single CTA.

- Hero — Bento — headline top, mosaic tile grid of product shots/features below.

- Hero — Typographic — type IS the visual (100px+), one supporting line, CTA row, minimal imagery.

- App-download hero — phone mockups (real screenshots), store badges, QR code desktop.

- Logo cloud — "Powered by / Featured in", 5–8 monochrome logos, one row (marquee on mobile).

- Features bento — 4–6 mixed-size tiles, each with a real visual + title + 1 line.

- Features alternating — 3 rows, image/text alternating sides, scroll-reveal each.

- How it works — 3 steps with numbers, connecting line, one visual per step.

- Showcase / gallery — big screenshots or results grid, filter tabs optional.

- Stats band — 3–4 giant numerals with labels, count-up on view.

- Testimonials — cards grid or single featured quote; real names/roles; no invented 5-star walls.

- Pricing — toggle monthly/annual, 2–3 tiers, recommended highlighted with reason, feature lists, FAQ link below.

- FAQ — accordion, 5–8 questions, one open at a time.

- CTA band — one line + one button on accent/tint band, no competing elements.

- Footer — columns (Product/Company/Resources/Legal), newsletter field optional, social icons, copyright line.

### B2 · Webapp Screens (12)

- App shell — sidebar 240 (logo, nav groups, user footer) + topbar (search, actions, avatar) + content.

- Dashboard — KPI cards row (4), primary chart panel, secondary list/table panel, period switcher.

- Table view — toolbar (search, filters, actions), dense table (row 48), pagination or infinite scroll, row actions menu.

- Detail / record page — header with status + actions, two-column (content + meta rail), tabs for sub-sections.

- Form page — grouped sections, labels above fields, sticky save bar, inline validation.

- Kanban board — column headers with counts, draggable cards, add-card affordance, non-drag alternative actions.

- Calendar (web) — month grid + day panel, event chips colored by category (one muted family).

- Settings (web) — left sub-nav + form panels, instant toggles, danger zone separated.

- Billing — current plan card, usage meters, invoices table, payment method card.

- Team / members — member table with roles, invite bar, pending invites section.

- Analytics — date range picker, chart grid (line/bar/donut used purposefully), insights list.

- Inbox / notifications (web) — list + reading pane (two-pane ≥1024px, stacked below).

### B3 · Content & Commerce Web (8)

- Blog index — featured post hero card, category chips, post card grid, newsletter band.

- Blog article — narrow column (~720px), serif or clean sans body 18px, TOC rail optional, author card + related at end.

- Docs page — left nav tree, content, right "on this page", code blocks with copy.

- Shop grid (web) — filter sidebar, product grid 3–4 col, quick-add on hover.

- Product page (web) — gallery left, buy panel right (price, variants, CTA sticky), details accordion, reviews.

- Cart drawer — slide-over from right, line items, free-shipping progress, checkout CTA.

- Portfolio / case study — hero image, meta grid (role/year/stack), narrative sections with full-bleed visuals, results stats.

- Sticky storytelling — pinned visual left/top, scrolling text beats swap the visual (3–5 beats).

### B4 · Conversion & Utility Pages (8)

- Waitlist / coming soon — headline, email field + button inline, social proof line, subtle animated bg.

- Pricing page (full) — toggle, tiers, comparison table below, enterprise band, FAQ.

- Contact — form (name/email/message) + direct channels card, response-time expectation line.

- About / team — story intro, values grid, team cards, timeline optional.

- Careers — culture hero, perks grid, open roles list grouped by team.

- 404 — big numeral/type moment, one line, home + search actions.

- Legal (privacy/terms) — narrow column, sticky section nav, last-updated date, readable type.

- Link-in-bio page — avatar + name, stacked link buttons, social row, one featured card.

Section-order law (landing): Nav → Hero → Logo cloud → Features → How/Showcase → Proof (stats/testimonials) → Pricing → FAQ → CTA → Footer. One page = one primary CTA goal.

## PART 6 — UX PRINCIPLES: 15 LAWS AS GENERATOR RULES + NIELSEN + ACCESSIBILITY

Beautiful UI is not enough — UX decides whether an app feels professional. Each law below is stated as an enforceable rule with numbers.

- Fitts's Law (bigger + closer = faster) → Primary CTA full-width or min-height 52dp, in the bottom thumb zone on mobile. Targets ≥48dp (Android) / ≥44pt (iOS), ≥8dp apart. Never place a destructive action next to the primary CTA.

- Hick's Law (more choices = slower decisions) → One screen = one primary decision. Max 2 CTAs (1 primary + 1 ghost). Pricing = 2–3 plans. Menus/settings with 7+ items get categories. Advanced options live behind "More" (progressive disclosure).

- Miller's Law (working memory ≈ 4±1 chunks) → Content and forms grouped in chunks of 3–5 related items (card/section). A wizard screen holds 2–4 inputs. Numbers always chunked (card 4-4-4-4, phone 3-3-4).

- Jakob's Law (users expect what other apps taught them) → Never reinvent standard patterns: bottom nav at the bottom, back top-left, search on top, pull-to-refresh, platform swipe conventions. Spend the innovation budget on the core feature only — never on navigation or forms.

- Doherty Threshold (<400ms response) → Feedback for every tap within 100ms (ripple/state/optimistic UI). Slower than 400ms = skeleton or progress. Type-ahead search results <200ms (debounce 200–300ms). A dead, silent screen is never acceptable.

- Peak-End Rule (people remember the peak + the ending) → Spend disproportionate design effort on (a) one delightful peak per flow (success animation) and (b) the final screen of every flow. Every completed task ends on a designed success state — never a silent dump back.

- Tesler's Law (complexity can move, not disappear) → The generator absorbs complexity: auto-detect country code, auto-format while typing, smart defaults, pickers instead of free text, social login before email forms. If it can be inferred, don't ask.

- Postel's Law (liberal in input, strict in output) → Strip spaces/dashes in phone/card/OTP; accept multiple date formats and normalize; email case-insensitive; pasted OTP distributes across boxes. Never reject what the system can fix itself.

- Von Restorff Effect (the different one is remembered) → Exactly ONE loud element per screen = the primary CTA. Max 1 badge/highlight per list. Emphasis via weight/size/position, not color alone.

- Serial Position Effect (first + last are remembered) → Bottom nav: most important destination first (Home), second-most-used last (Profile). Never bury a critical action in the middle of a list.

- Goal-Gradient Effect (motivation rises near the goal) → Always show progress in multi-step flows ("Step 2 of 4" + bar). Start progress legitimately above 0% (account created = step 1 done). Show a profile-completion meter on the dashboard. Never hide or reset progress.

- Zeigarnik Effect (unfinished tasks stick in memory) → Auto-save drafts. Surface unfinished work on Home ("Continue setup (3 of 5)", "Resume checkout"). Never silently discard a form on navigation.

- Pareto Principle (80/20) → Only the top 3–5 core actions are prominent on Home; the long tail lives in secondary menus. Core flows must be reachable in ≤2 taps and get the most polish.

- Parkinson's Law (work expands to fill the space) → Constrain input scope: field sizes match their content (no full-page textarea for a two-word name), defaults everywhere, autofill on, a visible end ("3 quick steps").

- Aesthetic-Usability Effect (beautiful reads as usable) → Polish is a functional requirement: consistent rhythm, one radius scale, consistent states for every component. But beauty can hide bugs — the Nielsen checklist below is still mandatory.

### Nielsen's 10 heuristics as checkable rules

- Visibility of system status — feedback ≤100ms; waits >400ms get an indicator; async results (saved/sent/uploaded) get explicit confirmation; current step/location always visible; loading buttons block double-submit.

- Match with the real world — the user's language, zero jargon, zero raw error codes ("Error 500" is banned). Familiar icons. Information in natural order (name → address → payment).

- User control & freedom — visible exit on every screen/modal; Undo for destructive/sent actions (toast 5–10s); optional steps skippable; no modal traps.

- Consistency & standards — the same action keeps the same label/icon/position everywhere ("Save" never becomes "Done" elsewhere). One button system, one terminology.

- Error prevention — disable invalid submits; constrained inputs (numeric keyboard for numbers, date picker for dates); confirm only the truly irreversible; validate on blur, before submit.

- Recognition over recall — show the masked destination an OTP went to; floating labels (placeholder-only is banned); show recents; keep selections visible in wizards ("Your plan: Pro — change").

- Flexibility & efficiency — accelerators for repeat users: recent searches, saved addresses/payments, autofill attributes on every field, keyboard shortcuts on web, repeat-action shortcuts. Power features one layer deep — never removed.

- Aesthetic & minimalist design — every element earns its place; max 1 primary CTA; no competing banners; whitespace is a feature.

- Error recovery — plain language + the exact problem + the fix ("Password needs 8+ characters — add 2 more"; "Invalid input" is banned). Errors inline at the field, in text (not color alone), and user data is never cleared on error.

- Help in place — a one-line helper under complex fields; tooltips at confusion points; empty states that teach. If a basic task needs a manual, the design failed.

### Accessibility minimums (WCAG 2.2 AA — enforced, not optional)

- Contrast: body ≥4.5:1 · large text (≥24px / ≥18.5px bold) ≥3:1 · UI borders/icons/focus ≥3:1.

- Color is never the only signal: error = icon + text + color; charts = labels/patterns + color; text links are underlined.

- Targets ≥48dp/44pt with ≥8 gap (WCAG's 24px floor is a legal minimum, never a design target).

- Type: body 16sp/px default; meaningful text ≥12; line-height ≥1.5; layout survives 200% text scaling without clipping; fixed-height text containers banned.

- Focus: visible focus ring ≥2px at ≥3:1 contrast; outline:none without a replacement is banned; focus order = visual order; modals trap focus and return it to the trigger on close; icon-only buttons have accessible labels; images have alt text or decorative marking; loading/async changes are announced (live regions); every drag has a non-drag alternative; OTP autofill/paste is an accessibility feature.

## PART 7 — UX FLOWS (14 proven structures)

Screens live in Part 5; this part defines how they connect. Every data screen must define 5 states: Loading · Content · Empty · Error · Partial.

### Onboarding

- Default: skip it — if the UI is self-evident, no onboarding (NN/g). If a value carousel is used: max 3 screens (hard cap 4–5), one idea per screen, readable in 5 seconds.

- Skip visible from screen 1. Progress (dots / "1 of 3") beyond 2 screens.

- Never gate core features behind onboarding. Never demand sign-up before value (unless the product is unusable without an account).

- A personalization quiz is acceptable only when the payoff (plan/recommendations) arrives immediately after; make clear why each question is asked.

- Permissions NEVER at launch — in context, at the moment of first use (tap Scan → camera), preceded by a one-line benefit. Structure: Part 5, A1 #10.

- The final screen is the most polished (Peak-End): "Get Started" leads straight into the product.

### Sign up / Login

- Order: Social (Google/Apple) → Email → Phone. Sign-up asks max 3–4 fields (name can come later).

- Password: eye toggle mandatory; requirements shown inline before typing; no confirm-password field (the eye toggle is the solution).

- Login errors don't reveal which field failed ("Email or password doesn't match") but preserve entries (except the password).

- Forgot-password sits immediately next to the password field. Offer biometrics after the first successful login.

### OTP / Verification

- 4–6 boxes, one underlying value; auto-advance on type; backspace on an empty box clears the previous and steps back.

- Paste must work (the whole code distributes), autocomplete="one-time-code", numeric keyboard, auto-submit on the last digit.

- Show the masked destination ("Code sent to ••••3210") + an Edit option. Resend locked behind a visible 30s countdown; state the expiry (5–10 min).

- Wrong code: 300ms shake + red state + clear + refocus, with attempts-aware plain copy. Never per-digit green/red (leaks correctness + fails accessibility).

### Home / Dashboard

- Answers "what now?" in under 3 seconds: greeting/status on top, primary action within ≤2 taps, max 3–5 modules above the fold (Pareto).

- Bottom nav: 3–5 destinations, labels + icons always, active state always visible, positions never change.

- One slot reserved for unfinished work (Zeigarnik resume card) + a completion prompt while the profile is <100%.

### Search

- Search sits persistently at the top in content apps. On focus: recents first (max 5–8, individually deletable + Clear all), then trending/popular.

- Live suggestions update in <200ms with debounce; a clear (×) inside the field; result counts where helpful.

- Zero-results is a designed state: closest alternatives + a spelling hint + a clear-filters action. Never blank.

### Empty states

- Empty = icon/illustration + why it's empty (one line) + the one CTA that fills it ("No projects yet → Create your first project").

- Variants differ: first-run (teach + create CTA) · no-results (adjust search) · filter-emptied (reset filters) · all-cleared (positive — "You're all caught up").

### Error states

- Inline, at the exact field/section; problem + fix in plain language; data 100% preserved.

- A failed section gets a section-level error + its own Retry; the rest of the screen keeps working. Full-screen errors only for full-screen failures.

- Transient (network → Retry) vs permanent (fix input / grant permission) get different treatments. Offline: state what was lost AND what still works.

### Loading / Skeletons

- Under 200ms: show nothing (a flashing skeleton is worse than blank). Button actions: spinner inside the button + a verb label ("Saving…").

- Content: the skeleton matches the final layout exactly (same rows/heights/spacing — zero layout shift), subtle pulse, no racing shimmer, max one screenful.

- Measurable progress (upload/download) = a real progress bar + %. Fake spinners banned. Never skeleton + spinner on the same surface. Sections load independently.

### Paywall / Upgrade

- Placement: AFTER value has been demonstrated (end of onboarding or a premium-feature tap) — never before.

- Structure (top → bottom): close × top-left from the first second + Restore purchases top-right · benefit headline · 3–5 benefit rows (first row = the differentiator) · 2–3 plan cards · trial explanation · full-width CTA · legal line.

- Annual/recommended plan pre-selected + a savings badge ("Save 40%"); show the per-month equivalent on annual.

- Trial = a timeline with real dates (Today → reminder day → charge date) + "Cancel anytime" next to the CTA (the biggest trial fear is forgetting and being charged).

- Full price + renewal terms + post-trial price visible BEFORE the tap (store compliance). Countdowns, fake scarcity, and trial-toggles are banned (dark patterns + Apple 3.1.2 rejections).

- CTA label: "Continue" / "Start Free Trial" (descriptive labels win in testing). On exit, at most ONE offer; never stack dark-pattern modals.

### Settings

- Groups of 3–5: Account / Notifications / Appearance / Privacy / Support / About. Toggles apply instantly — no Save button for settings.

- Destructive items (Delete account, Log out) pinned at the very bottom, visually separated. Row = label + current value/chevron. Search only above 15 rows.

### Profile

- Header: avatar, name, identifier; one Edit action. Stats = only the 3–4 numbers the user actually cares about.

- Incomplete profile: a completion meter + only the next one missing item ("Add a photo to finish").

### Checkout / Form wizards

- 3–5 steps max, visible progress (bar + "Step 2 of 4"), each step = one chunk of 2–4 related fields. Back always available. Answers editable from the review step with no data loss.

- Autofill attributes on every field; keyboard matched to field type (email/tel/numeric); address autocomplete where possible; smart defaults pre-filled.

- Validation inline on blur; on submit-failure, an error summary on top anchored to the fields. Price/selection summary stays sticky through the whole flow. The commit button names the action ("Pay ₹499" — "Submit" is banned).

### Delete / Destructive

- Default = Undo, not confirmation: delete → item disappears + toast with Undo (5–10s). Modal confirmation only for the truly irreversible (account deletion, permanent data loss).

- Modal rules: title names the object ("Delete 'Project Alpha'?") · one line of consequence · verb buttons ("Delete project" in red / "Keep project") — Yes/No and OK/Cancel are banned · the destructive button is never the default-focus/primary position · type-the-name only for account-level destruction.

### Success states

- Every completed task gets explicit confirmation: check icon + what happened + what happens next ("Payment done — receipt sent to your email") + one clear next action.

- The celebratory-motion budget is spent HERE (the Peak-End moment) — not on navigation.

- Toasts auto-dismiss in 3–5s; Undo on reversible actions. Offer share/receipt on the success screen itself, not buried later.

## PART 8 — MOTION (durations, easing, choreography)

Rule zero: every animated element must specify duration + easing + stagger. "Add animations" is a slop instruction. Specificity is quality. (Sources: Material 3 motion tokens, MotionSites prompt anatomy, emilkowalski/skills.)

### Timing tokens (no number outside the scale)

- Short 50/100/150/200ms — state changes, icon swaps, switches, press feedback (~100ms typical).

- Medium 250/300/350/400ms — elements enter/exit, expanding cards, modals (~200–300ms); most transitions live here.

- Long 450/500/550/600ms — large surfaces, full-screen transitions only.

- Extra-long 700–1000ms — ambient/expressive only. Ceiling: no UI transition exceeds 1000ms.

- Bigger surface + longer travel = longer duration. Exit ≈ 60–75% of enter (classic pair: enter 225 / exit 195).

- An action the user performs 50× a day gets ≤150ms. Animation never blocks input (UI stays tappable during transitions).

### Feedback timing (hard limits)

- Tap/toggle/checkbox feedback starts within 100ms (feels instant).

- Perceived-response limit <400ms (Doherty) — slower requires a skeleton/progress.

- NN/g scale: 0.1s feels instant · 1s keeps flow (with an indicator) · 10s loses attention (progress + % mandatory for measurable waits over ~2s).

### Easing tokens

Enter: decelerate (0, 0, 0, 1) · Hero enter: emphasized-decelerate (0.05, 0.7, 0.1, 1) · Exit: accelerate (0.3, 0, 1, 1) · emphasized-accelerate (0.3, 0, 0.8, 0.15) · In-place/standard: (0.2, 0, 0, 1) · Smooth rise (0.16, 1, 0.3, 1) · Gentle settle (0.22, 1, 0.36, 1) · Spring (nav pill, playful) (0.34, 1.56, 0.64, 1) · Elastic (rare, expressive) (0.68, -0.55, 0.265, 1.55).

Linear is allowed only for progress bars and opacity/color fades. Linear on position/scale/size is banned. Asymmetry is mandatory: things arrive slow, leave fast.

### Micro-interaction recipes

- Button press: state change + scale 0.97 or ripple, 100ms. Switch thumb: 200ms emphasized. FAB morph / shared element: 300ms. Success check draw: 300–400ms. Error shake: ~300ms, small amplitude, once.

- Max 3 animated properties on one element at once; prefer transform + opacity (60fps); animating layout properties (width/height/top) is banned.

- Lists: stagger only the first viewport; cap total stagger; no full-duration entrance per item.

### Hero choreography (production values)

- Staggered Rise (default): headline translateY(40px)→0 + fade, 0.8s (0.16,1,0.3,1) → subhead 0.7s, delay 0.15 → CTA 0.6s, delay 0.3 → visual scale(0.95)→1, 1.0s.

- Split Reveal: columns enter from opposite sides, 0.8s (0.22,1,0.36,1); overline first (0.4s), CTA group last (delay 0.5s).

- Cinematic Fade: background slow zoom 1.05→1.0 over 2.0s → headline fade+rise 1.2s at delay 0.5 → CTA fade+scale at delay 1.2s.

- Typewriter + Reveal: ~0.03s/char + blinking cursor, then subhead/CTA cascade.

- Parallax layers: background 0.2× / mid 0.5× / foreground 0.8× scroll speed (restraint — never more).

- Timing table: overline 0.4s · headline 0.8s · subhead 0.6s (delay 0.25) · CTA 0.5s (delay 0.4) · hero image 1.0s · background 1.5–2.0s.

- Choreography rules: never everything at once · follow reading order (headline → sub → CTA) · background before content · smaller element = faster · total hero reveal <3s · slight stagger imperfection (0.12/0.18/0.25) feels human; uniform feels robotic.

### Scroll patterns

- Progressive disclosure: sections fade-up + stagger on entry; images scale 0.9→1.0; line-by-line text reveal for big statements; a scroll-progress indicator on long pages.

- Sticky storytelling: visual pinned, text blocks swap the visual per beat (Part 5, B3 #38).

- Horizontal scroll section: vertical scroll drives a horizontal track, with snap points + progress bar.

- Web: prefer native CSS animation-timeline (scroll-driven) and the View Transitions API for page navigation.

### Micro-detail patterns (web)

Magnetic button (pulls toward cursor within 50px; spring stiffness 200 / damping 20; click scale 0.95→1) · Text scramble on hover (~0.3s, resolves left→right) · Clip-path reveal (circle/wipe 0.6–0.8s) · Logo marquee (infinite) · Count-up stats · Scroll indicator loop · Breathing idle (3–4s cycle) on hero visuals only.

### Android / native notes

- Gesture-following motion runs on the native driver / Reanimated-style worklets — jank is slop.

- Sheets: drag-follow + velocity-based dismiss; light haptics on state changes (toggle, success).

- Mobile-native checklist: no sticky hover states on mobile (hover CSS sticks), the 100vh bug (use dvh/svh), tap lag (touch-action: manipulation), safe-area insets on every fixed element.

### When NOT to animate

- prefers-reduced-motion / system reduce-motion: decorative animation OFF; essential state changes become instant cuts or short fades. This degradation is expected, not optional.

- No more than 150ms on high-frequency actions · no looping decoration that competes with content · no animation that delays task-completion feedback · no long celebration anywhere except success.

- Break-ui test (review step): stress motion + layout with worst-case data (very long names, 100+ items, empty states, slow network) — if it breaks, fix it first.

## PART 9 — ASSETS DIRECTORY (free, license-verified)

Whatever the generator needs (icons, illustrations, photos…) comes from these sources — the model never draws or invents assets itself. ⚠️ = license trap, read first.

### Icons (default first, then alternates)

- Lucide (ISC) — 1,900 icons, 24px/2px stroke. DEFAULT. Inherits theme via currentColor.

- Phosphor (MIT) — 1,512 icons × 6 weights, for weight variation.

- Tabler (MIT) — 6,200+, niche domains (medical/finance/devices).

- Heroicons (MIT) — ~316 curated, for Tailwind projects.

- Material Symbols (Apache 2.0) — variable font, for pure Android/M3 output.

- Iconoir (MIT) — softer personality. Bootstrap Icons (MIT) — social glyphs. iconmonstr (free license) — bold filled.

- Iconify API (per-set license ⚠️ filter required) — 300k+ icons, universal fallback search.

- ⚠️ Flaticon (attribution mandatory on free tier) · Remix Icon (license changed 2026 — re-verify) · Font Awesome Free (CC BY 4.0).

### Illustrations

- unDraw — 1,000+ flat SVGs, on-site recoloring. Commercial use OK, no attribution. ⚠️ Redistribution packs + AI training banned. DEFAULT scene source.

- Open Peeps / Humaaans / Open Doodles (CC0) — people/characters, zero friction, may be vendored into the app.

- illlustrations.co (MIT) · IRA Design (MIT) — bundle-safe.

- ManyPixels — 20,000+, multiple styles; no attribution; no pack redistribution.

- Blush — parametric illustration system (specify attributes). DrawKit — packs + HANDZ set; ⚠️ template resale banned.

- ⚠️ Storyset (attribution required — the only free source of animated scenes; credit always) · Icons8/Ouch! (link-back on every use).

### Fonts

- Google Fonts — 1,900 families, OFL/Apache. Zero-risk default catalog.

- Fontshare — Satoshi, Clash Display, Chillax… (the escape from "Inter everywhere"). Ingest free-license families only.

- Fontsource — npm self-hosted delivery (the right mechanism for builder apps; no Google CDN dependency).

- Families by personality: Part 3 + the 8 pairings in Part 3.

### Color tools

- Radix Colors (MIT) — role-based scales, contrast by construction. Strongest for generators.

- Realtime Colors — 5-role live preview + CSS/Tailwind/OKLCH export. Free forever.

- Leonardo (Apache) — scales from target contrast ratios, scriptable. ColorBox — single-seed ramps. OKLCH Picker (MIT).

- Colormind API — ML palettes callable from inside the app. Color Hunt — curated taste-anchor seeds. Hue Codex — palette extraction from a reference screenshot (a builder feature!).

- ⚠️ Coolors free is capped at 5 colors — don't build a dependency on it. Khroma went paid.

### Backgrounds & textures

- Haikei — 15 SVG generators (waves/blobs/peaks/blurry gradients), no signup/watermark. DEFAULT procedural source.

- fffuel — ~65 generators (grain/noise/mesh); ⚠️ flatten grain SVGs to WebP on mobile.

- Pattern Monster (MIT, 320+) — the attribution-free replacement for Hero Patterns (CC BY ⚠️).

- Pattern.css (MIT) — pure-CSS patterns, zero images, token-recolorable.

- Open Props (MIT) — 30 gradients + shadow scales as tokens.

- Mesh gradients — CSS radial stacks = zero assets, premium hero look.

- ⚠️ svgbackgrounds.com — embed-only license; bundling banned.

### Animation assets

- LottieFiles — free files under the Lottie Simple License (commercial, attribution NOT required). ⚠️ Search mixes paid Marketplace files — the "Free" filter is mandatory.

- useAnimations — ~300 animated UI icons, free/no credit. Perfect for micro-interactions.

- Animated Icons — 4,000+ recolorable animated icons, free tier without attribution.

- Players: dotLottie / lottie-web / ThorVG (MIT/Apache) — free Android playback.

- ⚠️ Lordicon free (credit + CDN cap) · Rive free exports (splash screens; paid for shipped output) · GSAP (free but custom license — use in generated UI is fine, redistribution is not).

### Stock photos / video

- Unsplash (5M+) · Pexels (photo + video) · Pixabay — all free for commercial use, no attribution. Rules: unaltered resale banned, competing stock services banned, ML training banned on Unsplash, model/logo rights are not cleared.

- StockSnap (CC0) — zero-ambiguity filler.

### Mockups & device frames

- Presenta — completely free, no watermark/signup. DEFAULT.

- Hoverify Device Frames — 28 modern frames (Pixel/Galaxy/foldables included), real sizes, free for commercial use.

- ⚠️ Screenhance free = watermarked. Freepik mockups = attribution.

### Logos & brands

- Simple Icons (CC0 files) — 3,400+ brand SVGs + official hex, served from CDN. ⚠️ CC0 covers the files, not the brands — follow brand guidelines, never imply endorsement. Pin the version.

- Logoipsum — fictional placeholder logos. The correct choice for generated/demo brands (zero trademark risk).

### 3D

- 3dicons (CC0) — 1,500 3D icons in PNG + glTF. The gold standard for free.

- Poly Haven (CC0) — models/HDRI/textures (for Three.js webapps).

- ⚠️ Shapefest free = 512px only · Spline free exports are watermarked (treat as paid for shipped work).

### Sound (app micro-interactions)

- Kenney UI sounds (CC0) — 150 interface sounds, bundle freely. DEFAULT.

- Mixkit SFX / Pixabay SFX — fine inside an end product; standalone pack redistribution banned.

### Golden rules for assets

- Bundle-safe licenses only: MIT · Apache-2.0 · ISC · CC0 · OFL (plus verified custom-open licenses like unDraw/ManyPixels, on their terms).

- Attribution-required sources (Flaticon, Storyset, Icons8, Lordicon, Hero Patterns, FA Free) only when the generator auto-inserts the credit — otherwise skip.

- Hard-code the permissive filter on per-file-license sources (Iconify, SVG Repo, Freesound).

- Brand logos always from Simple Icons; never from model memory.

## PART 10 — GITHUB DESIGN SKILLS & REPOS (collection + architecture lessons)

Star counts verified on the repo pages on 2026-11-07 (they drift over time).

### Architecture lessons (the most important part)

- ui-ux-pro-max (146k★, MIT) — the generation pipeline: request → 5 parallel domain searches (product/style/palette/pattern/typography) → a reasoning engine (per-industry rules + anti-patterns) → a fixed output contract (Pattern + Style + Colors + Type + Effects + Avoid list + checklist). Catalogs live in searchable CSV/JSON — not one giant prompt. This document's assembly order (Part 0 §8) follows that model.

- impeccable (105k★, Apache) — the Review Loop blueprint: (a) a 62-rule deterministic slop detector (no LLM/API needed — see Part 2B checks), (b) named single-move steer commands (bolder/quieter/distill/colorize/typeset/layout/animate/delight) — never one vague "make it better" prompt, (c) the PRODUCT.md + DESIGN.md two-file context (this system ships both in every project).

- taste-skill (111k★, MIT) — Deal, don't describe: deal randomly from vetted palette/font/layout combos (kills the same-output problem), gate effects by category (WebGL for tech/gaming, zero for calm pages), and a headless render checker (contrast/alignment/placeholder PASS/WARN/FAIL at 1440/1024/768/390 widths).

- anthropics/skills (188k★, per-skill Apache) — the SKILL.md packaging format (frontmatter + Markdown body + references) is the format for the host app's own modules. Its frontend-design anti-default warnings are in Part 2C.

- Style Dictionary (Apache) — the token pipeline: one tokens.json compiles to Android XML/Compose + CSS variables + Tailwind @theme. The bridge between this system's three outputs.

- The DESIGN.md format (origin: Google Stitch) — the winning interchange format. VoltAgent/awesome-design-md (126k★, MIT): ~84 brand DESIGN.md files = ready theme presets (see Part 3). Ant Design (100k★) also keeps a DESIGN.md at its repo root.

- emilkowalski/skills (54k★, MIT) — the source of the motion layer (Part 8): animate-expo (Android/native), the mobile-native checklist, the break-ui stress test.

- Meta-lesson (from MUI/Storybook): large projects now bundle agent skills inside their own repos — ship a DESIGN.md inside every generated project so the user's next edit session stays on-brand.

### Collection — skills

| Repo | Stars | What to take from it | 

| anthropics/skills | 188k | frontend-design, canvas-design (54 OFL fonts), brand-guidelines, theme-factory — format + rules | 

| nextlevelbuilder/ui-ux-pro-max-skill | 146k | The full pipeline architecture + catalog model | 

| Leonxlnx/taste-skill | 111k | The dealing mechanism + checker; siblings: taste-skill-classics, imagegen-skills | 

| pbakaus/impeccable | 105k | Slop-detector rules + steer commands + the PRODUCT.md pattern | 

| ComposioHQ/awesome-claude-skills | 79k | A discovery index of 1000+ skills | 

| emilkowalski/skills | 54k | 17 motion/interaction skills + cheatsheets | 

| VoltAgent/awesome-agent-skills | 37k | A catalog of official vendor skills (Figma, GSAP, Stitch, Flutter) | 

| Community (unverified stars) | — | Nutlope/hallmark · Owl-Listener/designer-skills (63 design skills) · inclusive-design-skills (40 accessibility skills) · jakubkrehel/skills (granular better-* skills) · ibelick/ui-skills (create-design-md) · vercel-labs/agent-skills (web-design-guidelines) · addyosmani/web-quality-skills | 

### Collection — open design tools

| Repo | Stars | What to take from it | 

| penpot/penpot (MPL-2.0) | 62k | The tokens-as-truth model, open formats, MCP-for-agents | 

| onlook-dev/onlook (Apache) | 27k | The DOM↔code visual-editing loop = the reference for a preview-edit feature | 

### Collection — mega repos

| Repo | Stars | What to take from it | 

| shadcn-ui/ui (MIT) | 128k | The web output format: owned-source components, CSS-variable theming | 

| ant-design/ant-design (MIT) | 100k | The seed→map→alias token architecture + a repo-root DESIGN.md | 

| mui/material-ui (MIT) | 99k | M3 component/state specs (the Android baseline) | 

| tailwindlabs/tailwindcss (MIT) | 98k | v4 @theme = the web token format | 

| storybookjs/storybook (MIT) | 91k | The "story" model for vault components (spec + variants + states) | 

| bradtraversy/design-resources-for-developers (MIT) | 67k | A pre-vetted resource mega-list (re-verify licenses per item) | 

| motiondivision/motion (MIT) | 35k | The default webapp animation library + its own /motion skill | 

| greensock/GSAP (custom ⚠️) | 30k | Learn the patterns; never redistribute the code | 

| alexpate/awesome-design-systems (Unlicense) | 26k | 200+ real design systems — for grounding presets | 

| simple-icons (CC0) | 26k | The only correct source of brand icons | 

| lucide (ISC) | 26k | The default icon set | 

| radix-ui/primitives (MIT) | 19k | Web behavior/accessibility primitives (dialog, menu, tabs) | 

| style-dictionary (Apache) | 5k | The token compile pipeline (Android + web + webapp) | 

### Bundle-safe list (verified licenses)

MIT: ui-ux-pro-max · taste-skill · emilkowalski/skills · shadcn · Ant Design · MUI · Tailwind · Storybook · Motion · Radix · awesome-design-md · Magic UI · Uiverse  |  Apache-2.0: anthropics/skills (per-skill) · impeccable · Onlook · Style Dictionary  |  CC0: simple-icons files, 3dicons, Open Peeps  |  ISC: Lucide.

⚠️ Flags: GSAP custom license (use OK, redistribute no) · Penpot MPL-2.0 (use OK, its files are copyleft) · React Bits Commons Clause (patterns only).

## PART 11 — REVIEW LOOP (design critic)

Generate → Screenshot → Deterministic checks → LLM critique → Steer-fix → Re-check. Without this loop the first draft becomes final — the original failure mode. (Blueprint: impeccable + the taste-skill checker.)

### Step 1 — Deterministic checks (no LLM, no API cost)

Run the 15 mechanical checks from Part 2B. Any FAIL triggers an automatic fix attempt first (mechanical fixes like token swaps happen in code). Results are PASS / WARN / FAIL per check, attached to the screenshot.

### Step 2 — LLM critique prompt (ready template for a vision model)

```
You are a senior product designer reviewing generated UI.
Below are screenshot(s) plus this project's DESIGN.md (theme tokens).

Score each category 0–10 and give a SPECIFIC fix for every deduction
(never "increase spacing" — write "card padding 16 → 24, section gap 32 → 48"):

1. HIERARCHY — Does the most important thing catch the eye first? Is there contrast in the type scale?
2. SPACING & RHYTHM — Is the token rhythm consistent? Any cramped or random gaps?
3. COLOR — One-accent discipline? Any risky contrast? Any gradient slop?
4. TYPOGRAPHY — Do headings have display character? Is body text readable?
5. COMPONENTS — Are states complete (pressed/disabled/loading)? Icons from one family?
6. UX FLOW — Is the primary action in the thumb zone / easy reach? One screen = one decision?
7. STATES — Have loading/empty/error been considered for this screen?
8. ORIGINALITY — Does this look like a generic template, or does the app have its own identity?

Output format:
SCORE: X/80
TOP 3 FIXES: (highest impact first, with exact values)
SLOP FLAGS: (anything from the Part 2 ban list, cited by number)
VERDICT: SHIP / FIX-FIRST / REGENERATE
```

- Score < 56/80, or any SLOP FLAG from a hard ban = FIX-FIRST or REGENERATE.

- Always give the reviewer the theme tokens + the anti-slop list as context — critique without context is generic.

### Step 3 — Steer commands (one-move fixes — never "make it better")

Following the impeccable model, every fix is a named single-move command; the model executes only that move:

- distill — remove clutter: demote/remove secondary elements, increase whitespace.

- bolder — sharpen hierarchy: display size +1 step, more weight contrast, commit harder to the accent.

- quieter — reduce saturation, soften shadows, remove one decoration layer.

- colorize — repair the tint system: re-derive category tints from one muted family.

- typeset — typography only: scale, line-height, tracking, pairing discipline.

- layout — structure only: alignment grid, section rhythm, container widths.

- animate — apply/repair choreography per the Part 8 motion tokens.

- harden — worst-case data stress (break-ui): long names, 100+ items, empty states, slow-network states — fix whatever breaks.

Max 2 steer commands per round — more than that means regeneration is the better move.

### Step 4 — Re-check & ship rules

- After a fix: new screenshot → Step 1 again (deterministic) → ship if the score improved, otherwise one more round (max 2 LLM rounds — beyond that, the theme/prompt itself is wrong; go back).

- Multi-size check: mobile 390 · tablet 768 · desktop 1440 (for web) — a broken layout at any size = FIX-FIRST.

- On ship, update the project's DESIGN.md + PRODUCT.md with the decisions made, so the next generation learns from this review history.

## PART 12 — PROMPT TEMPLATES (generation recipes)

Based on the MotionSites prompt anatomy: [Context] + [Layout] + [Motion choreography] + [Exact timings] + [Named easings] + [Interaction behavior] + [Color/Type] + [Technical constraints]. A vague prompt produces vague output. Every field is mandatory.

### Master template (base for every platform)

```
ROLE: You are a senior UI engineer who follows the Design Brain system.
CONTEXT INJECTED: Part 0 master rules + Theme: <THEME_NAME> (locked tokens) + Platform: <Android/Web/Webapp>

BUILD: <app name + what it does in one line>
SCREENS/SECTIONS (from Part 5 only — use these archetypes):
  1. <archetype #> — <variation for this app>
  2. ...
COMPONENTS (from Part 4): <list>
CONTENT: <realistic copy/data — names, numbers, everything reads real; placeholders banned>

MOTION: <for every animated element: duration token + easing name + stagger order>
INTERACTIONS: <what happens on tap/hover/scroll — respecting the ≤100ms feedback rule>
STATES: also build the Loading/Empty/Error/Success variants of every data screen.

OUTPUT CONTRACT (Part 0 §9): deliver with the AVOID list + self-check.
No theme token may drift.
```

### Android recipe (specific notes)

- Language: Material 3 Expressive (tonal palette, expressive shapes, FAB where idiomatic, floating toolbars/nav) — not an iOS glass clone.

- Nav: the floating pill bottom bar (Part 3 signature) or the M3 navigation bar, per archetype; labels always.

- Tokens in dp/sp; a dark-mode variant ships with every screen (warm dark).

- Flows follow the Part 7 structures verbatim (OTP, paywall, onboarding caps).

### Website / Landing recipe

- Canonical section order: Nav → Hero (pick an archetype: Centered / Split / Cinematic / Bento / Typographic / App-Download) → Logo Cloud → Features Bento → Showcase/How → Proof (Testimonials/Stats) → Pricing → FAQ → CTA Band → Footer.

- Hero choreography uses the Part 8 production values (Staggered Rise by default).

- The product's REAL screenshot/demo goes in the hero — abstract gradient art is banned (a wash/aurora is allowed only as a background layer).

- One page = one primary CTA goal. A focused campaign page runs 1–3 viewport scrolls; a full marketing page 5–10 sections.

### Webapp recipe

- Shell first: sidebar 240 or top-nav (one only) + content area; density tokens one step tighter.

- Behavior at primitives grade: focus trap + ARIA + keyboard patterns for dialogs/menus/tabs (the Radix model) — hand-rolled behavior is banned.

- All 5 states on every screen; tables at row-height 48, hairline dividers, no zebra stripes.

- Repeat-user accelerators: recents, saved items, shortcuts (Part 6, rule 7).

### Dealing (theme/layout selection — the taste-skill model)

Do not show the user 100 options. The system deals:

- From app type + mood, deal 3 vetted theme cards (palette + fonts + a one-line mood) — the user picks one.

- Hero/screen layouts are also dealt from tested archetypes (Part 5) — never random invention.

- Effects are category-gated: calm/wellness/editorial apps get ZERO heavy animation/WebGL; tech/gaming get a budget (max 1 heavy background per page, mobile fallback mandatory).

### Few-shot rule

Keep 1 gold example per platform in the prompt (one screen's ideal output, annotated with tokens) — this lifts quality more than any other single technique. The example bank evolves with the pack: any generated output scoring 72+/80 in the review loop becomes the next few-shot example (a self-improving library).

### Banned prompt patterns

- "Make it modern and beautiful" (directionless) → name the theme + archetypes.

- "Add animations" → specify duration + easing + stagger.

- "Use nice colors" → lock a theme; the model never chooses hex values.

- The whole app (20 screens) in one prompt → generate screen by screen, with shared tokens.

- "Make it like this" with a reference screenshot but no build notes → first extract tokens/build-notes from the screenshot (decisions, not pixels — the vibrant.design model), then generate.

## APPENDIX — COUNTS & HONEST NOTES

### What this system contains

- Screen/section archetypes: 114 · Cataloged Uiverse elements: 135 · Magic UI components: ~90

- UX laws: 15 · Nielsen heuristics as rules: 10 · Flows: 14 · Anti-slop bans: 36 · Automated checks: 15

- Theme presets: 20+ (plus a source of 84 brand DESIGN.md presets) · Asset resources: 85+ · GitHub repos/skills cataloged: 40+

- Proven font pairings: 8 · Motion recipes: 15+ · Hero archetypes: 6

- Total cataloged items: 550+

### Honest notes (limits of the research)

- Google Stitch's official DESIGN.md spec page could not be fetched during research — this document's file anatomy is corroborated by the VoltAgent collection (126k★) and Ant Design's own DESIGN.md, but it is not the official spec verbatim.

- Some community skill repos' star counts are unverified (flagged in Part 10).

- Pinterest blocked direct browsing — its patterns were corroborated via Dribbble, mirrors/reposts, and published design-system documentation.

- godly.website now redirects to recent.design — in the owner's original 13-site list, the two are the same source.

- This system is living: outputs scoring 72+/80 in the review loop join the few-shot example bank, and rules keep being tuned against real outputs.

DESIGN BRAIN — Complete UI/UX Generation System · Single-file edition · Compiled 2026-11-07 from the Design Brain Pack v2 (14 files + 8 research files) · For the product owner's open-source AI UI builder (Android apps, websites, webapps).