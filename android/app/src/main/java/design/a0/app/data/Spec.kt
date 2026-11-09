package design.a0.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

/**
 * The real design contract.
 *
 * The model returns exactly this JSON; the app renders it, audits it and stores it.
 * There is no sample project in the app — every spec on screen came from a model
 * call that actually happened.
 */
@Serializable
data class Spec(
    val name: String = "",
    val tagline: String = "",
    val theme: SpecTheme = SpecTheme(),
    val screens: List<SpecScreen> = emptyList()
) {
    val isValid: Boolean get() = screens.isNotEmpty() && screens.any { it.blocks.isNotEmpty() }
}

@Serializable
data class SpecTheme(
    val bg: String = "#F8FBF4",
    val surface: String = "#FFFFFF",
    val ink: String = "#14171A",
    val accent: String = "#7FCF2E",
    val accent2: String = "#C6F36A",
    val radius: Int = 26,
    val font: String = "sans"        // sans | serif | mono
)

@Serializable
data class SpecScreen(
    val id: String = "screen",
    val title: String = "",
    val blocks: List<Block> = emptyList()
)

@Serializable
data class Block(
    val type: String,                 // headline | sub | hero | button | cards | rows | stat | note
    val text: String = "",
    val title: String = "",
    val meta: String = "",
    val items: List<Item> = emptyList()
) {
    val isKnown: Boolean
        get() = type in setOf("headline", "sub", "hero", "button", "cards", "rows", "stat", "note")
}

@Serializable
data class Item(
    val title: String = "",
    val meta: String = "",
    @SerialName("trail") val trail: String = ""
)

@Serializable
data class Version(
    val id: String,
    val label: String,
    val createdAt: Long,
    val spec: Spec
)

@Serializable
data class Project(
    val id: String,
    val name: String,
    val createdAt: Long,
    val versions: List<Version>
) {
    val latest: Version? get() = versions.maxByOrNull { it.createdAt }
}

@Serializable
data class UsageEntry(
    val at: Long,
    val provider: String,
    val model: String,
    val promptTokens: Int = 0,
    val completionTokens: Int = 0,
    val costUsd: Double = 0.0,
    val project: String = ""
) {
    val totalTokens: Int get() = promptTokens + completionTokens
}

/** Wire format for the app → backend call. */
@Serializable
data class GenerateRequest(
    val prompt: String,
    val model: String,
    val mode: String,
    val platform: String = "android",
    val system_prompt: String = "",
    val theme: String = ""
)

object SpecJson {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        prettyPrint = false
    }

    /**
     * Models wrap JSON in prose or fences often enough that a strict parse is not
     * enough. We extract the first balanced object and parse that; anything else
     * surfaces as a real error instead of a silent empty screen.
     */
    fun parse(raw: String): Result<Spec> = runCatching {
        val text = raw.trim()
        val start = text.indexOf('{')
        if (start < 0) error("The model did not return JSON. It said: " + text.take(180))
        var depth = 0
        var end = -1
        var inString = false
        var escaped = false
        for (i in start until text.length) {
            val c = text[i]
            when {
                escaped -> escaped = false
                c == '\\' -> escaped = true
                c == '"' -> inString = !inString
                inString -> Unit
                c == '{' -> depth++
                c == '}' -> {
                    depth--
                    if (depth == 0) { end = i; break }
                }
            }
        }
        if (end < 0) error("The model returned truncated JSON.")
        val spec = json.decodeFromString(Spec.serializer(), text.substring(start, end + 1))
        if (!spec.isValid) error("The model returned a design with no screens.")
        spec
    }

    fun encode(spec: Spec): String = json.encodeToString(Spec.serializer(), spec)

    fun root(raw: String): JsonObject? = runCatching {
        json.parseToJsonElement(raw).jsonObject
    }.getOrNull()
}

/** Compact, human-readable summary used in share/export and in the project list. */
fun Spec.summary(): String = buildString {
    appendLine("# $name")
    if (tagline.isNotBlank()) appendLine(tagline)
    appendLine()
    appendLine("## Tokens")
    appendLine("- bg ${theme.bg} · surface ${theme.surface} · ink ${theme.ink}")
    appendLine("- accent ${theme.accent} · accent-2 ${theme.accent2}")
    appendLine("- radius ${theme.radius}dp · typeface ${theme.font}")
    appendLine()
    screens.forEach { screen ->
        appendLine("## ${screen.title.ifBlank { screen.id }}")
        screen.blocks.forEach { block ->
            when (block.type) {
                "headline", "sub", "note" -> appendLine("- ${block.text}")
                "hero" -> appendLine("- hero: ${block.title} — ${block.meta}")
                "button" -> appendLine("- CTA: ${block.text}")
                "stat" -> appendLine("- stat: ${block.text} ${block.meta}")
                "cards", "rows" -> block.items.forEach { appendLine("  · ${it.title} — ${it.meta} ${it.trail}") }
            }
        }
        appendLine()
    }
}
