package design.a0.app.data

import kotlin.math.max
import kotlin.math.min

/**
 * The design engine: the prompt the model receives, the audit that decides whether
 * the result is shippable, and the code export. All deterministic, all local.
 */
object DesignEngine {

    // ------------------------------------------------------------------ prompt
    const val SYSTEM_PROMPT = """You are a0, a senior product designer who returns machine-readable design specs.

You design one product per request, from a locked token set, and you output JSON only.

HARD RULES
1. One theme, locked: one accent, one accent-2, one surface, one ink. No second chromatic family.
2. Never use indigo #6366F1, blue-purple gradients, neon glows, pure #000000 text or pure #FFFFFF page backgrounds.
3. Exactly ONE button block per screen (the primary action). Never two competing CTAs.
4. Radius between 8 and 34. Use 20–28 for cards. Typeface is one of: sans, serif, mono.
5. Real content only, written from the brief: real product names, real labels, real prices in the brief's currency. Never "lorem ipsum", "Your text here", "Feature One", "Welcome to X", "Unlock the power of…".
6. No emoji anywhere. No marketing filler. Copy is specific, in the brief's language.
7. 2 to 4 screens. Each screen has 4 to 7 blocks, ordered: context (sub) → headline → hero or stat → content (cards/rows) → button.
8. Headlines are 2 lines maximum, under 60 characters, and name the subject of the brief.

OUTPUT — JSON ONLY, no prose, no markdown fences:
{
  "name": "Product name, 1-3 words",
  "tagline": "one short line describing the product",
  "theme": {"bg": "#hex", "surface": "#hex", "ink": "#hex", "accent": "#hex", "accent2": "#hex", "radius": 26, "font": "sans"},
  "screens": [
    {"id": "home", "title": "Home", "blocks": [
      {"type": "sub", "text": "context line"},
      {"type": "headline", "text": "Two lines\nof headline"},
      {"type": "hero", "title": "Featured item", "meta": "short meta"},
      {"type": "button", "text": "Primary action"},
      {"type": "cards", "items": [{"title": "Card title", "meta": "one line"}]},
      {"type": "rows", "items": [{"title": "Row", "meta": "meta", "trail": "value"}]},
      {"type": "stat", "text": "42", "meta": "what the number is"},
      {"type": "note", "text": "supporting line"}
    ]}
  ]
}

Block types allowed: sub, headline, hero, button, cards, rows, stat, note. Nothing else."""

    fun userPrompt(
        brief: String,
        mode: String,
        answers: List<String>,
        previous: Spec?,
        failures: List<Finding>
    ): String = buildString {
        appendLine("BRIEF: $brief")
        if (answers.isNotEmpty()) appendLine("CHOSEN DIRECTION: " + answers.joinToString(" · "))
        appendLine("MODE: $mode")
        if (previous != null) {
            appendLine()
            appendLine("The user is refining this existing design. Keep its identity unless asked otherwise.")
            appendLine(SpecJson.encode(previous))
        }
        if (failures.isNotEmpty()) {
            appendLine()
            appendLine("Your previous answer failed these checks. Fix exactly these and return the full JSON again:")
            failures.forEach { appendLine("- ${it.title}: ${it.detail}") }
        }
        appendLine()
        appendLine("Return the JSON now.")
    }

    // ------------------------------------------------------------------- audit
    data class Finding(val title: String, val detail: String, val ok: Boolean)

    fun audit(spec: Spec): List<Finding> {
        val findings = mutableListOf<Finding>()
        val t = spec.theme

        val body = contrast(t.ink, t.bg)
        findings += Finding(
            "Contrast",
            if (body == null) "Could not read the ink or background colour."
            else "ink on bg ${fmt(body)}:1 " + if (body >= 4.5) "(AA)" else "(needs ≥4.5)",
            body != null && body >= 4.5
        )

        val accentOk = !isBluePurple(t.accent) && !isBluePurple(t.accent2)
        findings += Finding(
            "Accent",
            if (accentOk) "one accent family, no banned hue" else "banned blue-purple accent ${t.accent}",
            accentOk
        )

        val bgOk = !isPure(t.bg) && !isPure(t.ink)
        findings += Finding(
            "Pure black / white",
            if (bgOk) "warm background, ink off-black" else "pure #000000 or #FFFFFF in use",
            bgOk
        )

        val ctaIssues = spec.screens.mapNotNull { s ->
            val n = s.blocks.count { it.type == "button" }
            if (n != 1) "${s.title.ifBlank { s.id }} has $n" else null
        }
        findings += Finding(
            "Primary CTA",
            if (ctaIssues.isEmpty()) "exactly one primary action per screen"
            else ctaIssues.joinToString("; "),
            ctaIssues.isEmpty()
        )

        val unknown = spec.screens.flatMap { s -> s.blocks.filterNot { it.isKnown }.map { it.type } }
        findings += Finding(
            "Block types",
            if (unknown.isEmpty()) "all blocks are renderable" else "unknown types: ${unknown.distinct()}",
            unknown.isEmpty()
        )

        val placeholderHits = placeholderHits(spec)
        findings += Finding(
            "Placeholder copy",
            if (placeholderHits.isEmpty()) "no lorem or invented placeholders" else placeholderHits.joinToString(", "),
            placeholderHits.isEmpty()
        )

        val screensOk = spec.screens.size in 2..5
        findings += Finding(
            "Screen count",
            "${spec.screens.size} screens " + if (screensOk) "(2–5)" else "(needs 2–5)",
            screensOk
        )

        val radiusOk = t.radius in 8..34
        findings += Finding("Radius", "${t.radius}dp " + if (radiusOk) "" else "(needs 8–34)", radiusOk)

        val emoji = spec.screens.any { s ->
            (s.blocks.map { it.text + it.title + it.meta } + s.blocks.flatMap { b -> b.items.map { it.title + it.meta } })
                .any { containsEmoji(it) }
        }
        findings += Finding("Icons", if (emoji) "emoji found in copy" else "no emoji used as icons", !emoji)

        val typeOk = t.font in setOf("sans", "serif", "mono")
        findings += Finding("Typeface", "font=${t.font}", typeOk)

        return findings
    }

    fun verdict(findings: List<Finding>): String {
        val failed = findings.count { !it.ok }
        return when {
            failed == 0 -> "SHIP"
            failed <= 3 -> "FIX-FIRST"
            else -> "REGENERATE"
        }
    }

    private fun placeholderHits(spec: Spec): List<String> {
        val banned = listOf(
            "lorem ipsum", "your text here", "your company", "feature one", "feature two",
            "feature three", "placeholder", "sample text", "company name", "todo:"
        )
        val strings = spec.screens.flatMap { s ->
            s.blocks.flatMap { b ->
                listOf(b.text, b.title, b.meta) + b.items.flatMap { listOf(it.title, it.meta, it.trail) }
            }
        } + listOf(spec.name, spec.tagline)
        return strings.map { it.lowercase() }
            .flatMap { value -> banned.filter { value.contains(it) } }
            .distinct()
    }

    // ------------------------------------------------------------ code export
    fun composeCode(spec: Spec): String {
        val screen = spec.screens.firstOrNull() ?: return "// no screens in this design"
        val t = spec.theme
        val family = when (t.font) {
            "serif" -> "InstrumentSerif"
            "mono" -> "GeistMono"
            else -> "DmSans"
        }
        return buildString {
            appendLine("// Generated by a0 from the spec below — tokens, not magic numbers.")
            appendLine("object ${screen.id.replaceFirstChar { it.uppercase() }}Tokens {")
            appendLine("    val bg = Color(0xFF${t.bg.cleanHex()})")
            appendLine("    val surface = Color(0xFF${t.surface.cleanHex()})")
            appendLine("    val ink = Color(0xFF${t.ink.cleanHex()})")
            appendLine("    val accent = Color(0xFF${t.accent.cleanHex()})")
            appendLine("    val accent2 = Color(0xFF${t.accent2.cleanHex()})")
            appendLine("    val radius = ${t.radius}.dp")
            appendLine("    val family = $family")
            appendLine("}")
            appendLine()
            appendLine("@Composable")
            appendLine("fun ${screen.id.replaceFirstChar { it.uppercase() }}Screen() {")
            appendLine("    Column(")
            appendLine("        Modifier.fillMaxSize().background(Tokens.bg).padding(20.dp),")
            appendLine("        verticalArrangement = Arrangement.spacedBy(16.dp)")
            appendLine("    ) {")
            screen.blocks.forEach { block ->
                when (block.type) {
                    "sub" -> appendLine("        Text(\"${block.text.esc()}\", fontSize = 12.sp, color = Tokens.ink.copy(alpha = .6f))")
                    "headline" -> appendLine(
                        "        Text(\"${block.text.esc()}\", fontFamily = Tokens.family,\n" +
                            "            fontSize = 34.sp, lineHeight = 37.sp, fontWeight = FontWeight.Bold, color = Tokens.ink)"
                    )
                    "hero" -> appendLine(
                        "        Box(Modifier.fillMaxWidth().height(130.dp)\n" +
                            "            .clip(RoundedCornerShape(Tokens.radius.dp))\n" +
                            "            .background(Brush.linearGradient(listOf(Tokens.accent, Tokens.accent2)))) {\n" +
                            "            Text(\"${block.title.esc()}\", Modifier.align(Alignment.BottomStart).padding(16.dp), color = Color.White)\n" +
                            "        }"
                    )
                    "button" -> appendLine(
                        "        Box(Modifier.fillMaxWidth().height(48.dp)\n" +
                            "            .clip(RoundedCornerShape(99.dp)).background(Tokens.ink),\n" +
                            "            contentAlignment = Alignment.Center) { Text(\"${block.text.esc()}\", color = Tokens.bg) }"
                    )
                    "stat" -> appendLine(
                        "        Text(\"${block.text.esc()}\", fontSize = 44.sp, fontWeight = FontWeight.Bold, color = Tokens.ink)"
                    )
                    "note" -> appendLine("        Text(\"${block.text.esc()}\", fontSize = 14.sp, color = Tokens.ink.copy(alpha = .7f))")
                    "cards" -> {
                        appendLine("        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {")
                        block.items.forEach { item ->
                            appendLine(
                                "            Column(Modifier.weight(1f).height(90.dp)\n" +
                                    "                .clip(RoundedCornerShape((Tokens.radius * .77f).dp))\n" +
                                    "                .background(Tokens.surface).padding(12.dp)) {\n" +
                                    "                Text(\"${item.title.esc()}\", color = Tokens.ink)\n" +
                                    "                Text(\"${item.meta.esc()}\", color = Tokens.ink.copy(alpha = .6f), fontSize = 12.sp)\n" +
                                    "            }"
                            )
                        }
                        appendLine("        }")
                    }
                    "rows" -> block.items.forEach { item ->
                        appendLine(
                            "        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))\n" +
                                "            .background(Tokens.surface).padding(12.dp),\n" +
                                "            horizontalArrangement = Arrangement.SpaceBetween) {\n" +
                                "            Text(\"${item.title.esc()}\", color = Tokens.ink)\n" +
                                "            Text(\"${item.trail.ifBlank { item.meta }.esc()}\", color = Tokens.ink.copy(alpha = .7f))\n" +
                                "        }"
                        )
                    }
                }
            }
            appendLine("    }")
            appendLine("}")
        }
    }

    // ------------------------------------------------------------- colour maths
    fun contrast(a: String, b: String): Double? {
        val la = luminance(a) ?: return null
        val lb = luminance(b) ?: return null
        return (max(la, lb) + 0.05) / (min(la, lb) + 0.05)
    }

    private fun luminance(hex: String): Double? {
        val rgb = rgb(hex) ?: return null
        fun channel(v: Int): Double {
            val s = v / 255.0
            return if (s <= 0.03928) s / 12.92 else Math.pow((s + 0.055) / 1.055, 2.4)
        }
        return 0.2126 * channel(rgb.first) + 0.7152 * channel(rgb.second) + 0.0722 * channel(rgb.third)
    }

    private fun rgb(hex: String): Triple<Int, Int, Int>? {
        val v = hex.cleanHex()
        if (v.length != 6) return null
        return runCatching {
            Triple(v.substring(0, 2).toInt(16), v.substring(2, 4).toInt(16), v.substring(4, 6).toInt(16))
        }.getOrNull()
    }

    private fun isPure(hex: String): Boolean {
        val v = hex.cleanHex().lowercase()
        return v == "000000" || v == "ffffff"
    }

    private fun isBluePurple(hex: String): Boolean {
        val rgb = rgb(hex) ?: return false
        val (r, g, b) = rgb
        return b > 150 && b > r + 30 && b > g + 30
    }

    private fun containsEmoji(value: String): Boolean =
        value.any { it.code in 0x1F300..0x1FAFF || it.code in 0x2600..0x27BF }

    private fun fmt(v: Double) = String.format("%.2f", v)
}

fun String.cleanHex(): String = trim().removePrefix("#").uppercase()

private fun String.esc(): String = replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
