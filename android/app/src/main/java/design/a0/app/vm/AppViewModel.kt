package design.a0.app.vm

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import design.a0.app.data.AppStore
import design.a0.app.data.DesignEngine
import design.a0.app.data.Project
import design.a0.app.data.Settings
import design.a0.app.data.Spec
import design.a0.app.data.SpecJson
import design.a0.app.data.SpecTheme
import design.a0.app.data.UsageEntry
import design.a0.app.data.Version
import design.a0.app.net.DesignApi
import design.a0.app.net.LlmClient
import design.a0.app.ui.mascot.Mood
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class FeedKind { User, Agent, Steps, Error, Ready, Audit }

data class FeedItem(
    val id: Long,
    val kind: FeedKind,
    val text: String = "",
    val step: Int = 0,
    val spec: Spec? = null,
    val findings: List<DesignEngine.Finding> = emptyList(),
    val action: String = ""
)

data class KeyStatus(val state: String, val message: String = "")   // idle | checking | ok | error

/**
 * Single state holder. Every list here starts empty and only ever holds things that
 * actually happened on this device: real projects, real versions, real token usage,
 * real errors. Nothing is seeded.
 */
class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val store = AppStore(app)

    // ---- routing --------------------------------------------------------
    var screen by mutableStateOf("chat")
    var drawer by mutableStateOf(false)
    var sheet by mutableStateOf<String?>(null)
    var toast by mutableStateOf<String?>(null)
    private var toastJob: Job? = null

    // ---- settings -------------------------------------------------------
    var settings by mutableStateOf(store.settings)
        private set

    val providers = listOf("Google Gemini", "Groq", "OpenRouter", "OpenAI", "Anthropic", "DeepSeek")

    // ---- keys -----------------------------------------------------------
    var keyProvider by mutableStateOf(store.settings.provider)
    var keyInput by mutableStateOf("")
    var keyStatus by mutableStateOf(KeyStatus("idle"))

    fun hasKey(provider: String = settings.provider): Boolean =
        store.keys.get(provider)?.isNotBlank() == true

    fun maskedKey(provider: String = settings.provider): String {
        val k = store.keys.get(provider) ?: return ""
        return if (k.length <= 8) "••••" else k.take(4) + "••••" + k.takeLast(4)
    }

    fun selectProvider(provider: String) {
        keyProvider = provider
        keyInput = ""
        keyStatus = KeyStatus("idle")
    }

    fun saveKey() {
        val key = keyInput.trim()
        if (key.length < 16) {
            keyStatus = KeyStatus("error", "That key looks too short — paste the whole key.")
            return
        }
        keyStatus = KeyStatus("checking", "Checking with ${keyProvider}…")
        viewModelScope.launch {
            when (val res = LlmClient.checkKey(keyProvider, key)) {
                is LlmClient.Result.Ok -> {
                    store.keys.put(keyProvider, key)
                    keyStatus = KeyStatus("ok", "Key verified with $keyProvider and stored on this device.")
                    keyInput = ""
                    updateSettings(settings.copy(provider = keyProvider))
                    loadModels()
                }
                is LlmClient.Result.Err -> keyStatus = KeyStatus("error", "${res.status}: ${res.message}")
            }
        }
    }

    fun removeKey(provider: String) {
        store.keys.remove(provider)
        keyStatus = KeyStatus("idle")
        toast("$provider key removed")
    }

    // ---- models ---------------------------------------------------------
    var models by mutableStateOf<List<LlmClient.Model>>(emptyList())
        private set
    var modelsState by mutableStateOf("idle")     // idle | loading | ready | error
        private set
    var modelsError by mutableStateOf("")
        private set

    fun loadModels() {
        if (modelsState == "loading") return
        modelsState = "loading"
        modelsError = ""
        viewModelScope.launch {
            val list = LlmClient.openRouterModels()
            if (list.isEmpty()) {
                modelsState = "error"
                modelsError = "Could not load the model catalogue. Check your connection."
            } else {
                models = list.sortedBy { it.id }
                modelsState = "ready"
                if (models.none { it.id == settings.model }) {
                    // keep whatever the user had, but make sure the picker is usable
                    updateSettings(settings.copy(model = list.first().id))
                }
            }
        }
    }

    fun setModel(id: String) {
        updateSettings(settings.copy(model = id))
        closeAll()
        toast(id)
    }

    // ---- chat -----------------------------------------------------------
    val feed = mutableStateListOf<FeedItem>()
    var prompt by mutableStateOf("")
    var mode by mutableStateOf(0)                  // 0 Auto · 1 Plan
    var mood by mutableStateOf(Mood.Idle)
    var busy by mutableStateOf(false)
        private set
    var planStep by mutableStateOf(-1)             // -1 = no plan running
    val planAnswers = mutableStateListOf<String>()
    private var idSeed = 0L
    private var generationJob: Job? = null

    private fun nextId() = ++idSeed

    fun onPromptChange(value: String) {
        prompt = value
        mood = when {
            value.isBlank() -> Mood.Idle
            value.trimEnd().endsWith("?") -> Mood.Curious
            else -> Mood.Typing
        }
    }

    fun send(text: String = prompt) {
        val brief = text.trim()
        if (brief.isEmpty()) {
            mood = Mood.Curious
            toast("Describe what you want to design first")
            return
        }
        if (!hasKey()) {
            emptyStateNeedsKey = true
            toast("No API key. Add one first.")
            go("key")
            return
        }
        prompt = ""
        feed.add(FeedItem(nextId(), FeedKind.User, brief))
        if (mode == 1 && planStep < 0) {
            planAnswers.clear()
            planStep = 0
            mood = Mood.Curious
            return
        }
        generate(brief, planAnswers.toList(), null)
    }

    var emptyStateNeedsKey by mutableStateOf(false)

    fun answerPlan(index: Int, answer: String) {
        if (answer.isNotBlank()) planAnswers.add(answer)
        if (index >= 1) {
            planStep = -1
            val brief = feed.lastOrNull { it.kind == FeedKind.User }?.text ?: prompt
            generate(brief, planAnswers.toList(), null)
        } else {
            planStep = index + 1
        }
        mood = Mood.Happy
    }

    fun skipPlan() {
        planStep = -1
        val brief = feed.lastOrNull { it.kind == FeedKind.User }?.text ?: prompt
        generate(brief, planAnswers.toList(), null)
    }

    fun generate(brief: String, answers: List<String> = emptyList(), previous: Spec? = null, repair: List<DesignEngine.Finding> = emptyList()) {
        if (!hasKey()) {
            go("key")
            toast("No API key. Add one first.")
            return
        }
        generationJob?.cancel()
        busy = true
        mood = Mood.Think
        val itemId = nextId()
        feed.add(FeedItem(itemId, FeedKind.Steps, step = 0))
        generationJob = viewModelScope.launch {
            val provider = settings.provider
            val key = store.keys.get(provider) ?: run {
                busy = false
                replace(itemId, FeedItem(itemId, FeedKind.Error, "No key stored for $provider.", action = "Add key"))
                return@launch
            }

            replace(itemId, FeedItem(itemId, FeedKind.Steps, step = 1))
            val prompt = DesignEngine.userPrompt(brief, if (mode == 1) "Plan" else "Auto", answers, previous, repair)
            val result = if (settings.engine == "backend" && settings.backendUrl.isNotBlank()) {
                DesignApi.generate(settings.backendUrl, prompt, settings.model, key)
            } else {
                LlmClient.complete(provider, key, settings.model, DesignEngine.SYSTEM_PROMPT, prompt)
            }

            when (result) {
                is LlmClient.Result.Err -> {
                    busy = false
                    mood = Mood.Sad
                    replace(
                        itemId,
                        FeedItem(
                            itemId, FeedKind.Error,
                            "${result.status} ${result.message}".trim(),
                            action = if (result.status == 401 || result.status == 403) "Fix key" else "Retry"
                        )
                    )
                }
                is LlmClient.Result.Ok -> {
                    replace(itemId, FeedItem(itemId, FeedKind.Steps, step = 2))
                    val parsed = SpecJson.parse(result.text)
                    if (parsed.isFailure) {
                        busy = false
                        mood = Mood.Sad
                        replace(
                            itemId,
                            FeedItem(
                                itemId, FeedKind.Error,
                                parsed.exceptionOrNull()?.message ?: "The model returned something unreadable.",
                                action = "Retry"
                            )
                        )
                        return@launch
                    }
                    val spec = parsed.getOrThrow()
                    val findings = DesignEngine.audit(spec)
                    val failed = findings.filterNot { it.ok }

                    // one automatic repair round, then we show the real result either way
                    if (failed.isNotEmpty() && repair.isEmpty()) {
                        busy = false
                        feed.removeAt(feed.indexOfFirst { it.id == itemId })
                        generate(brief, answers, spec, failed)
                        return@launch
                    }

                    replace(itemId, FeedItem(itemId, FeedKind.Steps, step = 3))
                    val label = if (previous == null) "v1" else "v" + ((currentProject?.versions?.size ?: 0) + 1)
                    val project = store.addVersion(currentProject?.id, spec, label)
                    currentProject = project
                    currentVersion = project.latest
                    projects = store.projects()
                    recordUsage(result.usage, provider, settings.model, spec.name)
                    busy = false
                    mood = Mood.Happy
                    feed.removeAll { it.id == itemId }
                    feed.add(FeedItem(nextId(), FeedKind.Ready, spec = spec))
                    if (findings.any { !it.ok }) {
                        feed.add(FeedItem(nextId(), FeedKind.Audit, findings = findings))
                    }
                }
            }
        }
    }

    fun stopGeneration() {
        generationJob?.cancel()
        busy = false
        mood = Mood.Idle
        val last = feed.lastOrNull { it.kind == FeedKind.Steps } ?: return
        replace(last.id, FeedItem(last.id, FeedKind.Error, "Stopped. No request is in flight."))
    }

    fun retry() {
        val brief = feed.lastOrNull { it.kind == FeedKind.User }?.text ?: return
        generate(brief)
    }

    fun newChat() {
        generationJob?.cancel()
        feed.clear()
        busy = false
        planStep = -1
        prompt = ""
        mood = Mood.Idle
        currentProject = null
        currentVersion = null
        go("chat")
    }

    private fun replace(id: Long, item: FeedItem) {
        val i = feed.indexOfFirst { it.id == id }
        if (i >= 0) feed[i] = item
    }

    private fun recordUsage(usage: LlmClient.Usage, provider: String, model: String, project: String) {
        val price = models.firstOrNull { it.id == model }
        val cost = if (price != null && (price.promptPrice > 0 || price.completionPrice > 0)) {
            usage.promptTokens * price.promptPrice + usage.completionTokens * price.completionPrice
        } else 0.0
        store.addUsage(
            UsageEntry(
                at = System.currentTimeMillis(),
                provider = provider,
                model = model,
                promptTokens = usage.promptTokens,
                completionTokens = usage.completionTokens,
                costUsd = cost,
                project = project
            )
        )
        usageEntries = store.usage()
    }

    // ---- projects -------------------------------------------------------
    var projects by mutableStateOf(store.projects())
        private set
    var currentProject by mutableStateOf<Project?>(null)
    var currentVersion by mutableStateOf<Version?>(null)

    fun openProject(project: Project) {
        currentProject = project
        currentVersion = project.latest
        screenIndex = 0
        go("preview")
    }

    fun renameProject(project: Project, name: String) {
        store.renameProject(project.id, name)
        projects = store.projects()
        if (currentProject?.id == project.id) currentProject = store.project(project.id)
        toast("Renamed")
    }

    fun deleteProject(project: Project) {
        store.deleteProject(project.id)
        projects = store.projects()
        if (currentProject?.id == project.id) {
            currentProject = null
            currentVersion = null
        }
        closeAll()
        toast("Deleted ${project.name}")
    }

    // ---- preview --------------------------------------------------------
    var previewTab by mutableStateOf(0)     // Design · Screens · Code · Style · History
    var screenIndex by mutableStateOf(0)
    var codeLang by mutableStateOf(0)       // Compose · Markdown
    var selectedBlock by mutableStateOf<Int?>(null)

    val spec: Spec? get() = currentVersion?.spec

    fun updateTheme(theme: SpecTheme) {
        val version = currentVersion ?: return
        val updated = version.copy(spec = version.spec.copy(theme = theme))
        currentVersion = updated
    }

    /** Style edits are real edits: they create a new version so History stays truthful. */
    fun commitStyleChange(label: String) {
        val version = currentVersion ?: return
        val project = currentProject ?: return
        val next = store.addVersion(project.id, version.spec, label)
        currentProject = next
        currentVersion = next.latest
        projects = store.projects()
        toast("Saved as ${next.latest?.label}")
    }

    fun restoreVersion(version: Version) {
        currentVersion = version
        previewTab = 0
        toast("Restored ${version.label}")
    }

    // ---- usage ----------------------------------------------------------
    var usageEntries by mutableStateOf(store.usage())
        private set

    // ---- explore presets (real palettes, no invented counts) -------------
    data class Preset(val name: String, val note: String, val accent: String, val bg: String, val ink: String, val font: String)

    val presets = listOf(
        Preset("Pastel Soft", "Warm off-white, lime accent, serif display", "#7FCF2E", "#F8FBF4", "#14171A", "serif"),
        Preset("Warm Minimal", "Editorial cream, terracotta, quiet type", "#C0623B", "#F6EFE4", "#2B2118", "serif"),
        Preset("Quiet Luxury", "Low saturation, hairline rules, generous air", "#8A7B5C", "#F4F2ED", "#1B1A17", "serif"),
        Preset("Soft Brutalist", "Hard edges, heavy contrast, one warm accent", "#D6A24A", "#EFEFEA", "#111111", "mono"),
        Preset("Warm Dark", "Plum-charcoal canvas, one lifted accent", "#C6F36A", "#1E1916", "#F4ECE3", "sans")
    )

    fun startFromPreset(preset: Preset) {
        go("chat")
        prompt = "Design an app in the ${preset.name} direction — ${preset.note.lowercase()}."
    }

    // ---- settings & misc ------------------------------------------------
    private fun updateSettings(next: Settings) {
        settings = next
        store.settings = next
    }

    fun setSystemPrompt(text: String) = updateSettings(settings.copy(systemPrompt = text))

    fun setBackendUrl(url: String) = updateSettings(settings.copy(backendUrl = url.trim(), engine = if (url.isBlank()) "direct" else "backend"))

    fun cycleFontSize() {
        updateSettings(settings.copy(fontScaleIndex = (settings.fontScaleIndex + 1) % 3))
        toast("Font size: " + listOf("Small", "Medium", "Large")[settings.fontScaleIndex])
    }

    fun fontScale(): Float = when (settings.fontScaleIndex) {
        0 -> 0.92f
        2 -> 1.12f
        else -> 1f
    }

    fun toggleHaptics() = updateSettings(settings.copy(haptics = !settings.haptics))

    fun clearEverything() {
        store.clearEverything()
        projects = emptyList()
        usageEntries = emptyList()
        currentProject = null
        currentVersion = null
        settings = store.settings
        feed.clear()
        newChat()
        toast("All local data cleared")
    }

    // ---- chrome ---------------------------------------------------------
    fun go(target: String) {
        screen = target
        closeAll()
    }

    fun openDrawer() { drawer = true }
    fun openSheet(id: String) { sheet = id }
    fun closeAll() { drawer = false; sheet = null }

    fun toast(message: String) {
        toast = message
        toastJob?.cancel()
        toastJob = viewModelScope.launch {
            delay(2400)
            toast = null
        }
    }
}
