package design.a0.app.ui.mascot

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** The moods the prototype drives through `data-m`. */
enum class Mood { Idle, Happy, Love, Think, Typing, Curious, Sad, Listen, Wow, Sleep }

/**
 * a0's mascot — a glossy lime sphere with two bar eyes, drawn entirely with
 * Canvas primitives so it scales to any size and stays crisp.
 *
 * Motion inventory (matches the reference CSS keyframes):
 *   bob    4s loop, -6% Y            bl     5s blink (93% → 96% → 100%)
 *   jp     happy jump               dt     think eye sweep
 *   curious rotate -9deg            sad    tilt + desaturate
 *   typing eyes drop                listen/wow taller eyes
 *   sleep  thin eyes + slow breathe  fx     heart / star particles
 */
@Composable
fun Mascot(
    size: Dp = 104.dp,
    mood: Mood = Mood.Idle,
    modifier: Modifier = Modifier,
    gazeX: Float = 0f,
    gazeY: Float = 0f
) {
    val transition = rememberInfiniteTransition(label = "mascot")

    val bob by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )
    val blink by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 5000
                1f at 0
                1f at 4650
                0.08f at 4800
                1f at 5000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "blink"
    )
    val sweep by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sweep"
    )

    // particle burst on love / happy
    val burst = remember { Animatable(1f) }
    val burstKind = remember { arrayOfNulls<String>(1) }
    LaunchedEffect(mood) {
        when (mood) {
            Mood.Love -> { burstKind[0] = "heart"; burst.snapTo(0f); burst.animateTo(1f, tween(1300, easing = LinearEasing)) }
            Mood.Happy -> { burstKind[0] = "star"; burst.snapTo(0f); burst.animateTo(1f, tween(1300, easing = LinearEasing)) }
            else -> Unit
        }
    }

    val floatY = when (mood) {
        Mood.Sad -> size.value * 0.04f
        Mood.Typing -> 0f
        else -> -size.value * 0.06f * bob
    }
    val rotation = when (mood) {
        Mood.Curious -> -9f
        else -> 0f
    }
    val sx = when (mood) {
        Mood.Typing -> 1.04f
        Mood.Sad -> 0.96f
        else -> 1f
    }

    Box(modifier.size(size)) {
        Canvas(
            Modifier
                .size(size)
                .graphicsLayer {
                    translationY = floatY.dp.toPx()
                    rotationZ = rotation
                    scaleX = sx
                }
        ) {
            val s = this.size.minDimension
            drawMascot(s, mood, blink, sweep, gazeX, gazeY)
        }
        Canvas(Modifier.size(size)) {
            drawBurst(this.size.minDimension, burst.value, burstKind[0])
        }
    }
}

private fun DrawScope.drawMascot(
    s: Float,
    mood: Mood,
    blink: Float,
    sweep: Float,
    gazeX: Float,
    gazeY: Float
) {
    val cx = s / 2f
    val cy = s / 2f
    val r = s * 0.46f
    val sleeping = mood == Mood.Sleep

    // ground shadow (::before in the reference)
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x666FC12A), Color.Transparent),
            center = Offset(cx, s * 0.96f),
            radius = r * 0.9f
        ),
        topLeft = Offset(cx - r * 0.72f, s * 0.9f),
        size = Size(r * 1.44f, r * 0.28f)
    )

    // sphere body
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFDDFF8E), Color(0xFF8FD63A), Color(0xFF6FC12A)),
            center = Offset(cx - r * 0.36f, cy - r * 0.48f),
            radius = r * 1.7f
        ),
        radius = r,
        center = Offset(cx, cy)
    )
    // warm bottom bounce
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xCCEEFFA8), Color(0x66B9EF52), Color.Transparent),
            center = Offset(cx, cy + r * 1.1f),
            radius = r * 0.95f
        ),
        radius = r,
        center = Offset(cx, cy)
    )
    // rim light
    drawCircle(
        brush = Brush.linearGradient(
            colors = listOf(Color.White.copy(alpha = 0.5f), Color.Transparent),
            start = Offset(cx - r, cy - r),
            end = Offset(cx + r, cy + r)
        ),
        radius = r,
        center = Offset(cx, cy),
        style = Stroke(width = s * 0.012f)
    )

    // specular highlight
    rotate(degrees = -24f, pivot = Offset(cx - r * 0.4f, cy - r * 0.62f)) {
        drawOval(
            brush = Brush.verticalGradient(
                colors = listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0f))
            ),
            topLeft = Offset(cx - r * 0.72f, cy - r * 0.78f),
            size = Size(r * 0.68f, r * 0.4f)
        )
    }

    // cheeks (only when happy / love)
    if (mood == Mood.Happy || mood == Mood.Love) {
        val cheekAlpha = 0.9f
        listOf(cx - r * 0.62f, cx + r * 0.62f).forEach { x ->
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFB4B4).copy(alpha = cheekAlpha), Color.Transparent),
                    center = Offset(x, cy + r * 0.34f),
                    radius = r * 0.34f
                ),
                radius = r * 0.34f,
                center = Offset(x, cy + r * 0.34f)
            )
        }
    }

    // eyes
    val eyeW = s * 0.085f
    val eyeH = when (mood) {
        Mood.Listen, Mood.Wow -> s * 0.27f
        Mood.Curious -> s * 0.25f
        Mood.Sleep -> s * 0.03f
        else -> s * 0.21f
    }
    val eyeGap = s * 0.17f
    val eyeY = cy - s * 0.10f + when (mood) {
        Mood.Typing -> s * 0.07f
        Mood.Sleep -> s * 0.05f
        else -> 0f
    }
    val driftX = when (mood) {
        Mood.Think -> sweep * s * 0.06f
        Mood.Curious -> s * 0.04f
        else -> gazeX * s * 0.03f
    }
    val driftY = when (mood) {
        Mood.Think -> -s * 0.03f
        Mood.Curious -> -s * 0.04f
        else -> gazeY * s * 0.02f
    }

    val eyeColor = Color(0xFF16240C)
    val arcEyes = mood == Mood.Happy || mood == Mood.Love

    listOf(-1f, 1f).forEachIndexed { index, side ->
        val ex = cx + side * eyeGap + driftX
        val ey = eyeY + driftY
        if (arcEyes) {
            // closed, smiling arcs
            val path = Path().apply {
                moveTo(ex - s * 0.075f, ey + s * 0.02f)
                quadraticBezierTo(ex, ey - s * 0.075f, ex + s * 0.075f, ey + s * 0.02f)
            }
            drawPath(path, eyeColor, style = Stroke(width = s * 0.035f, cap = StrokeCap.Round))
        } else {
            val h = eyeH * blink
            val w = eyeW
            drawRoundRect(
                color = eyeColor,
                topLeft = Offset(ex - w / 2f, ey - h / 2f),
                size = Size(w, h),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(w / 2f, w / 2f)
            )
        }
        if (mood == Mood.Sad) {
            rotate(degrees = if (index == 0) 14f else -14f, pivot = Offset(ex, ey)) {
                drawRoundRect(
                    color = eyeColor,
                    topLeft = Offset(ex - eyeW / 2f, ey - eyeH / 2f),
                    size = Size(eyeW, eyeH),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(eyeW / 2f, eyeW / 2f)
                )
            }
        }
    }

    // mouth (speaking bar)
    if (mood == Mood.Happy || mood == Mood.Love) {
        val path = Path().apply {
            moveTo(cx - s * 0.06f, cy + r * 0.34f)
            quadraticBezierTo(cx, cy + r * 0.46f, cx + s * 0.06f, cy + r * 0.34f)
        }
        drawPath(path, eyeColor, style = Stroke(width = s * 0.025f, cap = StrokeCap.Round))
    }

    // sleep z's
    if (sleeping) {
        drawCircle(Color(0xFFB79A94).copy(alpha = 0.5f), radius = s * 0.012f, center = Offset(cx + r * 0.9f, cy - r * 0.8f))
        drawCircle(Color(0xFFB79A94).copy(alpha = 0.35f), radius = s * 0.008f, center = Offset(cx + r * 1.05f, cy - r * 0.95f))
    }
}

private fun DrawScope.drawBurst(s: Float, progress: Float, kind: String?) {
    if (kind == null || progress <= 0f || progress >= 1f) return
    val seeds = listOf(0.22f, 0.38f, 0.58f, 0.74f)
    seeds.forEachIndexed { i, seed ->
        val t = (progress - i * 0.05f).coerceIn(0f, 1f)
        if (t <= 0f) return@forEachIndexed
        val x = s * seed + (if (i % 2 == 0) 1 else -1) * s * 0.06f * t
        val y = s * 0.18f - s * 0.5f * t
        val alpha = (1f - t).coerceIn(0f, 1f)
        val radius = s * 0.035f * (0.6f + t * 0.7f)
        if (kind == "heart") {
            drawCircle(Color(0xFFFF8FB8).copy(alpha = alpha), radius, Offset(x, y))
            drawCircle(Color(0xFFFF8FB8).copy(alpha = alpha), radius, Offset(x + radius * 1.1f, y))
            drawPath(
                Path().apply {
                    moveTo(x - radius * 0.9f, y + radius * 0.35f)
                    lineTo(x + radius * 1.9f, y + radius * 0.35f)
                    lineTo(x + radius * 0.5f, y + radius * 1.9f)
                    close()
                },
                Color(0xFFFF8FB8).copy(alpha = alpha)
            )
        } else {
            drawPath(
                Path().apply {
                    val cxx = x; val cyy = y
                    moveTo(cxx, cyy - radius * 1.8f)
                    lineTo(cxx + radius * 0.5f, cyy - radius * 0.4f)
                    lineTo(cxx + radius * 1.8f, cyy)
                    lineTo(cxx + radius * 0.5f, cyy + radius * 0.4f)
                    lineTo(cxx, cyy + radius * 1.8f)
                    lineTo(cxx - radius * 0.5f, cyy + radius * 0.4f)
                    lineTo(cxx - radius * 1.8f, cyy)
                    lineTo(cxx - radius * 0.5f, cyy - radius * 0.4f)
                    close()
                },
                Color(0xFFFFD166).copy(alpha = alpha)
            )
        }
    }
}
