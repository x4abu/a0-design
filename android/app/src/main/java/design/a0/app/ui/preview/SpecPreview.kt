package design.a0.app.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import design.a0.app.data.Block
import design.a0.app.data.Spec
import design.a0.app.data.SpecScreen
import design.a0.app.ui.comp.a0Clickable
import design.a0.app.ui.theme.DmSans
import design.a0.app.ui.theme.GeistMono
import design.a0.app.ui.theme.InstrumentSerif

/**
 * Renders a real generated spec. There is no sample content here: every string,
 * colour and radius came out of the model response stored in the project.
 */
@Composable
fun SpecPreview(
    spec: Spec,
    screenIndex: Int,
    deviceMode: Int,
    selectedBlock: Int?,
    onSelectBlock: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = spec.theme
    val bg = theme.bg.color(Color(0xFFF6EFE4))
    val surface = theme.surface.color(Color.White)
    val ink = theme.ink.color(Color(0xFF2B2118))
    val accent = theme.accent.color(Color(0xFFC0623B))
    val accent2 = theme.accent2.color(Color(0xFFD8B98A))
    val family: FontFamily = when (theme.font) {
        "serif" -> InstrumentSerif
        "mono" -> GeistMono
        else -> DmSans
    }
    val radius = theme.radius.dp
    val screen: SpecScreen? = spec.screens.getOrNull(screenIndex) ?: spec.screens.firstOrNull()

    val frameWidth: Dp = when (deviceMode) {
        1 -> 340.dp
        2 -> 360.dp
        else -> 290.dp
    }
    val frameRadius = when (deviceMode) {
        2 -> 14.dp
        1 -> 30.dp
        else -> 38.dp
    }

    Column(
        modifier
            .width(frameWidth)
            .heightIn(min = 540.dp)
            .shadow(26.dp, RoundedCornerShape(frameRadius), ambientColor = Color(0x553A4A30), spotColor = Color(0x553A4A30))
            .background(bg, RoundedCornerShape(frameRadius))
            .border(if (deviceMode == 2) 5.dp else 7.dp, Color(0xFF14171A), RoundedCornerShape(frameRadius))
            .padding(start = 18.dp, end = 18.dp, top = if (deviceMode == 2) 34.dp else 22.dp, bottom = 74.dp)
    ) {
        if (screen == null) {
            Text("No screens in this design", color = ink, fontFamily = family, fontSize = 14.sp)
        } else {
            screen.blocks.forEachIndexed { index, block ->
                BlockView(
                    block = block,
                    index = index,
                    selected = selectedBlock == index,
                    onSelect = onSelectBlock,
                    ink = ink,
                    surface = surface,
                    accent = accent,
                    accent2 = accent2,
                    family = family,
                    radius = radius
                )
            }
            if (screen.blocks.none { it.type == "button" }) {
                Spacer(Modifier.height(8.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        ScreenTabs(spec, screenIndex, ink, bg)
    }
}

@Composable
private fun BlockView(
    block: Block,
    index: Int,
    selected: Boolean,
    onSelect: (Int) -> Unit,
    ink: Color,
    surface: Color,
    accent: Color,
    accent2: Color,
    family: FontFamily,
    radius: Dp
) {
    val selection = if (selected) {
        Modifier.border(2.dp, Color(0xFF5DB01F), RoundedCornerShape(10.dp)).padding(2.dp)
    } else Modifier
    val base = Modifier.fillMaxWidth().a0Clickable { onSelect(index) }.then(selection)

    when (block.type) {
        "sub" -> Text(
            block.text,
            color = ink.copy(alpha = 0.62f),
            fontSize = 12.sp,
            fontFamily = family,
            modifier = base.padding(bottom = 4.dp)
        )

        "headline" -> Text(
            block.text,
            color = ink,
            fontSize = 34.sp,
            lineHeight = 37.sp,
            letterSpacing = (-1.2).sp,
            fontWeight = if (family == InstrumentSerif) FontWeight.Normal else FontWeight.Bold,
            fontFamily = family,
            modifier = base.padding(top = 4.dp, bottom = 14.dp)
        )

        "hero" -> Box(
            base
                .padding(bottom = 12.dp)
                .height(130.dp)
                .clip(RoundedCornerShape(radius))
                .background(Brush.linearGradient(listOf(accent, accent2)))
                .padding(16.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Text(
                listOf(block.title, block.meta).filter { it.isNotBlank() }.joinToString("\n"),
                color = Color.White,
                fontSize = 19.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = family
            )
        }

        "stat" -> Row(
            base.padding(bottom = 12.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(block.text, color = ink, fontSize = 44.sp, fontWeight = FontWeight.Bold, fontFamily = family)
            if (block.meta.isNotBlank()) Text(
                block.meta,
                color = ink.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontFamily = family,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        "button" -> Box(
            base
                .padding(top = 4.dp, bottom = 12.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(ink),
            contentAlignment = Alignment.Center
        ) {
            Text(block.text, color = Color(0xFFFBF9F5), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = family)
        }

        "cards" -> Row(
            base.padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            block.items.take(2).forEach { item ->
                Column(
                    Modifier
                        .weight(1f)
                        .height(92.dp)
                        .clip(RoundedCornerShape((radius.value * 0.77f).dp))
                        .background(surface)
                        .padding(12.dp)
                ) {
                    Text(item.title, color = ink, fontSize = 12.sp, fontFamily = family, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.weight(1f))
                    if (item.meta.isNotBlank()) Text(item.meta, color = ink, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, fontFamily = family, maxLines = 1)
                }
            }
        }

        "rows" -> Column(base.padding(bottom = 12.dp)) {
            block.items.forEach { item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape((radius.value * 0.6f).dp))
                        .background(surface)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(Modifier.size(30.dp).clip(RoundedCornerShape(9.dp)).background(accent.copy(alpha = 0.85f)))
                    Column(Modifier.weight(1f)) {
                        Text(item.title, color = ink, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, fontFamily = family, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (item.meta.isNotBlank()) Text(item.meta, color = ink.copy(alpha = 0.6f), fontSize = 11.sp, fontFamily = family, maxLines = 1)
                    }
                    if (item.trail.isNotBlank()) Text(item.trail, color = ink, fontSize = 13.sp, fontFamily = family)
                }
            }
        }

        "note" -> Text(
            block.text,
            color = ink.copy(alpha = 0.7f),
            fontSize = 13.sp,
            lineHeight = 19.sp,
            fontFamily = family,
            textAlign = TextAlign.Start,
            modifier = base.padding(bottom = 10.dp)
        )
    }
}

@Composable
private fun ScreenTabs(spec: Spec, screenIndex: Int, ink: Color, bg: Color) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(99.dp))
            .background(ink)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        spec.screens.forEachIndexed { index, screen ->
            val on = index == screenIndex
            Box(
                Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(if (on) bg else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    screen.title.ifBlank { screen.id }.take(10),
                    color = if (on) ink else bg.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

private fun String.color(fallback: Color): Color {
    val v = trim().removePrefix("#")
    if (v.length != 6) return fallback
    return runCatching {
        Color(0xFF000000.toInt() or v.toLong(16).toInt())
    }.getOrDefault(fallback)
}
