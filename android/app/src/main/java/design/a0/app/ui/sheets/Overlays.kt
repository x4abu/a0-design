package design.a0.app.ui.sheets

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import design.a0.app.data.DesignEngine
import design.a0.app.data.summary
import design.a0.app.ui.comp.A0Button
import design.a0.app.ui.comp.A0Field
import design.a0.app.ui.comp.PastelTile
import design.a0.app.ui.comp.a0Clickable
import design.a0.app.ui.comp.glass
import design.a0.app.ui.icon.A0Icon
import design.a0.app.ui.mascot.Mascot
import design.a0.app.ui.screens.hexColor
import design.a0.app.ui.theme.A0
import design.a0.app.ui.theme.T
import design.a0.app.vm.AppViewModel

// ---------------------------------------------------------------------------
// Drawer
// ---------------------------------------------------------------------------
@Composable
fun A0Drawer(vm: AppViewModel, modifier: Modifier = Modifier.fillMaxSize()) {
    AnimatedVisibility(visible = vm.drawer, enter = fadeIn(tween(250)), exit = fadeOut(tween(250))) {
        Box(Modifier.fillMaxSize().background(A0.SheetScrim).a0Clickable { vm.closeAll() })
    }
    Box(modifier, contentAlignment = Alignment.CenterStart) {
        AnimatedVisibility(
            visible = vm.drawer,
            enter = slideInHorizontally(tween(600, easing = A0.Pill)) { -it },
            exit = slideOutHorizontally(tween(300, easing = A0.Ease)) { -it }
        ) {
            Column(
                Modifier
                    .width(304.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFFFDFEFB), Color(0xFFF3F7EE))),
                        RoundedCornerShape(topEnd = A0.RSheet, bottomEnd = A0.RSheet)
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(start = 18.dp, end = 18.dp, top = 60.dp, bottom = 24.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Mascot(size = 44.dp, mood = vm.mood)
                    Text("a0", style = T.Title.copy(fontSize = 30.sp), color = A0.Ink)
                }
                A0Button(label = "New chat", icon = "edit", onClick = { vm.newChat() })
                Spacer(Modifier.height(12.dp))

                listOf(
                    Triple("chat", "Studio", "spark"),
                    Triple("tpl", "Directions", "grid"),
                    Triple("prj", "Projects", "folder"),
                    Triple("use", "Usage", "chart"),
                    Triple("set", "Settings", "gear")
                ).forEachIndexed { i, (id, label, icon) ->
                    Row(
                        Modifier.fillMaxWidth().a0Clickable { vm.go(id) }.padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        PastelTile(icon, i % 5, size = 44.dp)
                        Text(label, style = T.BodyMedium, color = A0.Ink, modifier = Modifier.weight(1f))
                    }
                }

                Text(
                    if (vm.projects.isEmpty()) "No projects yet" else "Recent",
                    style = T.Small, color = A0.Mu,
                    modifier = Modifier.padding(top = 22.dp, bottom = 6.dp)
                )
                vm.projects.take(6).forEach { project ->
                    Text(
                        project.name,
                        style = T.Small,
                        color = A0.Mu,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .a0Clickable { vm.openProject(project) }
                            .padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Bottom navigation
// ---------------------------------------------------------------------------
@Composable
fun A0BottomNav(vm: AppViewModel, visible: Boolean, modifier: Modifier = Modifier.fillMaxSize()) {
    Box(modifier, contentAlignment = Alignment.BottomCenter) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(tween(600, easing = A0.Pill)) { it * 2 } + fadeIn(tween(300)),
            exit = slideOutVertically(tween(300, easing = A0.Ease)) { it * 2 } + fadeOut(tween(200))
        ) {
            Row(
                Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    Modifier
                        .weight(1f)
                        .height(68.dp)
                        .glass(RoundedCornerShape(A0.RPill), alpha = 0.88f, elevation = 16.dp)
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        Triple("chat", "Studio", "spark"),
                        Triple("tpl", "Explore", "grid"),
                        Triple("prj", "Projects", "folder")
                    ).forEach { (id, label, icon) ->
                        val on = vm.screen == id
                        val weight by animateFloatAsState(
                            if (on) 1.9f else 1f, tween(450, easing = A0.Pill), label = "navWeight"
                        )
                        Row(
                            Modifier
                                .weight(weight)
                                .height(56.dp)
                                .clip(RoundedCornerShape(A0.RPill))
                                .background(if (on) A0.Ink else Color.Transparent)
                                .a0Clickable { vm.go(id) },
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
                Box(
                    Modifier
                        .size(68.dp)
                        .glass(CircleShape, alpha = 0.9f, elevation = 16.dp)
                        .background(if (vm.screen == "set") A0.Ink else Color.Transparent, CircleShape)
                        .a0Clickable { vm.go("set") },
                    contentAlignment = Alignment.Center
                ) {
                    A0Icon("gear", 22.dp, if (vm.screen == "set") Color.White else A0.Ink)
                }
            }
        }
    }
}

@Composable
fun MiniDesignPill(vm: AppViewModel, visible: Boolean, modifier: Modifier = Modifier.fillMaxSize()) {
    val project = vm.currentProject
    Box(modifier, contentAlignment = Alignment.BottomCenter) {
        AnimatedVisibility(
            visible = visible && project != null,
            enter = slideInVertically(tween(500, easing = A0.Pill)) { it * 3 } + fadeIn(tween(300)),
            exit = slideOutVertically(tween(250)) { it * 3 } + fadeOut(tween(150))
        ) {
            Row(
                Modifier
                    .padding(horizontal = 14.dp)
                    .fillMaxWidth()
                    .height(72.dp)
                    .glass(RoundedCornerShape(A0.RPill), alpha = 0.9f, elevation = 16.dp)
                    .a0Clickable { vm.go("preview") }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    hexColor(project?.latest?.spec?.theme?.bg ?: "#F6EFE4", Color(0xFFF6EFE4)),
                                    hexColor(project?.latest?.spec?.theme?.accent ?: "#C0623B", Color(0xFFC0623B))
                                )
                            )
                        )
                )
                Column(Modifier.weight(1f)) {
                    Text(project?.name ?: "", style = T.SmallMedium, color = A0.Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        "${project?.versions?.size ?: 0} versions · open",
                        style = T.Caption, color = A0.Mu
                    )
                }
                A0Icon("chev", 18.dp, A0.Mu)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Sheets
// ---------------------------------------------------------------------------
@Composable
fun A0SheetHost(vm: AppViewModel, modifier: Modifier = Modifier.fillMaxSize()) {
    val open = vm.sheet != null
    AnimatedVisibility(visible = open, enter = fadeIn(tween(250)), exit = fadeOut(tween(200))) {
        Box(Modifier.fillMaxSize().background(A0.SheetScrim).a0Clickable { vm.closeAll() })
    }
    Box(modifier, contentAlignment = Alignment.BottomCenter) {
        AnimatedVisibility(
            visible = open,
            enter = slideInVertically(tween(600, easing = A0.Pill)) { it },
            exit = slideOutVertically(tween(300, easing = A0.Ease)) { it }
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = 640.dp)
                    .clip(RoundedCornerShape(topStart = A0.RSheet, topEnd = A0.RSheet))
                    .background(Color.White.copy(alpha = 0.97f))
                    .verticalScroll(rememberScrollState())
                    .padding(start = 22.dp, end = 22.dp, top = 12.dp, bottom = 34.dp)
            ) {
                Box(
                    Modifier
                        .width(40.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(A0.Track)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(16.dp))
                when {
                    vm.sheet == "model" -> ModelSheet(vm)
                    vm.sheet == "export" -> ExportSheet(vm)
                    vm.sheet == "prompt" -> PromptSheet(vm)
                    vm.sheet == "licence" -> LicenceSheet()
                    vm.sheet == "wipe" -> WipeSheet(vm)
                    vm.sheet == "engine" -> EngineSheet(vm)
                    vm.sheet?.startsWith("project:") == true ->
                        ProjectSheet(vm, vm.sheet!!.substringAfter("project:"))
                }
            }
        }
    }
}

@Composable
private fun ModelSheet(vm: AppViewModel) {
    var query by remember { mutableStateOf("") }
    var custom by remember { mutableStateOf("") }

    Text("Model", style = T.H2.copy(fontSize = 28.sp), color = A0.Ink, modifier = Modifier.padding(bottom = 4.dp))
    Text(
        when (vm.modelsState) {
            "loading" -> "Loading the live catalogue from OpenRouter…"
            "error" -> vm.modelsError
            "ready" -> "${vm.models.size} models available"
            else -> "Open the list to load the catalogue"
        },
        style = T.Caption, color = A0.Mu, modifier = Modifier.padding(bottom = 12.dp)
    )

    A0Field(value = query, onValueChange = { query = it }, placeholder = "Search models", height = 48.dp)
    Spacer(Modifier.height(8.dp))

    if (vm.modelsState == "idle" || vm.modelsState == "error") {
        A0Button(label = "Load models", primary = false, onClick = { vm.loadModels() })
        Spacer(Modifier.height(12.dp))
    }

    vm.models.filter { query.isBlank() || it.id.contains(query, true) || it.label.contains(query, true) }
        .take(40)
        .forEach { model ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .a0Clickable { vm.setModel(model.id) }
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(model.id, style = T.SmallMedium, color = A0.Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        listOfNotNull(
                            model.context.takeIf { it.isNotBlank() }?.let { "$it ctx" },
                            if (model.promptPrice > 0) "$" + String.format("%.2f", model.promptPrice * 1_000_000) + "/M in" else null
                        ).joinToString(" · "),
                        style = T.MonoSmall, color = A0.Mu
                    )
                }
                if (vm.settings.model == model.id) A0Icon("check", 18.dp, A0.AccentDeep, 2f)
            }
        }

    Spacer(Modifier.height(12.dp))
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.weight(1f)) {
            A0Field(value = custom, onValueChange = { custom = it }, placeholder = "provider/model-id", height = 48.dp)
        }
        Box(Modifier.width(96.dp)) {
            A0Button(label = "Use", onClick = { if (custom.isBlank()) vm.toast("Enter a model id") else vm.setModel(custom.trim()) })
        }
    }
}

@Composable
private fun ExportSheet(vm: AppViewModel) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val spec = vm.spec
    Text("Export", style = T.H2.copy(fontSize = 28.sp), color = A0.Ink, modifier = Modifier.padding(bottom = 6.dp))

    if (spec == null) {
        Text("No design open.", style = T.Small, color = A0.Mu)
        return
    }

    val code = DesignEngine.composeCode(spec)
    val markdown = spec.summary()

    listOf(
        "Copy Compose code" to { clipboard.setText(AnnotatedString(code)); vm.toast("Compose code copied") },
        "Copy DESIGN.md" to { clipboard.setText(AnnotatedString(markdown)); vm.toast("Markdown copied") },
        "Share design" to {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, spec.name)
                putExtra(Intent.EXTRA_TEXT, markdown)
            }
            context.startActivity(Intent.createChooser(intent, "Share design"))
            vm.closeAll()
        }
    ).forEach { (label, action) ->
        Row(
            Modifier.fillMaxWidth().a0Clickable { action() }.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = T.Body, color = A0.Ink, modifier = Modifier.weight(1f))
            A0Icon("chev", 18.dp, A0.Mu)
        }
    }
}

@Composable
private fun PromptSheet(vm: AppViewModel) {
    var text by remember { mutableStateOf(vm.settings.systemPrompt) }
    Text("System prompt", style = T.H2.copy(fontSize = 26.sp), color = A0.Ink, modifier = Modifier.padding(bottom = 6.dp))
    Text(
        "Appended to every request, after the design rules. Leave empty to use the defaults.",
        style = T.Caption, color = A0.Mu, modifier = Modifier.padding(bottom = 10.dp)
    )
    A0Field(
        value = text,
        onValueChange = { text = it },
        placeholder = "e.g. always write copy in Hinglish",
        height = 120.dp,
        singleLine = false
    )
    Spacer(Modifier.height(14.dp))
    A0Button(label = "Save", onClick = { vm.setSystemPrompt(text); vm.closeAll(); vm.toast("Saved") })
}

@Composable
private fun LicenceSheet() {
    Text("MIT licence", style = T.H2.copy(fontSize = 26.sp), color = A0.Ink, modifier = Modifier.padding(bottom = 10.dp))
    Text(
        "Copyright (c) 2026 a0 design contributors\n\n" +
            "Permission is hereby granted, free of charge, to any person obtaining a copy of this " +
            "software and associated documentation files (the \"Software\"), to deal in the Software " +
            "without restriction, including without limitation the rights to use, copy, modify, merge, " +
            "publish, distribute, sublicense, and/or sell copies of the Software, subject to the " +
            "condition that the above copyright notice and this permission notice are included.\n\n" +
            "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND.",
        style = T.Small, color = A0.InkSoft
    )
    Spacer(Modifier.height(10.dp))
    Text(
        "Bundled fonts: DM Sans, Instrument Serif, Geist Mono — SIL Open Font Licence.",
        style = T.Caption, color = A0.Mu
    )
}

@Composable
private fun WipeSheet(vm: AppViewModel) {
    Text("Clear local data", style = T.H2.copy(fontSize = 26.sp), color = A0.Ink, modifier = Modifier.padding(bottom = 8.dp))
    Text(
        "Deletes every project, version, usage record and API key stored on this device. " +
            "This cannot be undone.",
        style = T.Small, color = A0.InkSoft, modifier = Modifier.padding(bottom = 16.dp)
    )
    A0Button(label = "Delete everything", onClick = { vm.clearEverything(); vm.closeAll() })
    Spacer(Modifier.height(8.dp))
    A0Button(label = "Cancel", primary = false, onClick = { vm.closeAll() })
}

@Composable
private fun EngineSheet(vm: AppViewModel) {
    Text("Engine", style = T.H2.copy(fontSize = 26.sp), color = A0.Ink, modifier = Modifier.padding(bottom = 8.dp))
    Text(
        if (vm.settings.engine == "backend")
            "Requests go to ${vm.settings.backendUrl.ifBlank { "an unset backend" }} — the server assembles the full Design Brain before calling the model."
        else
            "Direct mode: this device calls ${vm.settings.provider} with your key and a compact rule set. Fastest, no server needed.",
        style = T.Small, color = A0.InkSoft, modifier = Modifier.padding(bottom = 16.dp)
    )
    A0Button(label = "Change in API keys", onClick = { vm.closeAll(); vm.go("key") })
}

@Composable
private fun ProjectSheet(vm: AppViewModel, projectId: String) {
    val project = vm.projects.firstOrNull { it.id == projectId }
    if (project == null) {
        Text("Project not found", style = T.Body, color = A0.Mu)
        return
    }
    var name by remember(project.id) { mutableStateOf(project.name) }
    Text("Project", style = T.H2.copy(fontSize = 26.sp), color = A0.Ink, modifier = Modifier.padding(bottom = 12.dp))
    A0Field(value = name, onValueChange = { name = it }, placeholder = "Project name", height = 52.dp)
    Spacer(Modifier.height(12.dp))
    A0Button(
        label = "Open",
        onClick = { vm.closeAll(); vm.openProject(project) }
    )
    Spacer(Modifier.height(8.dp))
    A0Button(
        label = "Save name",
        primary = false,
        onClick = { vm.renameProject(project, name.trim().ifBlank { project.name }); vm.closeAll() }
    )
    Spacer(Modifier.height(8.dp))
    A0Button(
        label = "Delete project",
        primary = false,
        onClick = { vm.deleteProject(project) }
    )
}
