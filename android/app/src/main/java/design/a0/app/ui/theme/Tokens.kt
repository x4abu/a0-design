package design.a0.app.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * a0 design tokens.
 *
 * Every value here is lifted 1:1 from the reference prototype (the lime + white
 * light theme, which is the final cascade winner in the source HTML):
 *   --ink:#14171A --mu:#79837C --hl:#E4EADF --a:#7FCF2E --a2:#C6F36A
 *   page bg: linear-gradient(180deg,#F8FBF4,#E7F0DE) + 3 radial mesh washes
 *   radii 20 / 28 / 34 / pill  ·  spring cubic-bezier(.32,.72,0,1)
 *   ease  cubic-bezier(.2,.8,.2,1)  ·  pop cubic-bezier(.34,1.2,.5,1)
 *
 * Nothing in the app may use a colour, radius, spacing or duration that is not
 * declared in this file. That rule is the anti-slop contract (see ANTI-SLOP.md).
 */
object A0 {
    // ---- ink & neutrals -------------------------------------------------
    val Ink = Color(0xFF14171A)
    val InkSoft = Color(0xFF3D4640)
    val Mu = Color(0xFF79837C)
    val MuSoft = Color(0xFF9AA49C)
    val Hairline = Color(0xFFE4EADF)
    val HairlineSoft = Color(0xFFEEF3EA)

    // ---- accent ---------------------------------------------------------
    val Accent = Color(0xFF7FCF2E)
    val Accent2 = Color(0xFFC6F36A)
    val AccentLime = Color(0xFFCBF56F)
    val AccentLime2 = Color(0xFF9BDD40)
    val AccentInk = Color(0xFF14210A)
    val AccentDeep = Color(0xFF4E9A12)

    // ---- surfaces -------------------------------------------------------
    val BgTop = Color(0xFFF8FBF4)
    val BgBottom = Color(0xFFE7F0DE)
    val Surface = Color(0xFFFFFFFF)
    val Glass = Color(0xCCFFFFFF)
    val GlassStrong = Color(0xF2FFFFFF)
    val SegTrack = Color(0xFFE6EDE0)
    val SegOff = Color(0xFF6C766F)
    val ChipInk = Color(0xFF46504A)
    val Track = Color(0xFFD5DDD0)
    val SheetScrim = Color(0x6614171A)

    // ---- semantic -------------------------------------------------------
    val Success = Color(0xFF6BD39B)
    val CodeString = Color(0xFF2F8F6B)
    val CodeKey = Color(0xFF4E9A12)
    val CodePlain = Color(0xFF3D4640)

    // ---- pastel chip gradients (p1..p5) ---------------------------------
    data class Pastel(val start: Color, val end: Color, val ink: Color)

    val P1 = Pastel(Color(0xFFF7EFC9), Color(0xFFF1E3A6), Color(0xFF6B5A18))
    val P2 = Pastel(Color(0xFFE3F6CE), Color(0xFFCDEEA9), Color(0xFF3E5B22))
    val P3 = Pastel(Color(0xFFFBE3EA), Color(0xFFF6D0DC), Color(0xFF7A3547))
    val P4 = Pastel(Color(0xFFECE7FB), Color(0xFFDCD4F5), Color(0xFF4B3F73))
    val P5 = Pastel(Color(0xFFDDF2EF), Color(0xFFC9E8E4), Color(0xFF2A5A54))

    val pastels = listOf(P1, P2, P3, P4, P5)
    fun pastel(index: Int): Pastel = pastels[((index % pastels.size) + pastels.size) % pastels.size]

    // ---- radii (max two sizes per screen: outer 28, inner 14) -----------
    val RField = 20.dp
    val RTile = 14.dp
    val RChip = 18.dp
    val RCard = 28.dp
    val RSheet = 34.dp
    val RDevice = 38.dp
    val RPill = 999.dp

    // ---- spacing (4pt grid, strict) -------------------------------------
    val S4 = 4.dp
    val S8 = 8.dp
    val S10 = 10.dp
    val S12 = 12.dp
    val S14 = 14.dp
    val S16 = 16.dp
    val S18 = 18.dp
    val S20 = 20.dp
    val S22 = 22.dp
    val S24 = 24.dp
    val S26 = 26.dp
    val S28 = 28.dp
    val S32 = 32.dp
    val S40 = 40.dp
    val S48 = 48.dp
    val S64 = 64.dp

    // ---- type scale (sp) ------------------------------------------------
    val TDisplay = 42.sp
    val TTitle = 34.sp
    val TH2 = 26.sp
    val TH3 = 21.sp
    val TCard = 17.sp
    val TBody = 15.sp
    val TSmall = 14.sp
    val TChip = 14.sp
    val TCaption = 12.sp
    val TEyebrow = 11.sp

    // ---- motion ---------------------------------------------------------
    const val D_FEEDBACK = 120
    const val D_SHORT = 200
    const val D_MED = 300
    const val D_LONG = 450
    const val D_SHEET = 600

    val Ease: Easing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)
    val Spring: Easing = CubicBezierEasing(0.34f, 1.2f, 0.5f, 1f)
    val Pill: Easing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)
    val Pop: Easing = CubicBezierEasing(0.34f, 1.4f, 0.4f, 1f)
}
