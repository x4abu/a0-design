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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import design.a0.app.data.Content
import design.a0.app.ui.comp.A0Button
import design.a0.app.ui.comp.A0Chip
import design.a0.app.ui.comp.A0Field
import design.a0.app.ui.comp.A0Toggle
import design.a0.app.ui.comp.PastelTile
import design.a0.app.ui.comp.SectionLabel
import design.a0.app.ui.comp.TopBar
import design.a0.app.ui.comp.a0Clickable
import design.a0.app.ui.comp.glass
import design.a0.app.ui.comp.pressScale
import design.a0.app.ui.icon.A0Icon
import design.a0.app.ui.theme.A0
import design.a0.app.ui.theme.T
import design.a0.app.vm.AppViewModel

// ---------------------------------------------------------------------------
// Projects
// ---------------------------------------------------------------------------
@Composable
fun ProjectsScreen(vm: AppViewModel) {
    Column(Modifier.fillMaxSize()) {
        TopBar(onBack = { vm.go("chat") })
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 22.dp, end = 22.dp, bottom = 40.dp)
        ) {
            Text("Projects", style = T.Title, color = A0.Ink, modifier = Modifier.padding(bottom = 14.dp))

            // stat cards, horizontally scrollable like the prototype
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    eyebrow = "YOUR DESIGN ACTIVITY",
                    value = "42",
                    sub = "SCREENS DESIGNED",
                    footer = "12 projects · 2026",
                    gradient = listOf(Color(0xFFD4F78E), Color(0xFF9EDB4C)),
                    ink = Color(0xFF14210A)
                )
                StatCard(
                    eyebrow = "YOUR TOP STYLE",
                    value = "Warm Minimal",
                    sub = "MOST USED",
                    footer = "18 designs",
                    gradient = listOf(Color(0xFFEAE4FA), Color(0xFFCDC3F0)),
                    ink = Color(0xFF2A2140)
                )
            }

            Spacer(Modifier.height(20.dp))
            Row(
                Modifier.fillMaxWidth().a0Clickable { vm.openSheet("drafts") }.padding(vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                A0Icon("folder", 22.dp, A0.Ink)
                Text("Drafts", style = T.Card.copy(fontSize = 18.sp), color = A0.Ink, modifier = Modifier.weight(1f))
                A0Icon("chev", 18.dp, A0.Mu)
            }
            Row(
                Modifier.fillMaxWidth().a0Clickable { vm.openSheet("exports") }.padding(vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                A0Icon("share", 22.dp, A0.Ink)
                Text("Exports", style = T.Card.copy(fontSize = 18.sp), color = A0.Ink, modifier = Modifier.weight(1f))
                A0Icon("chev", 18.dp, A0.Mu)
            }

            Text("Collections", style = T.H2, color = A0.Ink, modifier = Modifier.padding(top = 18.dp, bottom = 12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                CollectionTile("plus", "New design", "Start from a prompt", Modifier.weight(1f)) { vm.go("chat") }
                CollectionTile("file", "Import Figma", "Keep your system", Modifier.weight(1f)) {
                    vm.go("chat"); vm.attach("Figma file")
                }
            }

            Text("Recent designs", style = T.H2, color = A0.Ink, modifier = Modifier.padding(top = 26.dp, bottom = 8.dp))
            Content.projects.forEachIndexed { i, project ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .a0Clickable { vm.go("prev") },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(A0.pastel(i).start, A0.pastel(i).end)
                                )
                            )
                    )
                    Column(Modifier.weight(1f)) {
                        Text(project.name, style = T.Card.copy(fontSize = 17.sp), color = A0.Ink)
                        Text("${project.version} · edited today", style = T.Small, color = A0.Mu)
                    }
                    Box(
                        Modifier.size(44.dp).a0Clickable { vm.openSheet("action") },
                        contentAlignment = Alignment.Center
                    ) { A0Icon("dots", 20.dp, A0.Mu, 3.2f) }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    eyebrow: String, value: String, sub: String, footer: String,
    gradient: List<Color>, ink: Color
) {
    Column(
        Modifier
            .width(300.dp)
            .height(210.dp)
            .clip(RoundedCornerShape(A0.RCard))
            .background(Brush.linearGradient(gradient))
            .padding(22.dp)
    ) {
        Text(eyebrow, style = T.Eyebrow, color = ink.copy(alpha = 0.6f))
        Spacer(Modifier.weight(1f))
        Text(value, style = T.Display.copy(fontSize = if (value.length > 6) 34.sp else 44.sp), color = ink)
        Text(sub, style = T.Eyebrow, color = ink.copy(alpha = 0.6f), modifier = Modifier.padding(top = 2.dp))
        Spacer(Modifier.height(18.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("A0 CREATOR", style = T.Mono.copy(fontSize = 13.sp, letterSpacing = 1.6.sp), color = ink)
            Text(footer, style = T.Caption, color = ink.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun CollectionTile(icon: String, title: String, sub: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(modifier.a0Clickable(onClick = onClick)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color.White)
                .border(1.dp, A0.HairlineSoft, RoundedCornerShape(26.dp)),
            contentAlignment = Alignment.Center
        ) { A0Icon(icon, 38.dp, A0.Ink, 1.4f) }
        Text(title, style = T.Card.copy(fontSize = 17.sp), color = A0.Ink, modifier = Modifier.padding(top = 10.dp))
        Text(sub, style = T.Small, color = A0.Mu)
    }
}

// ---------------------------------------------------------------------------
// Explore
// ---------------------------------------------------------------------------
@Composable
fun ExploreScreen(vm: AppViewModel) {
    Column(Modifier.fillMaxSize()) {
        TopBar(onBack = { vm.go("chat") })
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 22.dp, end = 22.dp, bottom = 40.dp)
        ) {
            Text("Explore", style = T.Title, color = A0.Ink, modifier = Modifier.padding(bottom = 14.dp))
            Row(
                Modifier.horizontalScroll(rememberScrollState()).padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Content.filters.forEachIndexed { i, label ->
                    A0Chip(
                        label = label,
                        pastel = if (i == 0) null else i - 1,
                        selected = vm.exploreFilter == i,
                        onClick = { vm.exploreFilter = i }
                    )
                }
            }

            val filter = Content.filters[vm.exploreFilter]
            val styles = Content.styles.filter { filter == "All" || it.category == filter }
            val designs = Content.newDesigns.filter { filter == "All" || it.category == filter }

            if (styles.isNotEmpty()) {
                Text("Trending styles", style = T.H2, color = A0.Ink, modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
                styles.forEach { style ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .a0Clickable { vm.go("chat"); vm.send("A ${style.name.lowercase()} style design") },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.linearGradient(listOf(A0.pastel(style.pastel).start, A0.pastel(style.pastel).end)))
                        )
                        Column(Modifier.weight(1f)) {
                            Text(style.name, style = T.Card.copy(fontSize = 17.sp), color = A0.Ink)
                            Text(style.subtitle, style = T.Small, color = A0.Mu)
                        }
                        A0Icon("dots", 20.dp, A0.Mu, 3.2f)
                    }
                }
            }

            if (designs.isNotEmpty()) {
                Text("New designs", style = T.H2, color = A0.Ink, modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    designs.forEach { design ->
                        Column(
                            Modifier
                                .width(230.dp)
                                .a0Clickable { vm.go("chat"); vm.send("A ${design.name.lowercase()} style design") }
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(26.dp))
                                    .background(Brush.linearGradient(listOf(A0.pastel(design.pastel).start, A0.pastel(design.pastel).end)))
                            )
                            Text(design.name, style = T.Card.copy(fontSize = 18.sp), color = A0.Ink, modifier = Modifier.padding(top = 10.dp))
                            Text(design.subtitle, style = T.Small, color = A0.Mu)
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Usage
// ---------------------------------------------------------------------------
@Composable
fun UsageScreen(vm: AppViewModel) {
    Column(Modifier.fillMaxSize()) {
        TopBar(onBack = { vm.go("set") })
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 22.dp, end = 22.dp, bottom = 40.dp)
        ) {
            Text("Usage", style = T.Title, color = A0.Ink)
            Text("Estimated from your own keys.", style = T.Body, color = A0.Mu, modifier = Modifier.padding(top = 6.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .background(Color.White, RoundedCornerShape(A0.RCard))
                    .padding(18.dp)
            ) {
                Text("This month", style = T.Small, color = A0.Mu)
                Text("$2.84", style = T.Display.copy(fontSize = 54.sp), color = A0.Ink)
                Text("412k tokens in · 96k out", style = T.Mono.copy(fontSize = 12.sp), color = A0.Mu)
            }
            SectionLabel("By provider")
            Content.usageProviders.forEach { (name, amount, pct) -> UsageBar(name, amount, pct) }
            SectionLabel("By project")
            Content.usageProjects.forEach { (name, amount, pct) -> UsageBar(name, amount, pct) }
        }
    }
}

@Composable
private fun UsageBar(name: String, amount: String, pct: Int) {
    Column(Modifier.padding(bottom = 16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, style = T.Body, color = A0.Ink)
            Text(amount, style = T.Mono, color = A0.Mu)
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

// ---------------------------------------------------------------------------
// Settings
// ---------------------------------------------------------------------------
@Composable
fun SettingsScreen(vm: AppViewModel) {
    Column(Modifier.fillMaxSize()) {
        TopBar(onBack = { vm.go("chat") })
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 22.dp, end = 22.dp, bottom = 40.dp)
        ) {
            Text("Settings", style = T.Title, color = A0.Ink, modifier = Modifier.padding(bottom = 10.dp))
            Content.settings.forEach { row ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .a0Clickable {
                            when (row.title) {
                                "API keys" -> vm.go("key")
                                "Default models" -> vm.openSheet("model")
                                "Usage and cost" -> vm.go("use")
                                "Font size" -> vm.cycleFontSize()
                                "Language" -> vm.cycleLanguage()
                                "System prompt" -> vm.openSheet("prompt")
                                "Open-source licence" -> vm.openSheet("licence")
                                "GitHub" -> vm.toast("github.com/x4abu/a0-design")
                                else -> vm.toast(row.title)
                            }
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    PastelTile(row.icon, row.pastel, size = 44.dp)
                    Text(row.title, style = T.BodyMedium, color = A0.Ink, modifier = Modifier.weight(1f))
                    when (row.kind) {
                        design.a0.app.data.SettingKind.Toggle -> {
                            val checked = if (row.title == "Haptics") vm.haptics else vm.privacy
                            A0Toggle(
                                checked = checked,
                                onChange = { on ->
                                    if (row.title == "Haptics") vm.haptics = on else vm.privacy = on
                                }
                            )
                        }
                        design.a0.app.data.SettingKind.Value -> {
                            val value = when (row.title) {
                                "Font size" -> listOf("Small", "Medium", "Large")[vm.fontSizeIndex]
                                "Language" -> listOf("English", "Hinglish", "Hindi")[vm.languageIndex]
                                else -> row.value
                            }
                            Text(value, style = T.Body, color = A0.Mu)
                        }
                        else -> A0Icon("chev", 18.dp, A0.Mu)
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// API keys
// ---------------------------------------------------------------------------
@Composable
fun ApiKeysScreen(vm: AppViewModel) {
    Column(Modifier.fillMaxSize()) {
        TopBar(onBack = { vm.go("set") })
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 22.dp, end = 22.dp, bottom = 40.dp)
        ) {
            Text("API keys", style = T.Title, color = A0.Ink)
            Text(
                "Pick a provider and paste your key. It stays encrypted on this device.",
                style = T.Body, color = A0.Mu, modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Content.providers.forEachIndexed { i, provider ->
                    A0Chip(
                        label = provider,
                        pastel = i % 5,
                        selected = vm.keyProvider == i,
                        onClick = { vm.keyProvider = i }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            A0Field(
                value = vm.apiKey,
                onValueChange = { vm.onKeyChange(it) },
                mono = true,
                password = !vm.keyVisible,
                trailing = {
                    Box(
                        Modifier.size(40.dp).a0Clickable { vm.keyVisible = !vm.keyVisible },
                        contentAlignment = Alignment.Center
                    ) { A0Icon("eye", 20.dp, A0.Mu) }
                }
            )
            Spacer(Modifier.height(14.dp))
            A0Button(
                label = when (vm.backendState) {
                    "testing" -> "Testing…"
                    "ok" -> "Connected"
                    else -> "Test connection"
                },
                onClick = { vm.testConnection() }
            )
            Spacer(Modifier.height(10.dp))
            A0Button(
                label = "View source on GitHub",
                primary = false,
                onClick = { vm.toast("github.com/x4abu/a0-design") }
            )
            Spacer(Modifier.height(10.dp))
            A0Button(
                label = "Open the design brain",
                primary = false,
                onClick = { vm.toast("Design Brain lives in the backend /knowledge") }
            )
        }
    }
}
