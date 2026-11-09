package design.a0.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import design.a0.app.data.DesignEngine
import design.a0.app.data.Spec
import design.a0.app.data.SpecJson
import design.a0.app.data.summary
import design.a0.app.ui.comp.A0Button
import design.a0.app.ui.comp.A0Chip
import design.a0.app.ui.comp.A0Field
import design.a0.app.ui.comp.GlassIconButton
import design.a0.app.ui.comp.Segmented
import design.a0.app.ui.comp.a0Clickable
import design.a0.app.ui.comp.glass
import design.a0.app.ui.icon.A0Icon
import design.a0.app.ui.preview.SpecPreview
import design.a0.app.ui.theme.A0
import design.a0.app.ui.theme.T
import design.a0.app.vm.AppViewModel

private val tabs = listOf(
    Triple("Design", "phone", 0),
    Triple("Screens", "layers", 1),
    Triple("Code", "code", 2),
    Triple("Style", "pal", 3),
    Triple("History", "clock", 4)
)

@Composable
fun PreviewScreen(vm: AppViewModel) {
    val spec = vm.spec
    val clipboard = LocalClipboardManager.current

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 52.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlassIconButton("back", onClick = { vm.go("chat") })
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Row(
                    Modifier
                        .glass(RoundedCornerShape(A0.RPill), alpha = 0.85f, elevation = 8.dp)
                        .padding(horizontal = 16.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        (vm.currentProject?.name ?: spec?.name ?: "No design").take(18),
                        style = T.SmallMedium,
                        color = A0.Mu,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text("· ${vm.currentVersion?.label ?: "—"}", style = T.SmallMedium, color = A0.AccentDeep)
                }
            }
            GlassIconButton("share", onClick = { vm.openSheet("export") })
        }

        if (spec == null) {
            EmptyDesign(vm)
            return@Column
        }

        Box(Modifier.weight(1f)) {
            when (vm.previewTab) {
                1 -> ScreensPane(vm, spec)
                2 -> CodePane(vm, spec) { clipboard.setText(AnnotatedString(it)); vm.toast("Copied") }
                3 -> StylePane(vm, spec)
                4 -> HistoryPane(vm)
                else -> DesignPane(vm, spec)
            }
        }

        BottomTabs(vm)
    }
}

@Composable
private fun EmptyDesign(vm: AppViewModel) {
    Box(Modifier.fillMaxSize().padding(22.dp), contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .fillMaxWidth()
                .glass(RoundedCornerShape(A0.RCard), alpha = 0.92f)
                .padding(20.dp)
        ) {
            Text("No design open", style = T.H2, color = A0.Ink)
            Text(
                "Nothing is loaded. Generate a design in Studio, or open one from Projects.",
                style = T.Small, color = A0.Mu, modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
            )
            A0Button(label = "Go to Studio", onClick = { vm.go("chat") })
            Spacer(Modifier.height(8.dp))
            A0Button(label = "Open Projects", primary = false, onClick = { vm.go("prj") })
        }
    }
}

@Composable
private fun DesignPane(vm: AppViewModel, spec: Spec) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 24.dp, end = 24.dp, top = 10.dp, bottom = 110.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpecPreview(
            spec = spec,
            screenIndex = vm.screenIndex,
            deviceMode = 0,
            selectedBlock = vm.selectedBlock,
            onSelectBlock = { vm.selectedBlock = it }
        )
        vm.selectedBlock?.let { index ->
            val block = spec.screens.getOrNull(vm.screenIndex)?.blocks?.getOrNull(index)
            if (block != null) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                        .glass(RoundedCornerShape(20.dp), alpha = 0.92f)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    A0Chip(label = block.type, pastel = 1, height = 32.dp)
                    Text(
                        (block.text.ifBlank { block.title }).take(60),
                        style = T.Small,
                        color = A0.InkSoft,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Box(Modifier.size(28.dp).a0Clickable { vm.selectedBlock = null }, contentAlignment = Alignment.Center) {
                        A0Icon("x", 14.dp, A0.Mu)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScreensPane(vm: AppViewModel, spec: Spec) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .padding(top = 8.dp, bottom = 110.dp)
    ) {
        Text("Screens", style = T.H2, color = A0.Ink, modifier = Modifier.padding(bottom = 10.dp))
        spec.screens.forEachIndexed { index, screen ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .a0Clickable { vm.screenIndex = index; vm.previewTab = 0 }
                    .glass(RoundedCornerShape(20.dp), alpha = 0.9f)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    Modifier
                        .size(width = 44.dp, height = 62.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    hexColor(spec.theme.bg, Color(0xFFF6EFE4)),
                                    hexColor(spec.theme.accent, Color(0xFFC0623B))
                                )
                            )
                        )
                )
                Column(Modifier.weight(1f)) {
                    Text(screen.title.ifBlank { screen.id }, style = T.Card, color = A0.Ink)
                    Text("${screen.blocks.size} blocks", style = T.Caption, color = A0.Mu)
                }
                A0Icon("chev", 18.dp, A0.Mu)
            }
        }
    }
}

@Composable
private fun CodePane(vm: AppViewModel, spec: Spec, onCopy: (String) -> Unit) {
    var lang by remember { mutableStateOf(0) }
    val code = if (lang == 0) DesignEngine.composeCode(spec) else spec.summary()
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .padding(top = 8.dp, bottom = 110.dp)
    ) {
        Segmented(listOf("Compose", "Markdown"), lang, onSelect = { lang = it })
        Spacer(Modifier.height(14.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(22.dp))
                .border(1.dp, Color(0xFFE3EADB), RoundedCornerShape(22.dp))
                .padding(16.dp)
        ) {
            Text(code, style = T.Mono.copy(fontSize = 12.sp, lineHeight = 20.sp), color = A0.CodePlain)
        }
        Spacer(Modifier.height(14.dp))
        A0Button(label = "Copy", primary = false, icon = "copy", onClick = { onCopy(code) })
    }
}

@Composable
private fun StylePane(vm: AppViewModel, spec: Spec) {
    val theme = spec.theme
    val contrast = DesignEngine.contrast(theme.ink, theme.bg)
    var hex by remember(theme.accent) { mutableStateOf(theme.accent) }
    val presets = listOf("#7FCF2E", "#C0623B", "#E8638C", "#6F8F5C", "#D6A24A", "#3B322F")

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .padding(top = 8.dp, bottom = 110.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Tokens", style = T.H2, color = A0.Ink, modifier = Modifier.weight(1f))
            A0Chip(
                label = contrast?.let { String.format("%.1f:1", it) } ?: "—",
                pastel = if ((contrast ?: 0.0) >= 4.5) 1 else 2,
                height = 32.dp
            )
        }

        Label("Accent")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            presets.forEach { hexValue ->
                val selected = theme.accent.equals(hexValue, true)
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(hexColor(hexValue, A0.Accent))
                        .then(
                            if (selected) Modifier.border(3.dp, Color.White, CircleShape)
                                .border(5.dp, A0.Ink, CircleShape)
                            else Modifier
                        )
                        .a0Clickable {
                            vm.updateTheme(theme.copy(accent = hexValue, accent2 = lighten(hexValue)))
                            hex = hexValue
                        }
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f)) {
                A0Field(
                    value = hex,
                    onValueChange = {
                        hex = it
                        if (it.length == 7) vm.updateTheme(theme.copy(accent = it, accent2 = lighten(it)))
                    },
                    mono = true,
                    height = 48.dp
                )
            }
            Text("accent", style = T.Caption, color = A0.Mu)
        }

        Label("Roundness ${theme.radius}")
        Slider(
            value = theme.radius.toFloat(),
            onValueChange = { vm.updateTheme(theme.copy(radius = it.toInt())) },
            valueRange = 8f..34f,
            colors = SliderDefaults.colors(
                thumbColor = A0.Accent, activeTrackColor = A0.Accent, inactiveTrackColor = A0.Track
            )
        )

        Label("Typeface")
        Segmented(
            listOf("Sans", "Serif", "Mono"),
            when (theme.font) { "serif" -> 1; "mono" -> 2; else -> 0 },
            onSelect = {
                vm.updateTheme(theme.copy(font = when (it) { 1 -> "serif"; 2 -> "mono"; else -> "sans" }))
            }
        )

        Label("Audit")
        DesignEngine.audit(spec).forEach { finding ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(8.dp)
                        .background(if (finding.ok) A0.Success else Color(0xFFD97757), CircleShape)
                )
                Text(finding.title, style = T.Small, color = A0.Ink)
                Text(finding.detail, style = T.Caption, color = A0.Mu, modifier = Modifier.weight(1f))
            }
        }

        Spacer(Modifier.height(20.dp))
        A0Button(label = "Save as new version", onClick = { vm.commitStyleChange("style edit") })
    }
}

@Composable
private fun HistoryPane(vm: AppViewModel) {
    val project = vm.currentProject
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .padding(top = 8.dp, bottom = 110.dp)
    ) {
        Text("History", style = T.H2, color = A0.Ink, modifier = Modifier.padding(bottom = 10.dp))
        if (project == null || project.versions.isEmpty()) {
            Text("No versions yet.", style = T.Small, color = A0.Mu)
            return@Column
        }
        project.versions.sortedByDescending { it.createdAt }.forEach { version ->
            val active = vm.currentVersion?.id == version.id
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .a0Clickable { vm.restoreVersion(version) }
                    .glass(RoundedCornerShape(20.dp), alpha = if (active) 0.95f else 0.8f)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "${version.label} · ${version.spec.name}",
                        style = T.SmallMedium,
                        color = A0.Ink
                    )
                    Text(
                        android.text.format.DateFormat.format("d MMM, HH:mm", version.createdAt).toString(),
                        style = T.Caption,
                        color = A0.Mu
                    )
                }
                if (active) A0Chip(label = "Open", pastel = 1, height = 30.dp)
            }
        }
    }
}

@Composable
private fun Label(text: String) {
    Text(text, style = T.Small, color = A0.Mu, modifier = Modifier.padding(top = 22.dp, bottom = 10.dp))
}

@Composable
private fun BottomTabs(vm: AppViewModel) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 18.dp, bottom = 16.dp)
            .glass(RoundedCornerShape(A0.RPill), alpha = 0.88f, elevation = 16.dp)
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEach { (label, icon, index) ->
            val on = vm.previewTab == index
            val weight by animateFloatAsState(if (on) 1.9f else 1f, tween(450, easing = A0.Pill), label = "tabWeight")
            Row(
                Modifier
                    .weight(weight)
                    .height(58.dp)
                    .clip(RoundedCornerShape(A0.RPill))
                    .background(if (on) A0.Ink else Color.Transparent)
                    .a0Clickable { vm.previewTab = index },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                A0Icon(icon, 20.dp, if (on) Color.White else Color(0xFF5D675F))
                if (on) {
                    Text(
                        label,
                        style = T.ChipOn.copy(fontSize = 13.sp),
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

private fun lighten(hex: String): String {
    val v = hex.removePrefix("#")
    if (v.length != 6) return hex
    return runCatching {
        val r = (v.substring(0, 2).toInt(16) + 60).coerceAtMost(255)
        val g = (v.substring(2, 4).toInt(16) + 60).coerceAtMost(255)
        val b = (v.substring(4, 6).toInt(16) + 60).coerceAtMost(255)
        "#%02X%02X%02X".format(r, g, b)
    }.getOrDefault(hex)
}
