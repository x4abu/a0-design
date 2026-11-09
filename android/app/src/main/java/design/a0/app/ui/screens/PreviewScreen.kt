package design.a0.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import design.a0.app.data.Content
import design.a0.app.ui.comp.A0Button
import design.a0.app.ui.comp.A0Chip
import design.a0.app.ui.comp.A0Field
import design.a0.app.ui.comp.GlassIconButton
import design.a0.app.ui.comp.Segmented
import design.a0.app.ui.comp.a0Clickable
import design.a0.app.ui.comp.glass
import design.a0.app.ui.icon.A0Icon
import design.a0.app.ui.preview.DevicePreview
import design.a0.app.ui.theme.A0
import design.a0.app.ui.theme.AccentPresets
import design.a0.app.ui.theme.T
import design.a0.app.ui.theme.TypefaceChoice
import design.a0.app.vm.AppViewModel

@Composable
fun PreviewScreen(vm: AppViewModel) {
    var deviceScreen by remember { mutableStateOf("home") }
    var annotate by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        // ---- top bar ---------------------------------------------------
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
                    Text("Moss Coffee · ", style = T.SmallMedium, color = A0.Mu)
                    Text(vm.historyVersion, style = T.SmallMedium, color = A0.AccentDeep)
                }
            }
            GlassIconButton("undo", onClick = { vm.undo() })
            GlassIconButton("share", onClick = { vm.openSheet("export") })
        }

        // ---- device + edit segmented -----------------------------------
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.width(204.dp)) {
                Segmented(Content.deviceModes, vm.deviceMode, onSelect = { vm.deviceMode = it })
            }
            Spacer(Modifier.width(8.dp))
            Box(Modifier.width(120.dp)) {
                Segmented(
                    options = Content.editModes,
                    selected = if (vm.playMode) 1 else 0,
                    onSelect = { vm.playMode = it == 1 }
                )
            }
        }

        // ---- panes -----------------------------------------------------
        Box(Modifier.weight(1f)) {
            when (vm.previewTab) {
                1 -> CanvasPane(vm, deviceScreen) { deviceScreen = it; vm.previewTab = 0 }
                2 -> CodePane(vm)
                3 -> StylePane(vm)
                4 -> HistoryPane(vm)
                else -> Box(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 24.dp, end = 24.dp, top = 10.dp, bottom = 110.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    DevicePreview(
                        ds = vm.ds,
                        deviceMode = vm.deviceMode,
                        screen = deviceScreen,
                        playMode = vm.playMode,
                        selected = vm.selectedElement,
                        onSelect = { vm.selectElement(it) },
                        onNavigate = { deviceScreen = it }
                    )
                }
            }

            if (vm.selectedElement != null && vm.previewTab == 0) {
                ElementToolbar(
                    name = vm.selectedElement ?: "",
                    annotate = annotate,
                    onAnnotate = { annotate = it },
                    onClose = { vm.clearSelection() },
                    onQuick = { vm.quickEdit(it) },
                    onApply = { vm.annotate(annotate); annotate = "" }
                )
            }
        }

        // ---- bottom tabs ----------------------------------------------
        PreviewTabs(vm)
    }
}

@Composable
private fun ElementToolbar(
    name: String,
    annotate: String,
    onAnnotate: (String) -> Unit,
    onClose: () -> Unit,
    onQuick: (String) -> Unit,
    onApply: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(bottom = 96.dp)
            .glass(RoundedCornerShape(26.dp), alpha = 0.92f, elevation = 16.dp)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(name, style = T.Card, color = A0.Ink, modifier = Modifier.weight(1f))
            Box(Modifier.size(32.dp).a0Clickable(onClick = onClose), contentAlignment = Alignment.Center) {
                A0Icon("x", 16.dp, A0.Mu)
            }
        }
        Row(
            Modifier.horizontalScroll(rememberScrollState()).padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Rounder", "Bigger", "Recolour", "Bolder").forEachIndexed { i, action ->
                A0Chip(label = action, pastel = i, height = 38.dp, onClick = { onQuick(action) })
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f)) {
                A0Field(
                    value = annotate,
                    onValueChange = onAnnotate,
                    placeholder = "Annotate: describe a change",
                    height = 46.dp,
                    onImeAction = onApply
                )
            }
            Box(
                Modifier
                    .size(44.dp)
                    .background(Brush.linearGradient(listOf(A0.Accent2, Color(0xFF8FD33A))), CircleShape)
                    .a0Clickable(onClick = onApply),
                contentAlignment = Alignment.Center
            ) { A0Icon("up", 20.dp, A0.AccentInk, 1.9f) }
        }
    }
}

// ---------------------------------------------------------------------------
// Canvas
// ---------------------------------------------------------------------------
@Composable
private fun CanvasPane(vm: AppViewModel, screen: String, onOpen: (String) -> Unit) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(24f, 20f)) }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFFF4F7F0), Color(0xFFEAF0E4)),
                    center = Offset(400f, 400f),
                    radius = 900f
                )
            )
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.4f, 2f)
                    offset += pan
                }
            }
    ) {
        Row(
            Modifier
                .graphicsLayer {
                    translationX = offset.x
                    translationY = offset.y
                    scaleX = scale
                    scaleY = scale
                },
            horizontalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            listOf(
                Triple("Home", "home", Color(0xFFC0623B)),
                Triple("Brew", "brew", Color(0xFFB5654C)),
                Triple("Shop", "shop", Color(0xFF6F8F5C))
            ).forEach { (label, id, tint) ->
                Column(
                    Modifier
                        .width(140.dp)
                        .height(260.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFF6EFE4))
                        .border(1.dp, Color.White, RoundedCornerShape(24.dp))
                        .a0Clickable { onOpen(id) }
                        .padding(12.dp)
                ) {
                    Text(label, style = T.Caption, color = Color(0xFF2B2118))
                    Spacer(Modifier.height(10.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.linearGradient(listOf(tint, tint.copy(alpha = 0.5f))))
                    )
                    Spacer(Modifier.height(10.dp))
                    Box(Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)).background(Color(0xFFDDE2D3)))
                    Spacer(Modifier.height(8.dp))
                    Box(Modifier.fillMaxWidth(0.7f).height(12.dp).clip(RoundedCornerShape(6.dp)).background(Color(0xFFE9DCC8)))
                }
            }
        }

        // zoom + variants controls
        Column(
            Modifier.align(Alignment.BottomStart).padding(start = 14.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                Modifier.glass(RoundedCornerShape(A0.RPill), alpha = 0.92f).padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("−" to 0.8f, "+" to 1.25f).forEach { (label, factor) ->
                    Box(
                        Modifier.size(40.dp).a0Clickable { scale = (scale * factor).coerceIn(0.4f, 2f) },
                        contentAlignment = Alignment.Center
                    ) { Text(label, style = T.Card.copy(fontSize = 20.sp), color = A0.Ink) }
                }
                Text(
                    "${(scale * 100).toInt()}%",
                    style = T.Mono, color = A0.Ink,
                    modifier = Modifier.width(52.dp).padding(horizontal = 4.dp)
                )
                Box(
                    Modifier.size(40.dp).a0Clickable { scale = 1f; offset = Offset(24f, 20f) },
                    contentAlignment = Alignment.Center
                ) { A0Icon("grid", 18.dp, A0.Ink) }
            }
        }

        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 14.dp)
                .padding(bottom = 96.dp)
                .glass(RoundedCornerShape(26.dp), alpha = 0.92f, elevation = 14.dp)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(Modifier.weight(1f)) {
                Segmented(
                    options = listOf("Refine", "Explore", "Reimagine"),
                    selected = vm.variantSet,
                    height = 40.dp,
                    onSelect = { vm.variantSet = it }
                )
            }
            A0Chip(label = "Variants", pastel = 3, height = 40.dp, onClick = { vm.showVariants() })
        }
    }
}

// ---------------------------------------------------------------------------
// Code
// ---------------------------------------------------------------------------
@Composable
private fun CodePane(vm: AppViewModel) {
    val langs = listOf("Compose", "React", "HTML")
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .padding(top = 8.dp, bottom = 110.dp)
    ) {
        Segmented(langs, vm.codeLang, onSelect = { vm.codeLang = it })
        Spacer(Modifier.height(14.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(22.dp))
                .border(1.dp, Color(0xFFE3EADB), RoundedCornerShape(22.dp))
                .padding(16.dp)
        ) {
            Text(codeFor(vm.codeLang), style = T.Mono.copy(fontSize = 12.sp, lineHeight = 20.sp))
        }
        Spacer(Modifier.height(14.dp))
        A0Button(
            label = "Copy code",
            primary = false,
            icon = "copy",
            onClick = { vm.toast("Code copied") }
        )
    }
}

private fun codeFor(lang: Int): AnnotatedString {
    val key = SpanStyle(color = A0.CodeKey)
    val str = SpanStyle(color = A0.CodeString)
    val plain = SpanStyle(color = A0.CodePlain)
    return when (lang) {
        0 -> buildAnnotatedString {
            withStyle(key) { append("@Composable\nfun ") }
            withStyle(plain) { append("Hero() {\n  Card(shape = RoundedCornerShape(") }
            withStyle(str) { append("26.dp") }
            withStyle(plain) { append("),\n    colors = CardDefaults.cardColors(\n      ") }
            withStyle(str) { append("Color(0xFFC0623B)") }
            withStyle(plain) { append(")\n  ) { Text(") }
            withStyle(str) { append("\"Ethiopia Guji\"") }
            withStyle(plain) { append(") }\n}") }
        }
        1 -> buildAnnotatedString {
            withStyle(key) { append("export default function ") }
            withStyle(plain) { append("Hero() {\n  ") }
            withStyle(key) { append("return ") }
            withStyle(plain) { append("<div className=") }
            withStyle(str) { append("\"rounded-[26px] bg-[#C0623B] p-4\"") }
            withStyle(plain) { append(">\n    Ethiopia Guji\n  </div>\n}") }
        }
        else -> buildAnnotatedString {
            withStyle(plain) { append("<") }
            withStyle(key) { append("div") }
            withStyle(plain) { append(" class=") }
            withStyle(str) { append("\"hero\"") }
            withStyle(plain) { append(">Ethiopia Guji</") }
            withStyle(key) { append("div") }
            withStyle(plain) { append(">") }
        }
    }
}

// ---------------------------------------------------------------------------
// Style (design system)
// ---------------------------------------------------------------------------
@Composable
private fun StylePane(vm: AppViewModel) {
    val (label, pass) = vm.contrastLabel()
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .padding(top = 8.dp, bottom = 110.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Design system", style = T.H2, color = A0.Ink, modifier = Modifier.weight(1f))
            A0Chip(label = label, pastel = if (pass) 1 else 2, height = 32.dp)
        }

        SectionText("Accent")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AccentPresets.forEachIndexed { i, (a, b) ->
                val selected = vm.ds.accent == a
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(a, b)))
                        .then(
                            if (selected) Modifier.border(5.dp, Color.White, CircleShape)
                                .border(7.dp, A0.Ink, CircleShape)
                            else Modifier
                        )
                        .a0Clickable { vm.setAccent(i) }
                )
            }
        }

        SectionText("Typeface")
        Segmented(listOf("Sans", "Serif", "Mono"), vm.ds.typeface.ordinal, onSelect = { vm.setTypeface(it) })

        SectionText("Roundness ${vm.ds.radius}")
        Slider(
            value = vm.ds.radius.toFloat(),
            onValueChange = { vm.setRadius(it.toInt()) },
            valueRange = 4f..40f,
            colors = SliderDefaults.colors(
                thumbColor = A0.Accent,
                activeTrackColor = A0.Accent,
                inactiveTrackColor = A0.Track
            )
        )

        SectionText("Mode")
        Segmented(listOf("Light", "Dark"), if (vm.ds.dark) 1 else 0, onSelect = { vm.setDark(it == 1) })

        SectionText("Fidelity")
        Segmented(listOf("Wireframe", "Hi-fi"), if (vm.ds.wireframe) 0 else 1, onSelect = { vm.setWireframe(it == 0) })

        Spacer(Modifier.height(22.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.weight(1f)) {
                A0Button(label = "Export DESIGN.md", primary = false, onClick = { vm.toast("DESIGN.md copied") })
            }
            Box(Modifier.weight(1f)) {
                A0Button(label = "From URL", primary = false, onClick = { vm.toast("Design system extracted from URL") })
            }
        }
    }
}

@Composable
private fun SectionText(text: String) {
    Text(text, style = T.Small, color = A0.Mu, modifier = Modifier.padding(top = 22.dp, bottom = 10.dp))
}

// ---------------------------------------------------------------------------
// History
// ---------------------------------------------------------------------------
@Composable
private fun HistoryPane(vm: AppViewModel) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .padding(top = 8.dp, bottom = 110.dp)
    ) {
        Text("History", style = T.H2, color = A0.Ink, modifier = Modifier.padding(bottom = 10.dp))
        Content.history.forEachIndexed { i, v ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    Modifier
                        .size(width = 44.dp, height = 64.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFD8B98A), Color(0xFFC0623B)),
                                start = Offset(0f, i * 40f),
                                end = Offset(60f, 60f)
                            )
                        )
                )
                Column(Modifier.weight(1f)) {
                    Text("${v.tag} · ${v.title}", style = T.SmallMedium, color = A0.Ink)
                    Text(v.whenLabel, style = T.Caption, color = A0.Mu)
                }
                A0Chip(label = "Compare", pastel = 4, height = 34.dp, onClick = { vm.applyVersion(i, false) })
                A0Chip(label = "Restore", pastel = 3, height = 34.dp, onClick = { vm.applyVersion(i, true) })
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Floating bottom tabs: icons only, the active one expands into a dark pill
// ---------------------------------------------------------------------------
@Composable
private fun PreviewTabs(vm: AppViewModel) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 18.dp, bottom = 16.dp)
            .glass(RoundedCornerShape(A0.RPill), alpha = 0.88f, elevation = 16.dp)
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Content.previewTabs.forEachIndexed { i, (label, icon, _) ->
            val on = vm.previewTab == i
            val weight by animateFloatAsState(if (on) 1.9f else 1f, tween(450, easing = A0.Pill), label = "tabWeight")
            Row(
                Modifier
                    .weight(weight)
                    .height(58.dp)
                    .clip(RoundedCornerShape(A0.RPill))
                    .background(if (on) A0.Ink else Color.Transparent)
                    .a0Clickable { vm.previewTab = i; vm.clearSelection() },
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
