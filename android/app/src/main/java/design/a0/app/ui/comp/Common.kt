package design.a0.app.ui.comp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import design.a0.app.ui.icon.A0Icon
import design.a0.app.ui.theme.A0
import design.a0.app.ui.theme.T

// ---------------------------------------------------------------------------
// Surfaces
// ---------------------------------------------------------------------------

/** The page wash: warm off-white → pale green with three soft radial glows. */
@Composable
fun A0Background(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(A0.BgTop, A0.BgBottom)))
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFD6F59A).copy(alpha = 0.85f), Color.Transparent),
                        center = Offset(90f, -40f),
                        radius = 900f
                    )
                )
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.9f), Color.Transparent),
                        center = Offset(1200f, 700f),
                        radius = 700f
                    )
                )
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFC9EDA8).copy(alpha = 0.75f), Color.Transparent),
                        center = Offset(120f, 2100f),
                        radius = 800f
                    )
                )
        )
        content()
    }
}

/** Frosted white surface used by every floating element (.g in the prototype). */
fun Modifier.glass(
    shape: Shape = RoundedCornerShape(A0.RCard),
    alpha: Float = 0.8f,
    elevation: Dp = 14.dp
): Modifier = this
    .shadow(elevation, shape, clip = false, ambientColor = Color(0x334A6A3A), spotColor = Color(0x334A6A3A))
    .background(Color.White.copy(alpha = alpha), shape)
    .border(1.dp, Color.White, shape)

/** Press feedback: scale 0.96 on press, exactly like `button:active`. */
@Composable
fun Modifier.pressScale(scaleDown: Float = 0.96f): Modifier {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) scaleDown else 1f, tween(A0.D_SHORT, easing = A0.Ease))
    return this.graphicsLayer { scaleX = scale; scaleY = scale }
}

@Composable
fun Modifier.a0Clickable(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier {
    val source = remember { MutableInteractionSource() }
    return this.clickable(interactionSource = source, indication = null, enabled = enabled, onClick = onClick)
}

// ---------------------------------------------------------------------------
// Buttons & chips
// ---------------------------------------------------------------------------

@Composable
fun A0Button(
    label: String,
    modifier: Modifier = Modifier,
    icon: String? = null,
    primary: Boolean = true,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(A0.RPill)
    val bg = if (primary) {
        Brush.linearGradient(listOf(A0.AccentLime, A0.AccentLime2))
    } else {
        Brush.linearGradient(listOf(Color.White, Color.White))
    }
    Box(
        modifier
            .fillMaxWidth()
            .height(54.dp)
            .pressScale()
            .a0Clickable(enabled = enabled, onClick = onClick)
            .then(
                if (primary) Modifier.shadow(12.dp, shape, ambientColor = Color(0x557FCF2E), spotColor = Color(0x557FCF2E))
                else Modifier.border(1.dp, A0.Hairline, shape)
            )
            .background(bg, shape),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (icon != null) A0Icon(icon, 18.dp, if (primary) A0.AccentInk else A0.Ink, 1.8f)
            Text(
                label,
                style = T.Button,
                color = if (primary) A0.AccentInk else A0.Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** 44dp round glass icon button used across every top bar. */
@Composable
fun GlassIconButton(
    icon: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    tint: Color = A0.Ink,
    onClick: () -> Unit
) {
    Box(
        modifier
            .size(size)
            .pressScale(0.94f)
            .a0Clickable(onClick = onClick)
            .glass(CircleShape, alpha = 0.85f, elevation = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        A0Icon(icon, size * 0.5f, tint)
    }
}

@Composable
fun A0Chip(
    label: String,
    modifier: Modifier = Modifier,
    pastel: Int? = null,
    selected: Boolean = false,
    height: Dp = 44.dp,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(A0.RPill)
    val p = pastel?.let { A0.pastel(it) }
    val bg: Brush = when {
        selected -> SolidColor(A0.Ink)
        p != null -> Brush.linearGradient(listOf(p.start, p.end))
        else -> SolidColor(Color.White)
    }
    val ink = when {
        selected -> Color.White
        p != null -> p.ink
        else -> A0.ChipInk
    }
    Box(
        modifier
            .height(height)
            .then(if (onClick != null) Modifier.pressScale().a0Clickable(onClick = onClick) else Modifier)
            .background(bg, shape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = if (selected) T.ChipOn else T.Chip,
            color = ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

/** 40/44dp tinted icon tile (settings rows, drawer rows, attach grid). */
@Composable
fun PastelTile(
    icon: String,
    pastel: Int,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    shape: Shape = RoundedCornerShape(A0.RTile)
) {
    val p = A0.pastel(pastel)
    Box(
        modifier
            .size(size)
            .background(Brush.linearGradient(listOf(p.start, p.end)), shape),
        contentAlignment = Alignment.Center
    ) {
        A0Icon(icon, size * 0.5f, A0.Ink.copy(alpha = 0.82f), 1.6f)
    }
}

// ---------------------------------------------------------------------------
// Segmented control — track + spring-sliding thumb (the app's signature control)
// ---------------------------------------------------------------------------

@Composable
fun Segmented(
    options: List<String>,
    selected: Int,
    modifier: Modifier = Modifier,
    height: Dp = 44.dp,
    onSelect: (Int) -> Unit
) {
    BoxWithConstraints(
        modifier
            .height(height)
            .background(A0.SegTrack, RoundedCornerShape(A0.RPill))
            .padding(4.dp)
    ) {
        val trackWidth = maxWidth
        val thumbWidth = trackWidth / options.size
        val offset by animateDpAsState(
            targetValue = thumbWidth * selected,
            animationSpec = tween(A0.D_SHEET, easing = A0.Pill),
            label = "thumb"
        )
        Box(
            Modifier
                .offset(x = offset)
                .width(thumbWidth)
                .height(height - 8.dp)
                .shadow(6.dp, RoundedCornerShape(A0.RPill), ambientColor = Color(0x33000000), spotColor = Color(0x33000000))
                .background(A0.Ink, RoundedCornerShape(A0.RPill))
        )
        Row(Modifier.fillMaxSize()) {
            options.forEachIndexed { i, label ->
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .a0Clickable { onSelect(i) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        label,
                        style = if (i == selected) T.ChipOn else T.Chip,
                        color = if (i == selected) Color.White else A0.SegOff,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Toggle
// ---------------------------------------------------------------------------

@Composable
fun A0Toggle(checked: Boolean, onChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val knob by animateDpAsState(if (checked) 22.dp else 3.dp, tween(A0.D_LONG, easing = A0.Spring), label = "knob")
    val bg by androidx.compose.animation.animateColorAsState(
        if (checked) A0.Accent else A0.Track, tween(A0.D_MED), label = "toggleBg"
    )
    Box(
        modifier
            .size(width = 52.dp, height = 32.dp)
            .background(bg, RoundedCornerShape(A0.RPill))
            .a0Clickable { onChange(!checked) }
    ) {
        Box(
            Modifier
                .offset(x = knob, y = 3.dp)
                .size(26.dp)
                .shadow(3.dp, CircleShape)
                .background(Color.White, CircleShape)
        )
    }
}

// ---------------------------------------------------------------------------
// Field
// ---------------------------------------------------------------------------

@Composable
fun A0Field(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    height: Dp = 56.dp,
    mono: Boolean = false,
    password: Boolean = false,
    singleLine: Boolean = true,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
    trailing: (@Composable () -> Unit)? = null
) {
    var focused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(A0.RField)
    Row(
        modifier
            .fillMaxWidth()
            .height(height)
            .background(Color.White, shape)
            .border(
                if (focused) 1.dp else 1.dp,
                if (focused) A0.Accent else Color(0xFFE1E8DB),
                shape
            )
            .padding(start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(placeholder, style = if (mono) T.Mono else T.Body, color = A0.Mu, maxLines = 1)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                textStyle = (if (mono) T.Mono else T.Body).copy(color = A0.Ink),
                cursorBrush = SolidColor(A0.Accent),
                visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (password) KeyboardType.Password else KeyboardType.Text,
                    imeAction = imeAction
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onImeAction() },
                    onGo = { onImeAction() },
                    onSend = { onImeAction() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focused = it.isFocused }
            )
        }
        trailing?.invoke()
    }
}

// ---------------------------------------------------------------------------
// Small text helpers
// ---------------------------------------------------------------------------

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(text, style = T.Small, color = A0.Mu, modifier = modifier.padding(top = 26.dp, bottom = 10.dp))
}

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    onBack: (() -> Unit)? = null,
    left: (@Composable () -> Unit)? = null,
    right: (@Composable () -> Unit)? = null,
    center: (@Composable () -> Unit)? = null
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 52.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when {
            left != null -> left()
            onBack != null -> GlassIconButton("back", onClick = onBack)
            else -> Spacer(Modifier.width(44.dp))
        }
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            when {
                center != null -> center()
                title != null -> Text(title, style = T.Card, color = A0.Ink)
            }
        }
        if (right != null) right() else Spacer(Modifier.width(44.dp))
    }
}

// ---------------------------------------------------------------------------
// Toast — drops from the top, 2.2s, exactly like the prototype
// ---------------------------------------------------------------------------

@Composable
fun ToastHost(message: String?, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = message != null,
        enter = slideInVertically(animationSpec = tween(A0.D_SHEET, easing = A0.Pop)) { -it } + fadeIn(tween(A0.D_MED)),
        exit = slideOutVertically(animationSpec = tween(A0.D_MED, easing = A0.Ease)) { -it } + fadeOut(tween(A0.D_SHORT)),
        modifier = modifier
    ) {
        Box(
            Modifier
                .padding(top = 56.dp)
                .glass(RoundedCornerShape(A0.RPill), alpha = 0.92f, elevation = 16.dp)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(message ?: "", style = T.SmallMedium, color = A0.Ink, maxLines = 1)
        }
    }
}
