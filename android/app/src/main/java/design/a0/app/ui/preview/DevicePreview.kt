package design.a0.app.ui.preview

import androidx.compose.animation.core.tween
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import design.a0.app.ui.comp.a0Clickable
import design.a0.app.ui.comp.pressScale
import design.a0.app.ui.theme.A0
import design.a0.app.ui.theme.DesignSystem
import design.a0.app.ui.theme.InstrumentSerif

/**
 * The design the user is editing, rendered inside a phone / tablet / web frame.
 * Colours, radius, typeface, dark mode and wireframe all come from [ds], so the
 * Style tab drives this live — exactly like the prototype's CSS variables.
 */
@Composable
fun DevicePreview(
    ds: DesignSystem,
    deviceMode: Int,
    screen: String,
    playMode: Boolean,
    selected: String?,
    onSelect: (String) -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (ds.dark) Color(0xFF1E1916) else Color(0xFFF6EFE4)
    val ink = if (ds.dark) Color(0xFFF4ECE3) else Color(0xFF2B2118)
    val mu = if (ds.dark) Color(0xFFA59A90) else Color(0xFF8A7A68)
    val card1 = if (ds.dark) Color(0xFF2C2520) else Color(0xFFE9DCC8)
    val card2 = if (ds.dark) Color(0xFF272B24) else Color(0xFFDDE2D3)
    val btn = if (ds.dark) Color(0xFFF4ECE3) else Color(0xFF2B2118)
    val family: FontFamily = ds.family
    val radius = ds.radius.dp

    val maxW: Dp = when (deviceMode) {
        1 -> 340.dp
        2 -> 360.dp
        else -> 290.dp
    }
    val minH: Dp = when (deviceMode) {
        1 -> 620.dp
        2 -> 470.dp
        else -> 540.dp
    }
    val frameRadius = when (deviceMode) {
        2 -> 14.dp
        1 -> 30.dp
        else -> A0.RDevice
    }

    Column(
        modifier
            .width(maxW)
            .heightIn(min = minH)
            .shadow(26.dp, RoundedCornerShape(frameRadius), ambientColor = Color(0x553A4A30), spotColor = Color(0x553A4A30))
            .background(bg, RoundedCornerShape(frameRadius))
            .border(if (deviceMode == 2) 5.dp else 7.dp, A0.Ink, RoundedCornerShape(frameRadius))
            .padding(start = 18.dp, end = 18.dp, top = if (deviceMode == 2) 34.dp else 22.dp, bottom = 74.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        when (screen) {
            "brew" -> BrewScreen(ds, ink, mu, card1, family, radius, playMode, selected, onSelect, onNavigate)
            "shop" -> ShopScreen(ds, ink, mu, card1, card2, btn, family, radius, playMode, selected, onSelect, onNavigate)
            else -> HomeScreen(ds, ink, mu, card1, card2, btn, family, radius, playMode, selected, onSelect, onNavigate)
        }
        Spacer(Modifier.height(16.dp))
        DeviceNav(ink, bg, screen, playMode, onNavigate)
    }
}

@Composable
private fun Modifier.element(
    name: String,
    playMode: Boolean,
    selected: String?,
    onSelect: (String) -> Unit,
    onClick: (() -> Unit)? = null
): Modifier {
    val isSelected = selected == name && !playMode
    var m = this
    if (isSelected) {
        m = m.border(2.dp, Color(0xFF5DB01F), RoundedCornerShape(12.dp))
    }
    return m.a0Clickable {
        if (playMode) onClick?.invoke() else onSelect(name)
    }
}

@Composable
private fun HomeScreen(
    ds: DesignSystem, ink: Color, mu: Color, card1: Color, card2: Color, btn: Color,
    family: FontFamily, radius: Dp, playMode: Boolean, selected: String?,
    onSelect: (String) -> Unit, onNavigate: (String) -> Unit
) {
    Text(
        "Good morning, Zara",
        color = mu, fontSize = 12.sp, fontFamily = family,
        modifier = Modifier.element("Header", playMode, selected, onSelect)
    )
    Text(
        "Slow coffee,\nwell made.",
        color = ink,
        fontSize = 34.sp,
        lineHeight = 37.sp,
        letterSpacing = (-1.4).sp,
        fontWeight = if (family == InstrumentSerif) FontWeight.Normal else FontWeight.Bold,
        fontFamily = family,
        modifier = Modifier
            .padding(top = 8.dp, bottom = 14.dp)
            .element("Headline", playMode, selected, onSelect)
    )
    // hero card
    Box(
        Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(radius))
            .background(Brush.linearGradient(listOf(ds.accent, ds.accent2)))
            .padding(16.dp)
            .element("Hero card", playMode, selected, onSelect) { onNavigate("brew") },
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            "Ethiopia Guji\nNatural, 250g",
            color = Color(0xFFFBF9F5),
            fontSize = 19.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = family
        )
    }
    // primary button
    Box(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(top = 14.dp)
            .clip(RoundedCornerShape(A0.RPill))
            .background(btn)
            .element("Button", playMode, selected, onSelect) { onNavigate("shop") },
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Order a bag",
            color = if (ds.dark) Color(0xFF1E1916) else Color(0xFFF6EFE4),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = family
        )
    }
    Row(
        Modifier.fillMaxWidth().padding(top = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        listOf(
            Triple("Brew guide", "V60 · 2:30", card1),
            Triple("Roasters", "14 near you", card2)
        ).forEach { (title, value, color) ->
            Column(
                Modifier
                    .weight(1f)
                    .height(90.dp)
                    .clip(RoundedCornerShape((radius.value * 0.77f).dp))
                    .background(color)
                    .padding(12.dp)
                    .element("Card", playMode, selected, onSelect) { onNavigate("brew") }
            ) {
                Text(title, color = ink, fontSize = 12.sp, fontFamily = family)
                Text(value, color = ink, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = family)
            }
        }
    }
}

@Composable
private fun BrewScreen(
    ds: DesignSystem, ink: Color, mu: Color, card1: Color,
    family: FontFamily, radius: Dp, playMode: Boolean, selected: String?,
    onSelect: (String) -> Unit, onNavigate: (String) -> Unit
) {
    Text("Brew guide", color = mu, fontSize = 12.sp, fontFamily = family,
        modifier = Modifier.element("Header", playMode, selected, onSelect))
    Text(
        "V60,\nstep by step.",
        color = ink, fontSize = 34.sp, lineHeight = 37.sp, letterSpacing = (-1.4).sp,
        fontWeight = if (family == InstrumentSerif) FontWeight.Normal else FontWeight.Bold,
        fontFamily = family,
        modifier = Modifier.padding(top = 8.dp, bottom = 14.dp).element("Headline", playMode, selected, onSelect)
    )
    Row(
        Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(bottom = 10.dp)
            .clip(RoundedCornerShape((radius.value * 0.77f).dp))
            .background(card1)
            .padding(12.dp)
            .element("Timer", playMode, selected, onSelect),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("2:30", color = ink, fontSize = 28.sp, fontWeight = FontWeight.SemiBold, fontFamily = family)
        Text("Bloom · 30s", color = mu, fontSize = 12.sp, fontFamily = family)
    }
    listOf("Rinse the filter", "Bloom with 50 g water", "Pour slowly in circles")
        .forEachIndexed { i, step ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape((radius.value * 0.6f).dp))
                    .background(card1)
                    .padding(12.dp)
                    .element("Step", playMode, selected, onSelect),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    Modifier.size(26.dp).background(ink, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${i + 1}", color = if (ds.dark) Color(0xFF1E1916) else Color(0xFFF6EFE4),
                        fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = family)
                }
                Text(step, color = ink, fontSize = 13.sp, fontFamily = family, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
}

@Composable
private fun ShopScreen(
    ds: DesignSystem, ink: Color, mu: Color, card1: Color, card2: Color, btn: Color,
    family: FontFamily, radius: Dp, playMode: Boolean, selected: String?,
    onSelect: (String) -> Unit, onNavigate: (String) -> Unit
) {
    Text("Shop", color = mu, fontSize = 12.sp, fontFamily = family,
        modifier = Modifier.element("Header", playMode, selected, onSelect))
    Text(
        "Fresh\nthis week.",
        color = ink, fontSize = 34.sp, lineHeight = 37.sp, letterSpacing = (-1.4).sp,
        fontWeight = if (family == InstrumentSerif) FontWeight.Normal else FontWeight.Bold,
        fontFamily = family,
        modifier = Modifier.padding(top = 8.dp, bottom = 14.dp).element("Headline", playMode, selected, onSelect)
    )
    listOf(
        Triple("Ethiopia Guji", "250g · $18", ds.accent),
        Triple("Kenya AA", "250g · $20", ds.accent2)
    ).forEach { (name, meta, dot) ->
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .clip(RoundedCornerShape((radius.value * 0.6f).dp))
                .background(card1)
                .padding(12.dp)
                .element("Product", playMode, selected, onSelect),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(dot))
            Column(Modifier.weight(1f)) {
                Text(name, color = ink, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, fontFamily = family)
                Text(meta, color = mu, fontSize = 11.sp, fontFamily = family)
            }
        }
    }
    Box(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(top = 14.dp)
            .clip(RoundedCornerShape(A0.RPill))
            .background(btn)
            .element("Button", playMode, selected, onSelect),
        contentAlignment = Alignment.Center
    ) {
        Text("Checkout", color = if (ds.dark) Color(0xFF1E1916) else Color(0xFFF6EFE4),
            fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = family)
    }
}

@Composable
private fun DeviceNav(ink: Color, bg: Color, screen: String, playMode: Boolean, onNavigate: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(A0.RPill))
            .background(ink)
            .padding(4.dp)
    ) {
        listOf("home" to "Home", "brew" to "Brew", "shop" to "Shop").forEach { (id, label) ->
            val on = screen == id
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .height(38.dp)
                    .clip(RoundedCornerShape(A0.RPill))
                    .background(if (on) bg else Color.Transparent)
                    .a0Clickable { onNavigate(id) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    color = if (on) ink else bg.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
