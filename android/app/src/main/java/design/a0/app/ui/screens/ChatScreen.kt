package design.a0.app.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import design.a0.app.ui.comp.GlassIconButton
import design.a0.app.ui.comp.Segmented
import design.a0.app.ui.comp.a0Clickable
import design.a0.app.ui.comp.glass
import design.a0.app.ui.icon.A0Icon
import design.a0.app.ui.mascot.Mascot
import design.a0.app.ui.mascot.Mood
import design.a0.app.ui.theme.A0
import design.a0.app.ui.theme.T
import design.a0.app.vm.AppViewModel
import design.a0.app.vm.FeedItem
import design.a0.app.vm.FeedKind
import kotlinx.coroutines.delay

/**
 * Studio. Everything in the feed is a real event: a brief you typed, a request that
 * is actually in flight, a model error verbatim, a saved design, an audit result.
 */
@Composable
fun ChatScreen(vm: AppViewModel) {
    val listState = rememberLazyListState()

    LaunchedEffect(vm.feed.size, vm.feed.lastOrNull()?.step) {
        if (vm.feed.isNotEmpty()) listState.animateScrollToItem((vm.feed.size - 1).coerceAtLeast(0))
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 52.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassIconButton("menu", onClick = { vm.openDrawer() })
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Mascot(size = 38.dp, mood = vm.mood)
            }
            GlassIconButton("edit", onClick = { vm.newChat() })
        }

        Box(Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (vm.feed.isEmpty()) {
                    item { Spacer(Modifier.height(24.dp)) }
                    item { Hero(vm) }
                }
                items(vm.feed.size) { index -> FeedRow(vm.feed[index], vm) }
                item { Spacer(Modifier.height(12.dp)) }
            }
        }

        Composer(vm)
    }
}

@Composable
private fun Hero(vm: AppViewModel) {
    var bubble by remember { mutableIntStateOf(0) }
    val greeting = "Let's design something."
    LaunchedEffect(Unit) {
        while (bubble < greeting.length) {
            delay(45)
            bubble += 1
        }
    }
    Column {
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(bottom = 18.dp)) {
            Mascot(size = 104.dp, mood = vm.mood)
            Box(
                Modifier
                    .padding(start = 10.dp)
                    .glass(
                        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 6.dp),
                        alpha = 0.95f
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(greeting.take(bubble), style = T.BodyMedium, color = A0.Ink)
            }
        }
        Text("What shall we design today?", style = T.Display, color = A0.Ink, modifier = Modifier.padding(bottom = 16.dp))

        if (!vm.hasKey()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .glass(RoundedCornerShape(A0.RCard), alpha = 0.92f)
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        Modifier.size(36.dp).background(Color(0xFFEAF6D2), CircleShape),
                        contentAlignment = Alignment.Center
                    ) { A0Icon("key", 18.dp, A0.AccentDeep) }
                    Text("No API key yet", style = T.Card, color = A0.Ink)
                }
                Text(
                    "a0 has no model of its own. Add your OpenRouter, OpenAI or Anthropic key and " +
                        "the app will start designing for real — nothing is generated without it.",
                    style = T.Small,
                    color = A0.Mu,
                    modifier = Modifier.padding(top = 8.dp, bottom = 14.dp)
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(A0.RPill))
                        .background(Brush.linearGradient(listOf(A0.AccentLime, A0.AccentLime2)))
                        .a0Clickable { vm.go("key") },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Add API key", style = T.Button, color = A0.AccentInk)
                }
            }
        } else {
            Text(
                "Describe the product, the audience and the feeling. The app sends it to " +
                    "${vm.settings.model} with your key, then audits what comes back.",
                style = T.Small,
                color = A0.Mu
            )
        }
    }
}

@Composable
private fun FeedRow(item: FeedItem, vm: AppViewModel) {
    when (item.kind) {
        FeedKind.User -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text(
                item.text,
                style = T.Body,
                color = A0.AccentInk,
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .background(
                        Brush.linearGradient(listOf(Color(0xFFD9F7A0), Color(0xFFBFEB6C))),
                        RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomEnd = 6.dp, bottomStart = 22.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        FeedKind.Steps -> Row(verticalAlignment = Alignment.Top) {
            Mascot(size = 34.dp, mood = Mood.Think)
            Column(
                Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(A0.RCard))
                    .padding(16.dp)
            ) {
                listOf(
                    "Request sent to ${vm.settings.provider}",
                    "Model is designing",
                    "Validating tokens and copy",
                    "Saving version"
                ).forEachIndexed { i, label ->
                    val done = i < item.step
                    val active = i == item.step
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            Modifier
                                .size(22.dp)
                                .background(if (done) A0.Success else Color.Transparent, CircleShape)
                                .border(1.5.dp, if (done) A0.Success else A0.Hairline, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (done) A0Icon("check", 13.dp, Color.White, 2f)
                        }
                        Text(
                            label,
                            style = T.Small,
                            color = if (done || active) A0.Ink else A0.Mu
                        )
                        if (active) {
                            Spacer(Modifier.width(4.dp))
                            Pulse()
                        }
                    }
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .height(44.dp)
                        .border(1.dp, A0.Hairline, RoundedCornerShape(A0.RPill))
                        .a0Clickable { vm.stopGeneration() },
                    contentAlignment = Alignment.Center
                ) { Text("Stop", style = T.Button, color = A0.Ink) }
            }
        }

        FeedKind.Error -> Row(verticalAlignment = Alignment.Top) {
            Mascot(size = 34.dp, mood = Mood.Sad)
            Column(
                Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
                    .background(Color(0xFFFDF1EE), RoundedCornerShape(A0.RCard))
                    .border(1.dp, Color(0xFFF3D8D0), RoundedCornerShape(A0.RCard))
                    .padding(16.dp)
            ) {
                Text(item.text, style = T.Small, color = Color(0xFF8A3A28))
                if (item.action.isNotBlank()) {
                    Box(
                        Modifier
                            .padding(top = 12.dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(A0.RPill))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE9C6BC), RoundedCornerShape(A0.RPill))
                            .a0Clickable {
                                when (item.action) {
                                    "Add key", "Fix key" -> vm.go("key")
                                    else -> vm.retry()
                                }
                            }
                            .padding(horizontal = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item.action, style = T.ChipOn, color = Color(0xFF8A3A28))
                    }
                }
            }
        }

        FeedKind.Ready -> Row(verticalAlignment = Alignment.Top) {
            Mascot(size = 34.dp, mood = Mood.Happy)
            Column(
                Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(A0.RCard))
                    .padding(16.dp)
            ) {
                val spec = item.spec
                Text(
                    "${spec?.name ?: "Your design"} is ready — ${spec?.screens?.size ?: 0} screens, one token set.",
                    style = T.Body,
                    color = A0.InkSoft
                )
                if (!spec?.tagline.isNullOrBlank()) {
                    Text(spec.tagline, style = T.Small, color = A0.Mu, modifier = Modifier.padding(top = 4.dp))
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .glass(RoundedCornerShape(22.dp), alpha = 0.9f, elevation = 8.dp)
                        .a0Clickable { vm.go("preview") }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        Modifier
                            .size(width = 44.dp, height = 62.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        spec?.theme?.bg?.let { hexColor(it, Color(0xFFF6EFE4)) } ?: Color(0xFFF6EFE4),
                                        spec?.theme?.accent?.let { hexColor(it, Color(0xFFC0623B)) } ?: Color(0xFFC0623B)
                                    )
                                )
                            )
                    )
                    Column(Modifier.weight(1f)) {
                        Text("Open the design", style = T.Card, color = A0.Ink)
                        Text("Tap any block to edit it", style = T.Caption, color = A0.Mu)
                    }
                    A0Icon("chev", 18.dp, A0.Mu)
                }
            }
        }

        FeedKind.Agent -> Unit

        FeedKind.Agent -> Unit

        FeedKind.Audit -> Row(verticalAlignment = Alignment.Top) {
            Mascot(size = 34.dp, mood = Mood.Curious)
            Column(
                Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(A0.RCard))
                    .padding(16.dp)
            ) {
                Text("Anti-slop audit", style = T.Card, color = A0.Ink)
                Text(
                    "The model did not clear every rule. These are the real results:",
                    style = T.Caption, color = A0.Mu, modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                )
                item.findings.filterNot { it.ok }.forEach { finding ->
                    Row(Modifier.padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.size(8.dp).background(Color(0xFFD97757), CircleShape).align(Alignment.CenterVertically))
                        Text("${finding.title}: ${finding.detail}", style = T.Caption, color = A0.InkSoft)
                    }
                }
            }
        }
    }
}

@Composable
private fun Pulse() {
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), repeatMode = RepeatMode.Reverse),
        label = "alpha"
    )
    Box(Modifier.size(8.dp).background(A0.Accent.copy(alpha = alpha), CircleShape))
}

// ---------------------------------------------------------------------------
// Composer
// ---------------------------------------------------------------------------
@Composable
private fun Composer(vm: AppViewModel) {
    Column(
        Modifier
            .padding(horizontal = 14.dp)
            .padding(bottom = 14.dp)
            .imePadding()
            .glass(RoundedCornerShape(30.dp), alpha = 0.86f, elevation = 12.dp)
            .padding(start = 18.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)
    ) {
        if (vm.planStep >= 0) {
            PlanCard(vm)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(Modifier.weight(1f)) {
                    if (vm.prompt.isEmpty()) {
                        Text("Describe what you want to design", style = T.Body, color = A0.Mu, maxLines = 1)
                    }
                    BasicTextField(
                        value = vm.prompt,
                        onValueChange = { vm.onPromptChange(it) },
                        textStyle = T.Body.copy(color = A0.Ink),
                        cursorBrush = SolidColor(A0.Accent),
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { vm.send() }),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                    )
                }
            }

            Row(
                Modifier.fillMaxWidth().padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(Modifier.width(124.dp)) {
                    Segmented(
                        options = listOf("Auto", "Plan"),
                        selected = vm.mode,
                        height = 44.dp,
                        onSelect = { vm.mode = it }
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        Modifier
                            .a0Clickable { vm.openSheet("model") }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            vm.settings.model.substringAfterLast('/').take(18),
                            style = T.Small,
                            color = A0.Mu,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Box(
                        Modifier
                            .size(44.dp)
                            .shadow(8.dp, CircleShape, ambientColor = Color(0x557FCF2E), spotColor = Color(0x557FCF2E))
                            .background(Brush.linearGradient(listOf(A0.Accent2, Color(0xFF8FD33A))), CircleShape)
                            .a0Clickable { vm.send() },
                        contentAlignment = Alignment.Center
                    ) { A0Icon("up", 20.dp, A0.AccentInk, 1.9f) }
                }
            }
        }
    }
}

@Composable
private fun PlanCard(vm: AppViewModel) {
    val questions = listOf(
        "Colour mood" to listOf("Warm and earthy", "Monochrome", "Bold and vivid"),
        "Typography" to listOf("Clean sans", "Editorial serif", "Techy mono")
    )
    val index = vm.planStep.coerceIn(0, 1)
    val q = questions[index]
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(q.first, style = T.Card, color = A0.Ink)
            Text("${index + 1} / 2", style = T.Caption, color = A0.Mu)
        }
        Spacer(Modifier.height(10.dp))
        q.second.forEach { option ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .background(Color(0xFFF5F9F1), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFFE3EADB), RoundedCornerShape(16.dp))
                    .a0Clickable { vm.answerPlan(index, option) }
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(option, style = T.Small, color = A0.Ink)
            }
        }
        Text(
            "Let the model decide",
            style = T.Caption,
            color = A0.Mu,
            modifier = Modifier.padding(top = 2.dp).a0Clickable { vm.skipPlan() }
        )
    }
}

internal fun hexColor(value: String, fallback: Color): Color {
    val v = value.trim().removePrefix("#")
    if (v.length != 6) return fallback
    return runCatching { Color(0xFF000000.toInt() or v.toLong(16).toInt()) }.getOrDefault(fallback)
}
