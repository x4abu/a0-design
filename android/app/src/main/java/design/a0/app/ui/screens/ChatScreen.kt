package design.a0.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import design.a0.app.data.Content
import design.a0.app.ui.comp.A0Chip
import design.a0.app.ui.comp.GlassIconButton
import design.a0.app.ui.comp.a0Clickable
import design.a0.app.ui.comp.glass
import design.a0.app.ui.comp.pressScale
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
 * Studio (chat) screen — hero, live feed and composer.
 * Everything here is the prototype's #chat section, rebuilt in Compose.
 */
@Composable
fun ChatScreen(vm: AppViewModel, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()

    LaunchedEffect(vm.feed.size, vm.feed.lastOrNull()?.stepIndex) {
        if (vm.feed.isNotEmpty()) listState.animateScrollToItem((vm.feed.size - 1).coerceAtLeast(0))
    }

    Column(modifier.fillMaxSize()) {
        // ---- top bar ---------------------------------------------------
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

        // ---- feed ------------------------------------------------------
        Box(Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (vm.emptyChat) {
                    item { Spacer(Modifier.height(20.dp)) }
                    item { Hero(vm) }
                }
                items(vm.feed.size) { index ->
                    FeedRow(vm.feed[index], vm)
                }
                item { Spacer(Modifier.height(12.dp)) }
            }
        }

        Composer(vm)
    }
}

@Composable
private fun Hero(vm: AppViewModel) {
    var bubble by remember { mutableIntStateOf(0) }
    val greeting = remember {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good morning!"
            hour < 18 -> "Good afternoon!"
            else -> "Good evening!"
        }
    }
    LaunchedEffect(Unit) {
        while (bubble < greeting.length) {
            delay(50)
            bubble += 1
        }
    }
    Column {
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(bottom = 18.dp)) {
            Mascot(size = 104.dp, mood = vm.mood)
            Box(
                Modifier
                    .padding(start = 10.dp)
                    .glass(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 6.dp), alpha = 0.95f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(greeting.take(bubble), style = T.BodyMedium, color = A0.Ink)
            }
        }
        Text("What shall we design today?", style = T.Display, color = A0.Ink, modifier = Modifier.padding(bottom = 20.dp))
        Content.suggestions.forEachIndexed { i, s ->
            SuggestionRow(s, i) { vm.send(s) }
        }
    }
}

@Composable
private fun SuggestionRow(text: String, index: Int, onClick: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(900L + index * 90L); visible = true }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)) + slideInVertically(tween(500, easing = A0.Ease)) { it / 3 }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .pressScale(0.98f)
                .a0Clickable(onClick = onClick)
                .glass(RoundedCornerShape(20.dp), alpha = 0.9f, elevation = 8.dp)
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (text.length > 40) text.take(38) + "…" else text,
                style = T.Body, color = A0.InkSoft, maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            A0Icon("chev", 18.dp, A0.Mu)
        }
    }
}

@Composable
private fun FeedRow(item: FeedItem, vm: AppViewModel) {
    when (item.kind) {
        FeedKind.User -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Column(
                Modifier
                    .widthIn(max = 300.dp)
                    .background(
                        Brush.linearGradient(listOf(Color(0xFFD9F7A0), Color(0xFFBFEB6C))),
                        RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomEnd = 6.dp, bottomStart = 22.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(item.text, style = T.Body, color = A0.AccentInk)
                if (item.attachments.isNotEmpty()) {
                    Text(
                        item.attachments.joinToString(" · "),
                        style = T.Caption, color = A0.AccentInk.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        FeedKind.Agent -> Row(verticalAlignment = Alignment.Top) {
            Mascot(size = 34.dp, mood = if (item.text == "Thinking…") Mood.Think else Mood.Idle)
            Box(Modifier.padding(start = 10.dp).weight(1f)) {
                if (item.text == "Thinking…") Shimmer(width = 200.dp, height = 46.dp)
                else Typewriter(item.text)
            }
        }

        FeedKind.Updated -> Row(verticalAlignment = Alignment.Top) {
            Mascot(size = 34.dp, mood = Mood.Happy)
            Column(
                Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(A0.RCard))
                    .padding(16.dp)
            ) {
                Typewriter(item.text)
                ProjectRow(vm)
            }
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
                Content.generationSteps.forEachIndexed { i, step ->
                    val done = i < item.stepIndex
                    val active = i == item.stepIndex
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .graphicsLayer { alpha = if (done || active) 1f else 0.35f },
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
                        Text(step, style = T.Small, color = A0.Ink)
                    }
                }
                val note = Content.generationNotes[item.stepIndex.coerceIn(0, Content.generationNotes.lastIndex)]
                Text(note, style = T.Caption, color = A0.Mu, modifier = Modifier.padding(top = 6.dp))
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

        FeedKind.Ready -> Row(verticalAlignment = Alignment.Top) {
            Mascot(size = 34.dp, mood = Mood.Happy)
            Column(
                Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(A0.RCard))
                    .padding(16.dp)
            ) {
                Typewriter(item.text)
                ProjectRow(vm)
            }
        }

        FeedKind.Stopped -> Row(verticalAlignment = Alignment.Top) {
            Mascot(size = 34.dp, mood = Mood.Sad)
            Box(
                Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(A0.RCard))
                    .padding(16.dp)
            ) { Text("Generation stopped.", style = T.Body, color = A0.Mu) }
        }

        FeedKind.Plan -> PlanCard(item, vm)
    }
}

@Composable
private fun ProjectRow(vm: AppViewModel) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .pressScale(0.98f)
            .a0Clickable { vm.go("prev") }
            .glass(RoundedCornerShape(22.dp), alpha = 0.9f, elevation = 8.dp)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            Modifier
                .size(width = 54.dp, height = 76.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(Color(0xFFF6EFE4), Color(0xFFD8B98A), Color(0xFFC0623B))))
        )
        Column(Modifier.weight(1f)) {
            Text("Moss Coffee", style = T.Card, color = A0.Ink)
            Text("Open preview", style = T.Caption, color = A0.Mu)
        }
        A0Icon("chev", 18.dp, A0.Mu)
    }
}

@Composable
private fun PlanCard(item: FeedItem, vm: AppViewModel) {
    val questions = listOf(
        "Colour mood" to listOf("Warm and earthy", "Monochrome", "Bold and vivid"),
        "Typography" to listOf("Clean sans", "Editorial serif", "Techy mono")
    )
    Row(verticalAlignment = Alignment.Top) {
        Mascot(size = 34.dp, mood = Mood.Curious)
        Column(
            Modifier
                .padding(start = 10.dp)
                .weight(1f)
                .background(Color.White, RoundedCornerShape(A0.RCard))
                .padding(14.dp)
        ) {
            Text(item.text, style = T.Body, color = A0.Ink)
            Spacer(Modifier.height(10.dp))
            if (!item.planReady) {
                val q = questions[item.planStep.coerceIn(0, 1)]
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(q.first, style = T.Card, color = A0.Ink)
                    Text("${item.planStep + 1} / ${questions.size}", style = T.Caption, color = A0.Mu)
                }
                Spacer(Modifier.height(12.dp))
                q.second.forEachIndexed { i, option ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .background(Color(0xFFF5F9F1), RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFFE3EADB), RoundedCornerShape(16.dp))
                            .a0Clickable { vm.answerPlan(item.planStep, option) }
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            Modifier
                                .size(24.dp)
                                .background(Color(0xFFE6EFDD), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("ABC"[i].toString(), fontSize = 12.sp, color = A0.AccentDeep)
                        }
                        Text(option, style = T.Small, color = A0.Ink)
                    }
                }
                Text(
                    "Let AI decide",
                    style = T.Caption,
                    color = A0.Mu,
                    modifier = Modifier.padding(top = 4.dp).a0Clickable { vm.planDecideForMe(item.planStep) }
                )
            } else {
                listOf(
                    "Colour" to (item.answers.getOrNull(0) ?: "AI decides"),
                    "Type" to (item.answers.getOrNull(1) ?: "AI decides"),
                    "Screens" to "Home · Brew · Shop",
                    "Components" to "Cards, pills, nav bar"
                ).forEach { (k, v) ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 11.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(k, style = T.Small, color = A0.Mu)
                        Text(v, style = T.SmallMedium, color = A0.Ink)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Brush.linearGradient(listOf(A0.AccentLime, A0.AccentLime2)), RoundedCornerShape(A0.RPill))
                        .a0Clickable { vm.confirmPlan() },
                    contentAlignment = Alignment.Center
                ) { Text("Design it", style = T.Button, color = A0.AccentInk) }
            }
        }
    }
}

/** Character-by-character reveal used for every assistant message. */
@Composable
fun Typewriter(text: String) {
    var shown by remember(text) { mutableIntStateOf(0) }
    LaunchedEffect(text) {
        while (shown < text.length) {
            delay(14)
            shown += 1
        }
    }
    Text(text.take(shown), style = T.Body, color = A0.InkSoft)
}

@Composable
fun Shimmer(width: androidx.compose.ui.unit.Dp, height: androidx.compose.ui.unit.Dp) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400), repeatMode = RepeatMode.Restart),
        label = "shift"
    )
    Box(
        Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFE6EDE0), Color.White, Color(0xFFE6EDE0)),
                    start = androidx.compose.ui.geometry.Offset(300f * shift, 0f),
                    end = androidx.compose.ui.geometry.Offset(300f * shift + 300f, 0f)
                )
            )
    )
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
        if (vm.attachments.isNotEmpty()) {
            Row(
                Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                vm.attachments.take(3).forEachIndexed { i, label ->
                    Row(
                        Modifier
                            .background(A0.SegTrack, RoundedCornerShape(A0.RPill))
                            .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(label, style = T.Caption, color = A0.Ink)
                        Box(
                            Modifier.size(24.dp).a0Clickable { vm.removeAttachment(i) },
                            contentAlignment = Alignment.Center
                        ) { A0Icon("x", 13.dp, A0.Mu) }
                    }
                }
            }
        }

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
            if (vm.prompt.isNotBlank()) {
                Box(
                    Modifier
                        .size(44.dp)
                        .background(Color(0xFFEAF6D2), CircleShape)
                        .a0Clickable { vm.enhancePrompt() },
                    contentAlignment = Alignment.Center
                ) { A0Icon("spark", 20.dp, A0.AccentDeep) }
            }
        }

        Row(
            Modifier.fillMaxWidth().padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    Modifier
                        .size(44.dp)
                        .background(Color(0xFFEAF0E4), CircleShape)
                        .a0Clickable { vm.openSheet("attach") },
                    contentAlignment = Alignment.Center
                ) { A0Icon("plus", 20.dp, A0.Ink) }
                Box(Modifier.width(124.dp)) {
                    design.a0.app.ui.comp.Segmented(
                        options = Content.composerModes,
                        selected = vm.mode,
                        height = 44.dp,
                        onSelect = { vm.mode = it }
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    Modifier
                        .a0Clickable { vm.openSheet("model") }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) { Text(vm.modelName, style = T.Small, color = A0.Mu) }
                val ready = vm.prompt.isNotBlank()
                Box(
                    Modifier
                        .size(44.dp)
                        .shadow(8.dp, CircleShape, ambientColor = Color(0x557FCF2E), spotColor = Color(0x557FCF2E))
                        .background(Brush.linearGradient(listOf(A0.Accent2, Color(0xFF8FD33A))), CircleShape)
                        .a0Clickable { if (ready) vm.send() else vm.toast("Listening…") },
                    contentAlignment = Alignment.Center
                ) { A0Icon(if (ready) "up" else "mic", 20.dp, A0.AccentInk, 1.9f) }
            }
        }
    }
}
