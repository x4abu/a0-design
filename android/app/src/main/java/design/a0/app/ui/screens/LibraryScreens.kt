package design.a0.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import design.a0.app.data.UsageEntry
import design.a0.app.ui.comp.A0Button
import design.a0.app.ui.comp.A0Chip
import design.a0.app.ui.comp.A0Field
import design.a0.app.ui.comp.A0Toggle
import design.a0.app.ui.comp.PastelTile
import design.a0.app.ui.comp.SectionLabel
import design.a0.app.ui.comp.TopBar
import design.a0.app.ui.comp.a0Clickable
import design.a0.app.ui.comp.glass
import design.a0.app.ui.icon.A0Icon
import design.a0.app.ui.theme.A0
import design.a0.app.ui.theme.T
import design.a0.app.vm.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ---------------------------------------------------------------------------
// Projects — real, from local storage
// ---------------------------------------------------------------------------
@Composable
fun ProjectsScreen(vm: AppViewModel) {
    Column(Modifier.fillMaxSize()) {
        TopBar(onBack = { vm.go("chat") })
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 22.dp, end = 22.dp, bottom = 120.dp)
        ) {
            Text("Projects", style = T.Title, color = A0.Ink, modifier = Modifier.padding(bottom = 14.dp))

            if (vm.projects.isEmpty()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .glass(RoundedCornerShape(A0.RCard), alpha = 0.92f)
                        .padding(20.dp)
                ) {
                    Text("No designs yet", style = T.Card, color = A0.Ink)
                    Text(
                        "Projects appear here the moment the model returns a design you keep. " +
                            "Nothing is pre-filled.",
                        style = T.Small, color = A0.Mu, modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
                    )
                    A0Button(
                        label = if (vm.hasKey()) "Start a design" else "Add an API key first",
                        onClick = { vm.go(if (vm.hasKey()) "chat" else "key") }
                    )
                }
                return@Column
            }

            vm.projects.forEach { project ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .a0Clickable { vm.openProject(project) }
                        .glass(RoundedCornerShape(20.dp), alpha = 0.9f)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val spec = project.latest?.spec
                    Box(
                        Modifier
                            .size(width = 52.dp, height = 68.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        hexColor(spec?.theme?.bg ?: "#F6EFE4", Color(0xFFF6EFE4)),
                                        hexColor(spec?.theme?.accent ?: "#C0623B", Color(0xFFC0623B))
                                    )
                                )
                            )
                    )
                    Column(Modifier.weight(1f)) {
                        Text(project.name, style = T.Card, color = A0.Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            "${project.versions.size} version${if (project.versions.size == 1) "" else "s"} · " +
                                SimpleDateFormat("d MMM", Locale.getDefault()).format(Date(project.createdAt)),
                            style = T.Caption, color = A0.Mu
                        )
                    }
                    Box(
                        Modifier.size(44.dp).a0Clickable { vm.openSheet("project:${project.id}") },
                        contentAlignment = Alignment.Center
                    ) { A0Icon("dots", 20.dp, A0.Mu, 3.2f) }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Explore — real palette directions, no invented counts
// ---------------------------------------------------------------------------
@Composable
fun ExploreScreen(vm: AppViewModel) {
    Column(Modifier.fillMaxSize()) {
        TopBar(onBack = { vm.go("chat") })
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 22.dp, end = 22.dp, bottom = 120.dp)
        ) {
            Text("Directions", style = T.Title, color = A0.Ink)
            Text(
                "Real palettes you can brief the model with. Tapping one fills the composer — " +
                    "the model still designs everything else.",
                style = T.Small, color = A0.Mu, modifier = Modifier.padding(top = 6.dp, bottom = 14.dp)
            )
            vm.presets.forEach { preset ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .a0Clickable { vm.startFromPreset(preset) }
                        .glass(RoundedCornerShape(20.dp), alpha = 0.9f)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        hexColor(preset.bg, Color.White),
                                        hexColor(preset.accent, A0.Accent)
                                    )
                                )
                            )
                    )
                    Column(Modifier.weight(1f)) {
                        Text(preset.name, style = T.Card, color = A0.Ink)
                        Text(preset.note, style = T.Caption, color = A0.Mu, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    A0Icon("chev", 18.dp, A0.Mu)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Usage — computed from real API responses
// ---------------------------------------------------------------------------
@Composable
fun UsageScreen(vm: AppViewModel) {
    val entries = vm.usageEntries
    val totalTokens = entries.sumOf { it.totalTokens }
    val totalCost = entries.sumOf { it.costUsd }

    Column(Modifier.fillMaxSize()) {
        TopBar(onBack = { vm.go("set") })
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 22.dp, end = 22.dp, bottom = 120.dp)
        ) {
            Text("Usage", style = T.Title, color = A0.Ink)
            Text(
                "Counted from the usage fields your provider returned. Nothing estimated.",
                style = T.Small, color = A0.Mu, modifier = Modifier.padding(top = 6.dp)
            )

            if (entries.isEmpty()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                        .glass(RoundedCornerShape(A0.RCard), alpha = 0.92f)
                        .padding(20.dp)
                ) {
                    Text("No requests yet", style = T.Card, color = A0.Ink)
                    Text(
                        "Generate a design and this screen fills with the token counts your key was billed for.",
                        style = T.Small, color = A0.Mu, modifier = Modifier.padding(top = 6.dp)
                    )
                }
                return@Column
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .background(Color.White, RoundedCornerShape(A0.RCard))
                    .padding(18.dp)
            ) {
                Text("All time", style = T.Small, color = A0.Mu)
                Text(
                    if (totalCost > 0) "$" + String.format("%.2f", totalCost) else "$totalTokens tokens",
                    style = T.Display.copy(fontSize = 44.sp),
                    color = A0.Ink
                )
                Text(
                    "$totalTokens tokens · ${entries.size} request${if (entries.size == 1) "" else "s"}" +
                        if (totalCost > 0) "" else " · cost needs OpenRouter pricing",
                    style = T.Mono.copy(fontSize = 12.sp), color = A0.Mu
                )
            }

            SectionLabel("By model")
            entries.groupBy { it.model }.mapValues { (_, list) -> list.sumOf { it.totalTokens } }
                .entries.sortedByDescending { it.value }
                .forEach { (model, tokens) ->
                    UsageRow(model, "$tokens tokens", (tokens * 100 / totalTokens.coerceAtLeast(1)))
                }

            SectionLabel("Recent")
            entries.sortedByDescending { it.at }.take(8).forEach { entry ->
                UsageEntryRow(entry)
            }
        }
    }
}

@Composable
private fun UsageRow(name: String, value: String, pct: Int) {
    Column(Modifier.padding(bottom = 14.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name.substringAfterLast('/').take(24), style = T.Body, color = A0.Ink)
            Text(value, style = T.Mono, color = A0.Mu)
        }
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(A0.RPill))
                .background(Color(0xFFE4EADF))
        ) {
            Box(
                Modifier
                    .fillMaxWidth(pct / 100f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(A0.RPill))
                    .background(Brush.horizontalGradient(listOf(A0.Accent, A0.Accent2)))
            )
        }
    }
}

@Composable
private fun UsageEntryRow(entry: UsageEntry) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(entry.project.ifBlank { entry.model }, style = T.Small, color = A0.Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                SimpleDateFormat("d MMM, HH:mm", Locale.getDefault()).format(Date(entry.at)),
                style = T.Caption, color = A0.Mu
            )
        }
        Text("${entry.promptTokens}→${entry.completionTokens}", style = T.MonoSmall, color = A0.Mu)
    }
}

// ---------------------------------------------------------------------------
// Settings — every row does something real
// ---------------------------------------------------------------------------
@Composable
fun SettingsScreen(vm: AppViewModel) {
    val uri = LocalUriHandler.current
    Column(Modifier.fillMaxSize()) {
        TopBar(onBack = { vm.go("chat") })
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 22.dp, end = 22.dp, bottom = 120.dp)
        ) {
            Text("Settings", style = T.Title, color = A0.Ink, modifier = Modifier.padding(bottom = 10.dp))

            SettingItem("API keys", "key", 3, "…" + vm.maskedKey().takeLast(4)) { vm.go("key") }
            SettingItem(
                "Default model", "spark", 4,
                vm.settings.model.substringAfterLast('/').take(20)
            ) { vm.openSheet("model") }
            SettingItem("Usage and cost", "chart", 1, "") { vm.go("use") }
            SettingItem(
                "Font size", "type", 0,
                listOf("Small", "Medium", "Large")[vm.settings.fontScaleIndex]
            ) { vm.cycleFontSize() }

            Row(
                Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                PastelTile("phone", 2, size = 44.dp)
                Text("Haptics", style = T.BodyMedium, color = A0.Ink, modifier = Modifier.weight(1f))
                A0Toggle(checked = vm.settings.haptics, onChange = { vm.toggleHaptics() })
            }

            SettingItem("System prompt", "file", 3, "") { vm.openSheet("prompt") }
            SettingItem("Engine", "link", 1, if (vm.settings.engine == "backend") "Backend" else "Direct") {
                vm.openSheet("engine")
            }
            SettingItem("Privacy — clear local data", "lock", 2, "") { vm.openSheet("wipe") }
            SettingItem("Open-source licence", "file", 0, "MIT") { vm.openSheet("licence") }
            SettingItem("GitHub", "git", 4, "") {
                runCatching { uri.openUri("https://github.com/x4abu/a0-design") }
            }
        }
    }
}

@Composable
private fun SettingItem(
    title: String,
    icon: String,
    pastel: Int,
    value: String,
    onClick: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().a0Clickable(onClick = onClick).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        PastelTile(icon, pastel, size = 44.dp)
        Text(title, style = T.BodyMedium, color = A0.Ink, modifier = Modifier.weight(1f))
        if (value.isNotBlank()) Text(value, style = T.Small, color = A0.Mu)
        A0Icon("chev", 18.dp, A0.Mu)
    }
}

// ---------------------------------------------------------------------------
// API keys — real validation against the provider
// ---------------------------------------------------------------------------
@Composable
fun ApiKeysScreen(vm: AppViewModel) {
    var backendUrl by remember { mutableStateOf(vm.settings.backendUrl) }
    Column(Modifier.fillMaxSize()) {
        TopBar(onBack = { vm.go("set") })
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 22.dp, end = 22.dp, bottom = 120.dp)
        ) {
            Text("API keys", style = T.Title, color = A0.Ink)
            Text(
                "a0 ships no model. Paste a key and it is checked against the provider before " +
                    "it is stored — encrypted with a key that never leaves this device.",
                style = T.Small, color = A0.Mu, modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                vm.providers.forEachIndexed { i, provider ->
                    A0Chip(
                        label = if (vm.hasKey(provider)) "$provider ✓" else provider,
                        pastel = i,
                        selected = vm.keyProvider == provider,
                        onClick = { vm.selectProvider(provider) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            A0Field(
                value = vm.keyInput,
                onValueChange = { vm.keyInput = it },
                placeholder = "${vm.keyProvider} key",
                mono = true,
                password = true,
                onImeAction = { vm.saveKey() }
            )
            Spacer(Modifier.height(12.dp))
            A0Button(
                label = when (vm.keyStatus.state) {
                    "checking" -> "Checking…"
                    "ok" -> "Verified"
                    else -> "Save and verify"
                },
                enabled = vm.keyStatus.state != "checking",
                onClick = { vm.saveKey() }
            )

            if (vm.keyStatus.message.isNotBlank()) {
                Text(
                    vm.keyStatus.message,
                    style = T.Small,
                    color = if (vm.keyStatus.state == "error") Color(0xFF8A3A28) else A0.AccentDeep,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            if (vm.hasKey(vm.keyProvider)) {
                Spacer(Modifier.height(10.dp))
                A0Button(
                    label = "Remove ${vm.keyProvider} key",
                    primary = false,
                    onClick = { vm.removeKey(vm.keyProvider) }
                )
            }

            SectionLabel("Optional server engine")
            Text(
                "Run the FastAPI backend in /backend to assemble the full Design Brain server-side. " +
                    "Leave empty to call the provider directly from this device.",
                style = T.Caption, color = A0.Mu, modifier = Modifier.padding(bottom = 10.dp)
            )
            A0Field(
                value = backendUrl,
                onValueChange = { backendUrl = it },
                placeholder = "https://your-backend.example.com",
                mono = true,
                height = 52.dp
            )
            Spacer(Modifier.height(10.dp))
            A0Button(
                label = "Use backend",
                primary = false,
                onClick = {
                    vm.setBackendUrl(backendUrl)
                    vm.toast(if (backendUrl.isBlank()) "Direct mode" else "Backend mode")
                }
            )
        }
    }
}
