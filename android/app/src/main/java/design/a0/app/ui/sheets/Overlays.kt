package design.a0.app.ui.sheets

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
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import design.a0.app.data.Content
import design.a0.app.ui.comp.A0Button
import design.a0.app.ui.comp.A0Chip
import design.a0.app.ui.comp.A0Field
import design.a0.app.ui.comp.PastelTile
import design.a0.app.ui.comp.a0Clickable
import design.a0.app.ui.comp.glass
import design.a0.app.ui.comp.pressScale
import design.a0.app.ui.icon.A0Icon
import design.a0.app.ui.mascot.Mascot
import design.a0.app.ui.mascot.Mood
import design.a0.app.ui.theme.A0
import design.a0.app.ui.theme.T
import design.a0.app.vm.AppViewModel

// ---------------------------------------------------------------------------
// Drawer
// ---------------------------------------------------------------------------
@Composable
fun A0Drawer(vm: AppViewModel, modifier: Modifier = Modifier.fillMaxSize()) {
    AnimatedVisibility(
        visible = vm.drawer,
        enter = fadeIn(tween(250)),
        exit = fadeOut(tween(250))
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(A0.SheetScrim)
                .a0Clickable { vm.closeAll() }
        )
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
                .padding(top = 0.dp)
                .background(
                    Brush.verticalGradient(listOf(Color(0xFFFDFEFB), Color(0xFFF3F7EE))),
                    RoundedCornerShape(topEnd = A0.RSheet, bottomEnd = A0.RSheet)
                )
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
            Content.drawerNav.forEach { item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .a0Clickable { vm.go(item.id) }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    PastelTile(item.icon, item.pastel, size = 44.dp)
                    Text(item.label, style = T.BodyMedium, color = A0.Ink, modifier = Modifier.weight(1f))
                }
            }
            Text("Recent", style = T.Small, color = A0.Mu, modifier = Modifier.padding(top = 22.dp, bottom = 6.dp))
            Content.recentChats.forEach { chat ->
                Text(
                    chat,
                    style = T.Small,
                    color = A0.Mu,
                    modifier = Modifier
                        .fillMaxWidth()
                        .a0Clickable { vm.go("prev") }
                        .padding(vertical = 12.dp)
                )
            }
        }
    }
    }
}

// ---------------------------------------------------------------------------
// Bottom navigation: 3 destinations + settings orb
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
            Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp, bottom = 16.dp),
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
                Content.bottomNav.forEach { item ->
                    val on = vm.screen == item.id
                    val weight by animateFloatAsState(if (on) 1.9f else 1f, tween(450, easing = A0.Pill), label = "navWeight")
                    Row(
                        Modifier
                            .weight(weight)
                            .height(56.dp)
                            .clip(RoundedCornerShape(A0.RPill))
                            .background(if (on) A0.Ink else Color.Transparent)
                            .a0Clickable { vm.go(item.id) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        A0Icon(item.icon, 20.dp, if (on) Color.White else Color(0xFF5D675F))
                        if (on) {
                            Text(
                                item.label,
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

/** Floating "current design" pill shown above the nav on Projects / Explore. */
@Composable
fun MiniDesignPill(vm: AppViewModel, visible: Boolean, modifier: Modifier = Modifier.fillMaxSize()) {
    Box(modifier, contentAlignment = Alignment.BottomCenter) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(tween(500, easing = A0.Pill)) { it * 3 } + fadeIn(tween(300)),
        exit = slideOutVertically(tween(250)) { it * 3 } + fadeOut(tween(150))
    ) {
        Row(
            Modifier
                .padding(horizontal = 14.dp)
                .fillMaxWidth()
                .height(72.dp)
                .glass(RoundedCornerShape(A0.RPill), alpha = 0.9f, elevation = 16.dp)
                .a0Clickable { vm.go("prev") }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFFF6EFE4), Color(0xFFD8B98A), Color(0xFFC0623B))))
            )
            Column(Modifier.weight(1f)) {
                Text("Moss Coffee", style = T.SmallMedium, color = A0.Ink)
                Text("Design · v3 · Open", style = T.Caption, color = A0.Mu)
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
                .heightIn(max = 620.dp)
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
            when (vm.sheet) {
                "model" -> ModelSheet(vm)
                "attach" -> AttachSheet(vm)
                "export" -> ExportSheet(vm)
                "action" -> ActionSheet(vm)
                "drafts" -> ListSheet("Drafts", listOf("Paper Budget · draft", "Salt Notes · draft")) { vm.go("prev") }
                "exports" -> ListSheet("Exports", listOf("moss-coffee.html", "DESIGN.md")) { vm.toast("$it ready") }
                "licence" -> ListSheet("MIT licence", listOf("Read on GitHub")) { vm.toast("github.com/x4abu/a0-design") }
                "prompt" -> PromptSheet(vm)
            }
        }
    }
    }
}

@Composable
private fun ModelSheet(vm: AppViewModel) {
    var query by remember { mutableStateOf("") }
    var custom by remember { mutableStateOf("") }
    Text("Model", style = T.H2.copy(fontSize = 28.sp), color = A0.Ink, modifier = Modifier.padding(bottom = 12.dp))
    A0Field(value = query, onValueChange = { query = it }, placeholder = "Search", height = 48.dp)
    Spacer(Modifier.height(8.dp))
    var lastProvider = ""
    Content.models.filter {
        it.provider.contains(query, true) || it.name.contains(query, true)
    }.forEach { model ->
        if (model.provider != lastProvider) {
            lastProvider = model.provider
            Text(model.provider, style = T.Small, color = A0.Mu, modifier = Modifier.padding(top = 14.dp, bottom = 2.dp))
        }
        Row(
            Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier.weight(1f).a0Clickable { vm.setModel(model.name) }.padding(vertical = 10.dp)
            ) {
                Text(model.name, style = T.BodyMedium, color = A0.Ink)
                Text(model.meta, style = T.MonoSmall, color = A0.Mu)
            }
            Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                A0Icon("star", 20.dp, A0.Mu, 1.6f)
            }
        }
    }
    Spacer(Modifier.height(12.dp))
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.weight(1f)) {
            A0Field(value = custom, onValueChange = { custom = it }, placeholder = "provider/model-id", height = 48.dp)
        }
        Box(Modifier.width(96.dp)) {
            A0Button(label = "Add", onClick = { vm.addCustomModel(custom) })
        }
    }
}

@Composable
private fun AttachSheet(vm: AppViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Content.attachOptions.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { option ->
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color.White)
                            .a0Clickable {
                                vm.closeAll()
                                when (option.label) {
                                    "Website URL" -> { vm.go("chat"); vm.onPromptChange("Clone the design of https://") }
                                    "Voice" -> vm.toast("Listening…")
                                    else -> vm.attach(option.label)
                                }
                            }
                            .padding(vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PastelTile(option.icon, option.pastel, size = 48.dp, shape = RoundedCornerShape(18.dp))
                        Text(option.label, style = T.Caption, color = A0.ChipInk)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExportSheet(vm: AppViewModel) {
    Text("Export", style = T.H2.copy(fontSize = 28.sp), color = A0.Ink, modifier = Modifier.padding(bottom = 6.dp))
    Content.exports.forEach { item ->
        Row(
            Modifier
                .fillMaxWidth()
                .a0Clickable {
                    vm.closeAll()
                    vm.toast(
                        when {
                            item.startsWith("Figma") -> "Layers copied. Paste with an HTML-to-Figma plugin"
                            item.startsWith("Push") -> "Connect GitHub in Settings"
                            else -> "$item done"
                        }
                    )
                }
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(item, style = T.Body, color = A0.Ink, modifier = Modifier.weight(1f))
            A0Icon("chev", 18.dp, A0.Mu)
        }
    }
}

@Composable
private fun ActionSheet(vm: AppViewModel) {
    Text("Moss Coffee", style = T.H2.copy(fontSize = 26.sp), color = A0.Ink, modifier = Modifier.padding(bottom = 6.dp))
    listOf("Open", "Rename", "Duplicate", "Delete").forEach { action ->
        Row(
            Modifier
                .fillMaxWidth()
                .a0Clickable {
                    vm.closeAll()
                    when (action) {
                        "Open" -> vm.go("prev")
                        else -> vm.toast("$action done")
                    }
                }
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                action,
                style = T.Body,
                color = if (action == "Delete") Color(0xFFB3402E) else A0.Ink,
                modifier = Modifier.weight(1f)
            )
            A0Icon("chev", 18.dp, A0.Mu)
        }
    }
}

@Composable
private fun ListSheet(title: String, items: List<String>, onClick: (String) -> Unit) {
    Text(title, style = T.H2.copy(fontSize = 26.sp), color = A0.Ink, modifier = Modifier.padding(bottom = 6.dp))
    items.forEach { item ->
        Row(
            Modifier
                .fillMaxWidth()
                .a0Clickable { onClick(item) }
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(item, style = T.Body, color = A0.Ink, modifier = Modifier.weight(1f))
            A0Icon("chev", 18.dp, A0.Mu)
        }
    }
}

@Composable
private fun PromptSheet(vm: AppViewModel) {
    var text by remember { mutableStateOf(vm.systemPrompt) }
    Text("System prompt override", style = T.H2.copy(fontSize = 26.sp), color = A0.Ink, modifier = Modifier.padding(bottom = 10.dp))
    A0Field(
        value = text,
        onValueChange = { text = it },
        placeholder = "Always design with generous whitespace…",
        height = 120.dp,
        singleLine = false
    )
    Spacer(Modifier.height(14.dp))
    A0Button(label = "Save", onClick = { vm.systemPrompt = text; vm.closeAll(); vm.toast("System prompt saved") })
}
