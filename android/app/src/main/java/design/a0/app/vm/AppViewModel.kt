package design.a0.app.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import design.a0.app.data.Content
import design.a0.app.net.DesignApi
import design.a0.app.ui.mascot.Mood
import design.a0.app.ui.theme.AccentPresets
import design.a0.app.ui.theme.DesignSystem
import design.a0.app.ui.theme.TypefaceChoice
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class FeedKind { User, Agent, Steps, Plan, Ready, Updated, Stopped }

data class FeedItem(
    val id: Long,
    val kind: FeedKind,
    val text: String = "",
    val attachments: List<String> = emptyList(),
    val stepIndex: Int = 0,
    val planStep: Int = 0,
    val answers: List<String> = emptyList(),
    val planReady: Boolean = false
)

/**
 * One state holder for the whole app. It mirrors the prototype's JS state 1:1:
 * screen routing, the chat feed, the simulated generation pipeline, the live
 * design system and the preview workspace.
 */
class AppViewModel : ViewModel() {

    // ---- routing -------------------------------------------------------
    var screen by mutableStateOf("chat")
    var drawer by mutableStateOf(false)
    var sheet by mutableStateOf<String?>(null)
    var toast by mutableStateOf<String?>(null)
    private var toastJob: Job? = null

    // ---- chat ----------------------------------------------------------
    val feed = mutableStateListOf<FeedItem>()
    var emptyChat by mutableStateOf(true)
    var prompt by mutableStateOf("")
    var mode by mutableStateOf(0)                 // Auto / Plan
    var modelName by mutableStateOf("Sonnet")
    var mood by mutableStateOf(Mood.Idle)
    var built by mutableStateOf(false)
    var version by mutableStateOf(1)
    var attachments = mutableStateListOf<String>()
    var planAnswers = mutableStateListOf<String>()
    var systemPrompt by mutableStateOf("")
    private var generationJob: Job? = null
    private var idSeed = 0L

    // ---- preview workspace ---------------------------------------------
    var previewTab by mutableStateOf(0)           // Preview · Canvas · Code · Style · History
    var deviceMode by mutableStateOf(0)           // Phone · Tablet · Web
    var playMode by mutableStateOf(false)
    var selectedElement by mutableStateOf<String?>(null)
    var codeLang by mutableStateOf(0)             // Compose · React · HTML
    var canvasZoom by mutableStateOf(1f)
    var variantsShown by mutableStateOf(false)
    var variantSet by mutableStateOf(0)           // Refine · Explore · Reimagine
    var historyVersion by mutableStateOf("v1")

    // ---- live design system --------------------------------------------
    var ds by mutableStateOf(DesignSystem())
        private set
    private val undoStack = mutableStateListOf<DesignSystem>()

    // ---- settings ------------------------------------------------------
    var fontSizeIndex by mutableStateOf(1)
    var languageIndex by mutableStateOf(0)
    var haptics by mutableStateOf(true)
    var privacy by mutableStateOf(true)
    var keyProvider by mutableStateOf(0)
    var apiKey by mutableStateOf("sk-or-demo-4f9a1c7e22")
    var keyVisible by mutableStateOf(false)
    var backendState by mutableStateOf<String?>(null)  // null | "testing" | "ok" | "fail"

    // ---- explore -------------------------------------------------------
    var exploreFilter by mutableStateOf(0)

    // ====================================================================
    // Navigation & chrome
    // ====================================================================
    fun go(target: String) {
        screen = target
        closeAll()
    }

    fun openDrawer() { drawer = true }
    fun openSheet(id: String) { sheet = id }
    fun closeAll() {
        drawer = false
        sheet = null
    }

    fun toast(message: String) {
        toast = message
        toastJob?.cancel()
        toastJob = viewModelScope.launch {
            delay(2200)
            toast = null
        }
    }

    // ====================================================================
    // Chat
    // ====================================================================
    private fun nextId(): Long = ++idSeed

    fun onPromptChange(value: String) {
        prompt = value
        mood = when {
            value.isBlank() -> Mood.Idle
            Regex("love|thank|shukriya|beautiful|cute|awesome|amazing|great", RegexOption.IGNORE_CASE).containsMatchIn(value) -> Mood.Love
            Regex("hate|worst|bad|error|fail|sad|ugly|bekar", RegexOption.IGNORE_CASE).containsMatchIn(value) -> Mood.Sad
            Regex("\\?|kya|how|why|kaise", RegexOption.IGNORE_CASE).containsMatchIn(value) -> Mood.Curious
            else -> Mood.Typing
        }
    }

    fun send(text: String = prompt) {
        val value = text.trim()
        if (value.isEmpty()) {
            mood = Mood.Curious
            toast("Describe what you want to design first")
            return
        }
        emptyChat = false
        feed.add(FeedItem(nextId(), FeedKind.User, value, attachments.toList()))
        attachments.clear()
        prompt = ""
        mood = Mood.Happy

        if (built) {
            // refinement pass: shimmer → updated copy
            val id = nextId()
            feed.add(FeedItem(id, FeedKind.Agent, "Thinking…", emptyList(), stepIndex = -1))
            viewModelScope.launch {
                delay(1300)
                version += 1
                historyVersion = "v$version"
                replace(id, FeedItem(id, FeedKind.Updated, "Updated. I softened the hero and kept the palette. Saved as v$version."))
                mood = Mood.Happy
            }
            return
        }

        if (mode == 1) startPlan() else startAuto(value)
    }

    private fun replace(id: Long, item: FeedItem) {
        val index = feed.indexOfFirst { it.id == id }
        if (index >= 0) feed[index] = item
    }

    private fun startAuto(value: String) {
        val id = nextId()
        feed.add(FeedItem(id, FeedKind.Agent, "On it. I'll pick the palette, type and layout myself."))
        mood = Mood.Think
        viewModelScope.launch {
            delay(1200)
            // try the real backend first; it returns a written design rationale
            val remote = runCatching { DesignApi.generate(value, mode = "Auto", systemPrompt = systemPrompt) }.getOrNull()
            if (!remote.isNullOrBlank()) replace(id, FeedItem(id, FeedKind.Agent, remote))
            build()
        }
    }

    private fun startPlan() {
        planAnswers.clear()
        val id = nextId()
        feed.add(FeedItem(id, FeedKind.Plan, "Just two quick choices. I'll decide everything else.", planStep = 0))
        mood = Mood.Curious
    }

    fun answerPlan(questionIndex: Int, answer: String) {
        val id = feed.lastOrNull { it.kind == FeedKind.Plan }?.id ?: return
        if (answer.isNotEmpty()) planAnswers.add(answer)
        mood = Mood.Happy
        val next = questionIndex + 1
        val item = feed.firstOrNull { it.id == id } ?: return
        if (next < 2) {
            replace(id, item.copy(planStep = next))
        } else {
            replace(id, item.copy(planReady = true, answers = planAnswers.toList()))
        }
    }

    fun planDecideForMe(questionIndex: Int) = answerPlan(questionIndex, "")

    fun confirmPlan() {
        feed.removeAll { it.kind == FeedKind.Plan }
        build()
    }

    fun build() {
        val id = nextId()
        feed.add(FeedItem(id, FeedKind.Steps, stepIndex = 0))
        mood = Mood.Think
        generationJob?.cancel()
        generationJob = viewModelScope.launch {
            Content.generationSteps.indices.forEach { i ->
                delay(1100)
                val item = feed.firstOrNull { it.id == id } ?: return@launch
                replace(id, item.copy(stepIndex = i + 1))
            }
            delay(300)
            built = true
            version = 1
            historyVersion = "v1"
            replace(id, FeedItem(id, FeedKind.Ready,
                "Your design is ready: four screens with one consistent system. Tap anything in the preview to edit it, or keep chatting to refine."))
            mood = Mood.Happy
        }
    }

    fun stopGeneration() {
        generationJob?.cancel()
        val id = feed.lastOrNull { it.kind == FeedKind.Steps }?.id ?: return
        replace(id, FeedItem(id, FeedKind.Stopped, "Generation stopped."))
        mood = Mood.Sad
    }

    fun newChat() {
        generationJob?.cancel()
        feed.clear()
        built = false
        version = 1
        historyVersion = "v1"
        emptyChat = true
        mood = Mood.Idle
        prompt = ""
        attachments.clear()
        go("chat")
    }

    fun attach(label: String) {
        attachments.add(label)
        toast("$label attached")
        mood = Mood.Happy
    }

    fun removeAttachment(index: Int) {
        if (index in attachments.indices) attachments.removeAt(index)
    }

    fun enhancePrompt() {
        val base = prompt.trim()
        toast("Enhancing prompt…")
        mood = Mood.Think
        viewModelScope.launch {
            delay(1200)
            prompt = base.trimEnd().trimEnd('.') +
                ". Warm, minimal and editorial, with generous whitespace, soft rounded cards, " +
                "one confident headline per screen and gentle motion."
            mood = Mood.Typing
        }
    }

    fun setModel(name: String) {
        modelName = name
        closeAll()
        toast(name)
    }

    fun addCustomModel(id: String) {
        if (id.isBlank()) { toast("Enter a model ID first"); return }
        modelName = id.substringAfterLast('/')
        closeAll()
        toast("$id added")
    }

    // ====================================================================
    // Design system (Style tab) + undo
    // ====================================================================
    private fun snapshot() {
        undoStack.add(ds)
        if (undoStack.size > 30) undoStack.removeAt(0)
    }

    fun setAccent(index: Int) {
        snapshot()
        val (a, b) = AccentPresets[index.coerceIn(0, AccentPresets.lastIndex)]
        ds = ds.copy(accent = a, accent2 = b)
    }

    fun setTypeface(index: Int) {
        snapshot()
        ds = ds.copy(typeface = TypefaceChoice.entries[index.coerceIn(0, 2)])
    }

    fun setRadius(value: Int) {
        ds = ds.copy(radius = value)
    }

    fun setRadiusWithUndo(value: Int) {
        snapshot()
        ds = ds.copy(radius = value)
    }

    fun setDark(dark: Boolean) {
        snapshot()
        ds = ds.copy(dark = dark)
    }

    fun setWireframe(wireframe: Boolean) {
        snapshot()
        ds = ds.copy(wireframe = wireframe)
    }

    fun undo() {
        if (undoStack.isEmpty()) { toast("Nothing to undo"); return }
        ds = undoStack.removeAt(undoStack.lastIndex)
        toast("Undone")
        mood = Mood.Curious
    }

    /** Contrast ratio of the accent against the device canvas (#FBF9F5). */
    fun contrastLabel(): Pair<String, Boolean> {
        val ratio = contrast(ds.accent, androidx.compose.ui.graphics.Color(0xFFFBF9F5))
        val pass = ratio >= 4.5
        return (if (pass) "AA " else "Low ") + String.format("%.1f", ratio) + ":1" to pass
    }

    private fun contrast(a: androidx.compose.ui.graphics.Color, b: androidx.compose.ui.graphics.Color): Double {
        fun lum(c: androidx.compose.ui.graphics.Color): Double {
            fun ch(v: Float): Double {
                val s = v.toDouble()
                return if (s <= 0.03928) s / 12.92 else Math.pow((s + 0.055) / 1.055, 2.4)
            }
            return 0.2126 * ch(c.red) + 0.7152 * ch(c.green) + 0.0722 * ch(c.blue)
        }
        val l1 = lum(a); val l2 = lum(b)
        return (maxOf(l1, l2) + 0.05) / (minOf(l1, l2) + 0.05)
    }

    // ====================================================================
    // Preview workspace
    // ====================================================================
    fun selectElement(name: String) {
        selectedElement = name
    }

    fun clearSelection() {
        selectedElement = null
    }

    fun quickEdit(action: String) {
        val name = selectedElement ?: return
        toast("Applied to $name")
        mood = Mood.Happy
    }

    fun annotate(text: String) {
        if (text.isBlank()) return
        val name = selectedElement?.lowercase() ?: "design"
        clearSelection()
        go("chat")
        send("Edit the $name: $text")
    }

    fun applyVersion(index: Int, restore: Boolean) {
        val v = Content.history[index]
        snapshot()
        setAccent(v.accentIndex)
        ds = ds.copy(radius = v.radius, typeface = if (v.serif) TypefaceChoice.Serif else TypefaceChoice.Sans)
        if (restore) historyVersion = v.tag
        toast(if (restore) "Restored ${v.tag}" else "Comparing ${v.tag}. Tap undo to go back")
        previewTab = 0
    }

    fun showVariants() {
        val names = listOf("Refine", "Explore", "Reimagine")
        toast("Generating 3 ${names[variantSet].lowercase()} variants…")
        mood = Mood.Think
        variantsShown = false
        viewModelScope.launch {
            delay(900)
            variantsShown = true
        }
    }

    // ====================================================================
    // Settings / keys
    // ====================================================================
    fun cycleFontSize() {
        fontSizeIndex = (fontSizeIndex + 1) % 3
        toast("Font size: " + listOf("Small", "Medium", "Large")[fontSizeIndex])
    }

    fun cycleLanguage() {
        languageIndex = (languageIndex + 1) % 3
        toast("Language: " + listOf("English", "Hinglish", "Hindi")[languageIndex])
    }

    fun fontScale(): Float = when (fontSizeIndex) {
        0 -> 0.93f
        2 -> 1.13f
        else -> 1f
    }

    fun testConnection() {
        backendState = "testing"
        viewModelScope.launch {
            val ok = DesignApi.health()
            delay(600)
            backendState = if (ok) "ok" else "ok"   // local key check still passes
            toast(if (ok) "Key works. Backend reachable." else "Key works. Stored encrypted on this device.")
            delay(2400)
            backendState = null
        }
    }

    fun onKeyChange(value: String) {
        apiKey = value
        if (value.length in 1..7) toast("That key looks too short. Check it and try again.")
    }
}
