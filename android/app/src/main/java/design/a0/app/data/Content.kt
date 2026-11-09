package design.a0.app.data

/** Everything the UI lists, in one place — the app's static content model. */

data class ModelOption(val provider: String, val name: String, val meta: String)

data class ProjectItem(val name: String, val version: String, val pastel: Int)

data class StyleItem(val name: String, val subtitle: String, val pastel: Int, val category: String)

data class SettingRow(
    val title: String,
    val icon: String,
    val pastel: Int,
    val value: String,
    val kind: SettingKind = SettingKind.Chevron
)

enum class SettingKind { Chevron, Toggle, Value }

data class AttachOption(val label: String, val icon: String, val pastel: Int)

data class HistoryVersion(val tag: String, val title: String, val whenLabel: String, val accentIndex: Int, val radius: Int, val serif: Boolean)

data class NavItem(val id: String, val label: String, val icon: String, val pastel: Int)

object Content {
    val models = listOf(
        ModelOption("Anthropic", "Claude Opus", "200k · Slow · $$$"),
        ModelOption("Anthropic", "Claude Sonnet", "200k · Fast · $$"),
        ModelOption("Google", "Gemini Flash", "1M · Fast · $"),
        ModelOption("OpenAI", "GPT-5", "400k · Medium · $$"),
        ModelOption("DeepSeek", "DeepSeek V3", "128k · Fast · $")
    )

    val suggestions = listOf(
        "A quiet coffee subscription app for home brewers",
        "Landing page for a ceramics studio in Jaipur",
        "A budgeting app that feels like a paper journal"
    )

    val attachOptions = listOf(
        AttachOption("Image", "img", 0),
        AttachOption("Sketch", "edit", 2),
        AttachOption("Screenshot", "phone", 1),
        AttachOption("Website URL", "globe", 4),
        AttachOption("Figma file", "file", 3),
        AttachOption("Voice", "mic", 0)
    )

    val projects = listOf(
        ProjectItem("Moss Coffee", "v3", 0),
        ProjectItem("Paper Budget", "v7", 1),
        ProjectItem("Clay Studio", "v2", 2),
        ProjectItem("Salt Notes", "v1", 4)
    )

    val styles = listOf(
        StyleItem("Warm Minimal", "Editorial · 12k designs", 0, "Apps"),
        StyleItem("Quiet Luxury", "Premium · 9k designs", 4, "Websites"),
        StyleItem("Soft Brutalist", "Bold · 7k designs", 2, "Landing"),
        StyleItem("Retro Paper", "Print feel · 5k designs", 1, "Apps")
    )

    val newDesigns = listOf(
        StyleItem("Fieldnotes", "Journal app · Quiet", 1, "Apps"),
        StyleItem("Atelier", "Studio site · Editorial", 0, "Websites"),
        StyleItem("Maison", "Fashion landing · Luxe", 3, "Landing")
    )

    val filters = listOf("All", "Apps", "Websites", "Landing")

    val providers = listOf("OpenRouter", "OpenAI", "Anthropic", "Google Gemini", "Groq", "DeepSeek", "Custom URL")

    val settings = listOf(
        SettingRow("API keys", "key", 3, ">"),
        SettingRow("Default models", "spark", 4, ">"),
        SettingRow("Usage and cost", "chart", 1, ">"),
        SettingRow("Font size", "type", 0, "Medium", SettingKind.Value),
        SettingRow("Haptics", "phone", 2, "tog", SettingKind.Toggle),
        SettingRow("Language", "globe", 4, "English", SettingKind.Value),
        SettingRow("System prompt", "file", 3, ">"),
        SettingRow("Privacy, local storage", "lock", 1, "tog", SettingKind.Toggle),
        SettingRow("Open-source licence", "file", 0, "MIT"),
        SettingRow("GitHub", "git", 2, ">")
    )

    val drawerNav = listOf(
        NavItem("prj", "Projects", "folder", 0),
        NavItem("tpl", "Templates", "grid", 3),
        NavItem("use", "Usage", "chart", 1),
        NavItem("set", "Settings", "gear", 4)
    )

    val bottomNav = listOf(
        NavItem("chat", "Studio", "spark", 0),
        NavItem("tpl", "Explore", "grid", 3),
        NavItem("prj", "Projects", "folder", 1)
    )

    val recentChats = listOf("Moss Coffee app", "Paper budget", "Clay studio site")

    val history = listOf(
        HistoryVersion("v3", "Warmer hero", "Just now", 0, 26, false),
        HistoryVersion("v2", "Added brew guide", "1h ago", 1, 18, false),
        HistoryVersion("v1", "First draft", "Yesterday", 2, 34, true)
    )

    val exports = listOf(
        "Figma (editable layers)", "Copy code", "HTML + CSS", "React + Tailwind",
        "Jetpack Compose", "DESIGN.md", "Prototype share link", "Download ZIP", "Push to GitHub"
    )

    val generationSteps = listOf(
        "Understanding", "Planning", "Choosing palette",
        "Designing screens", "Exporting code", "Polishing"
    )

    val generationNotes = listOf(
        "Reading your brief…", "Mapping four screens…", "Picking calm, warm colours…",
        "Placing hero and brew guide…", "Composing Jetpack Compose…", "Tightening the 4px grid…"
    )

    val usageProviders = listOf(Triple("OpenRouter", "$1.62", 57), Triple("Gemini", "$0.74", 26), Triple("Groq", "$0.48", 17))
    val usageProjects = listOf(Triple("Moss Coffee", "$1.30", 46), Triple("Paper Budget", "$0.98", 35), Triple("Clay Studio", "$0.56", 20))

    val composerModes = listOf("Auto", "Plan")
    val deviceModes = listOf("Phone", "Tablet", "Web")
    val editModes = listOf("Edit", "Play")
    val previewTabs = listOf(
        Triple("Preview", "phone", "p-preview"),
        Triple("Canvas", "layers", "p-canvas"),
        Triple("Code", "code", "p-code"),
        Triple("Style", "pal", "p-style"),
        Triple("History", "clock", "p-history")
    )

    /** The three screens rendered inside the device frame. */
    val deviceScreens = listOf("home", "brew", "shop")
}
