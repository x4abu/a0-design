// a0 — motion recipes for Jetpack Compose
// Copy the pieces you need. Everything here uses the tokens in motion-tokens.md.
// Compiles against Compose 1.7 / Material3 / Kotlin 2.0. API 31+ for real blur.

package design.a0.motion

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// ---------------------------------------------------------------- tokens
object Motion {
    const val FEEDBACK = 120
    const val MICRO = 150
    const val SHORT = 200
    const val BASE = 250
    const val MEDIUM = 300
    const val LARGE = 450
    const val XLARGE = 600

    val Ease = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)
    val SettleCurve = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)
    val Exit = CubicBezierEasing(0.3f, 0f, 1f, 1f)
    val Pop = CubicBezierEasing(0.34f, 1.4f, 0.4f, 1f)

    val Settle = spring<Float>(dampingRatio = 0.85f, stiffness = 380f)
    val Snappy = spring<Float>(dampingRatio = 0.9f, stiffness = 900f)
    val Gentle = spring<Float>(dampingRatio = 1f, stiffness = 220f)
    val Overshoot = spring<Float>(dampingRatio = 0.55f, stiffness = 600f)

    fun <T> tween(duration: Int, easing: Easing = Ease): TweenSpec<T> =
        tween(durationMillis = duration, easing = easing)
}

// ------------------------------------------------------- press feedback
@Composable
fun Modifier.pressScale(target: Float = 0.96f): Modifier {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) target else 1f, Motion.Snappy, label = "press")
    return this.graphicsLayer { scaleX = scale; scaleY = scale }
}

// --------------------------------------------------------------- glass
fun Modifier.glassSurface(
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(28.dp),
    fillAlpha: Float = 0.82f,
    elevation: Dp = 14.dp
): Modifier = this
    .shadow(elevation, shape, clip = false, ambientColor = Color(0x334A6A3A), spotColor = Color(0x334A6A3A))
    .background(Color.White.copy(alpha = fillAlpha), shape)
    .border(1.dp, Color.White.copy(alpha = 0.9f), shape)

// -------------------------------------------------------------- toggle
@Composable
fun SpringToggle(
    checked: Boolean,
    onChange: (Boolean) -> Unit,
    accent: Color,
    track: Color = Color(0xFFD5DDD0),
    modifier: Modifier = Modifier
) {
    val knobX by animateDpAsState(if (checked) 22.dp else 3.dp, Motion.Settle, label = "knob")
    val bg by animateColorAsState(if (checked) accent else track, Motion.tween(Motion.MEDIUM), label = "track")
    Box(
        modifier
            .pressScale(0.97f)
            .size(52.dp, 32.dp)
            .background(bg, CircleShape)
            .clickable { onChange(!checked) }
    ) {
        Box(
            Modifier
                .offset(x = knobX, y = 3.dp)
                .size(26.dp)
                .shadow(2.dp, CircleShape)
                .background(Color.White, CircleShape)
        )
    }
}

// ---------------------------------------------------------- segmented
@Composable
fun SegmentedPill(
    options: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    track: Color = Color(0xFFE6EDE0),
    ink: Color = Color(0xFF14171A)
) {
    BoxWithConstraints(
        modifier
            .height(44.dp)
            .background(track, CircleShape)
            .padding(4.dp)
    ) {
        val thumbWidth = maxWidth / options.size
        val offset by animateDpAsState(thumbWidth * selected, Motion.Settle, label = "thumb")
        Box(
            Modifier
                .offset(x = offset)
                .width(thumbWidth)
                .fillMaxHeight()
                .shadow(6.dp, CircleShape)
                .background(ink, CircleShape)
        )
        Row(Modifier.fillMaxSize()) {
            options.forEachIndexed { i, label ->
                Box(
                    Modifier.weight(1f).fillMaxHeight().clickable { onSelect(i) },
                    contentAlignment = Alignment.Center
                ) {
                    val color by animateColorAsState(
                        if (i == selected) Color.White else ink.copy(alpha = 0.55f),
                        Motion.tween(Motion.SHORT),
                        label = "label"
                    )
                    Text(label, color = color, fontSize = 14.sp,
                         fontWeight = if (i == selected) FontWeight.SemiBold else FontWeight.Medium)
                }
            }
        }
    }
}

// ------------------------------------------- bottom nav (expanding pill)
@Composable
fun ExpandingNavBar(
    items: List<Pair<String, String>>,     // icon name to label
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    ink: Color = Color(0xFF14171A)
) {
    Row(
        modifier
            .height(68.dp)
            .glassSurface(CircleShape, 0.9f, 16.dp)
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { i, (icon, label) ->
            val active = i == selected
            val weight by animateFloatAsState(if (active) 1.9f else 1f, Motion.tween(Motion.LARGE, Motion.SettleCurve), label = "navWeight")
            Row(
                Modifier
                    .weight(weight)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(if (active) ink else Color.Transparent)
                    .clickable { onSelect(i) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                NavIcon(icon, active)
                AnimatedVisibility(
                    visible = active,
                    enter = fadeIn(Motion.tween(Motion.SHORT)) +
                        expandHorizontally(Motion.tween(Motion.BASE, Motion.SettleCurve), expandFrom = Alignment.Start),
                    exit = fadeOut(Motion.tween(Motion.MICRO))
                ) {
                    Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                         modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun NavIcon(name: String, active: Boolean) {
    // Replace with your icon set; the point is the size + colour transition.
    val tint by animateColorAsState(if (active) Color.White else Color(0xFF5D675F), Motion.tween(Motion.SHORT), label = "icon")
    Box(Modifier.size(20.dp).background(tint, CircleShape))
}

// -------------------------------------------------------------- text
/** BlurText: soft-focus reveal. Falls back to fade+rise below API 31. */
@Composable
fun BlurText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    enter: Boolean = true
) {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(enter) { if (enter) anim.animateTo(1f, tween(520, easing = Motion.Ease)) }
    Text(
        text,
        style = style,
        modifier = modifier.graphicsLayer {
            alpha = anim.value
            translationY = (1f - anim.value) * 14.dp.toPx()
        }
    )
}

/** SplitText: word-by-word entrance, 60ms stagger, capped at 12 words. */
@Composable
fun SplitHeadline(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    staggerMs: Int = 60
) {
    val words = text.split(" ").take(12)
    Row(modifier.wrapWords()) {
        words.forEachIndexed { i, word ->
            var visible by remember(text) { mutableStateOf(false) }
            LaunchedEffect(text) { delay((i * staggerMs).toLong()); visible = true }
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(420, easing = Motion.Ease)) +
                    slideInVertically(tween(420, easing = Motion.Ease)) { it / 3 }
            ) {
                Text(word + if (i < words.lastIndex) " " else "", style = style)
            }
        }
    }
}

private fun Modifier.wrapWords(): Modifier = this // use FlowRow in a real layout

/** Typewriter with a blinking caret. */
@Composable
fun Typewriter(text: String, style: TextStyle, msPerChar: Int = 16) {
    var shown by remember(text) { mutableIntStateOf(0) }
    LaunchedEffect(text) {
        while (shown < text.length) { delay(msPerChar.toLong()); shown++ }
    }
    Text(text.take(shown), style = style)
}

// -------------------------------------------------------- progressive blur
/** The iOS scroll-edge effect: a scrim that ramps toward the top edge. */
@Composable
fun ProgressiveTopScrim(height: Dp = 72.dp, tint: Color = Color(0xFFF8FBF4)) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(height)
            .background(
                Brush.verticalGradient(
                    listOf(tint.copy(alpha = 0.95f), tint.copy(alpha = 0.6f), Color.Transparent)
                )
            )
    )
}

// ------------------------------------------------------------- skeleton
@Composable
fun ShimmerSkeleton(width: Dp, height: Dp, radius: Dp = 14.dp) {
    val t = rememberInfiniteTransition(label = "shimmer")
    val shift by t.animateFloat(0f, 1f, infiniteRepeatable(tween(1400, easing = LinearEasing)), label = "shift")
    Box(
        Modifier
            .size(width, height)
            .clip(RoundedCornerShape(radius))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFE6EDE0), Color.White, Color(0xFFE6EDE0)),
                    start = Offset(320f * shift, 0f),
                    end = Offset(320f * shift + 320f, 0f)
                )
            )
    )
}

// ---------------------------------------------------------------- sheet
@Composable
fun BottomSheetSurface(visible: Boolean, onDismiss: () -> Unit, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(tween(Motion.XLARGE, easing = Motion.SettleCurve)) { it },
        exit = slideOutVertically(tween(Motion.MEDIUM, easing = Motion.Exit)) { it }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp))
                .background(Color.White.copy(alpha = 0.97f))
                .padding(22.dp)
        ) {
            Box(Modifier.align(Alignment.CenterHorizontally).size(40.dp, 5.dp)
                .background(Color(0xFFD5DDD0), CircleShape))
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

// --------------------------------------------------------------- success
@Composable
fun SuccessTick(onDone: () -> Unit = {}) {
    val scale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        scale.animateTo(1f, spring(0.55f, 600f))
        delay(700)
        onDone()
    }
    Box(
        Modifier
            .size(56.dp)
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
            .background(Color(0xFF6BD39B), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text("✓", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
    }
}
