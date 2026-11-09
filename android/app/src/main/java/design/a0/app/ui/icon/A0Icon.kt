package design.a0.app.ui.icon

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import design.a0.app.ui.theme.A0

/**
 * Renders one of the prototype's 24x24 stroke icons at any size, keeping the
 * original geometry and a proportional 1.6/22dp stroke.
 */
@Composable
fun A0Icon(
    name: String,
    size: Dp = 22.dp,
    tint: Color = A0.Ink,
    strokeWidth: Float = 1.6f,
    filled: Boolean = false,
    fillColor: Color? = null,
    modifier: Modifier = Modifier
) {
    val d = A0Paths.all[name] ?: return
    val path: Path = remember(d) { PathParser().parsePathString(d).toPath() }
    Canvas(modifier.size(size)) {
        val s = this.size.minDimension / 24f
        scale(s, s, pivot = Offset.Zero) {
            if (filled && fillColor != null) drawPath(path, color = fillColor)
            drawPath(
                path = path,
                color = tint,
                style = Stroke(
                    width = strokeWidth.dp.toPx() / s,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

/** A filled glyph (star toggle, success ticks, brand marks). */
@Composable
fun A0IconFilled(
    name: String,
    size: Dp = 22.dp,
    tint: Color = A0.Ink,
    modifier: Modifier = Modifier
) {
    val d = A0Paths.all[name] ?: return
    val path: Path = remember(d) { PathParser().parsePathString(d).toPath() }
    Canvas(modifier.size(size)) {
        val s = this.size.minDimension / 24f
        scale(s, s, pivot = Offset.Zero) { drawPath(path, color = tint) }
    }
}
