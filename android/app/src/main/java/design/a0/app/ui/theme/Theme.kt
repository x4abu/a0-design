package design.a0.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import design.a0.app.R

// ---------------------------------------------------------------------------
// Fonts: DM Sans (UI), Instrument Serif (display inside the device preview),
// Geist Mono (code, numerals, meta). Bundled in res/font — no runtime download.
// ---------------------------------------------------------------------------
val DmSans = FontFamily(
    Font(R.font.dm_sans_400, FontWeight.Normal),
    Font(R.font.dm_sans_500, FontWeight.Medium),
    Font(R.font.dm_sans_600, FontWeight.SemiBold),
    Font(R.font.dm_sans_700, FontWeight.Bold)
)

val InstrumentSerif = FontFamily(Font(R.font.instrument_serif_400, FontWeight.Normal))
val GeistMono = FontFamily(Font(R.font.geist_mono_400, FontWeight.Normal))

object T {
    val Display = TextStyle(
        fontFamily = DmSans, fontWeight = FontWeight.Bold, fontSize = A0.TDisplay,
        lineHeight = 45.sp, letterSpacing = (-0.035).sp
    )
    val Title = TextStyle(
        fontFamily = DmSans, fontWeight = FontWeight.Bold, fontSize = A0.TTitle,
        lineHeight = 37.sp, letterSpacing = (-0.04).sp
    )
    val H2 = TextStyle(
        fontFamily = DmSans, fontWeight = FontWeight.Bold, fontSize = A0.TH2,
        lineHeight = 30.sp, letterSpacing = (-0.03).sp
    )
    val H3 = TextStyle(
        fontFamily = DmSans, fontWeight = FontWeight.SemiBold, fontSize = A0.TH3,
        lineHeight = 26.sp, letterSpacing = (-0.02).sp
    )
    val Card = TextStyle(
        fontFamily = DmSans, fontWeight = FontWeight.SemiBold, fontSize = A0.TCard,
        lineHeight = 22.sp, letterSpacing = (-0.02).sp
    )
    val Body = TextStyle(
        fontFamily = DmSans, fontWeight = FontWeight.Normal, fontSize = A0.TBody,
        lineHeight = 23.sp
    )
    val BodyMedium = TextStyle(
        fontFamily = DmSans, fontWeight = FontWeight.Medium, fontSize = A0.TBody,
        lineHeight = 23.sp
    )
    val Small = TextStyle(
        fontFamily = DmSans, fontWeight = FontWeight.Normal, fontSize = A0.TSmall,
        lineHeight = 20.sp
    )
    val SmallMedium = TextStyle(
        fontFamily = DmSans, fontWeight = FontWeight.Medium, fontSize = A0.TSmall,
        lineHeight = 20.sp
    )
    val Chip = TextStyle(
        fontFamily = DmSans, fontWeight = FontWeight.Medium, fontSize = A0.TChip,
        lineHeight = 18.sp
    )
    val ChipOn = TextStyle(
        fontFamily = DmSans, fontWeight = FontWeight.SemiBold, fontSize = A0.TChip,
        lineHeight = 18.sp
    )
    val Caption = TextStyle(
        fontFamily = DmSans, fontWeight = FontWeight.Normal, fontSize = A0.TCaption,
        lineHeight = 16.sp
    )
    val Eyebrow = TextStyle(
        fontFamily = DmSans, fontWeight = FontWeight.SemiBold, fontSize = A0.TEyebrow,
        lineHeight = 14.sp, letterSpacing = 1.5.sp
    )
    val Button = TextStyle(
        fontFamily = DmSans, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
        lineHeight = 18.sp
    )
    val Mono = TextStyle(
        fontFamily = GeistMono, fontWeight = FontWeight.Normal, fontSize = 12.sp,
        lineHeight = 18.sp
    )
    val MonoSmall = TextStyle(
        fontFamily = GeistMono, fontWeight = FontWeight.Normal, fontSize = 11.sp,
        lineHeight = 16.sp
    )
    val SerifDisplay = TextStyle(
        fontFamily = InstrumentSerif, fontWeight = FontWeight.Normal, fontSize = 34.sp,
        lineHeight = 37.sp, letterSpacing = (-1.4).sp
    )
}

// ---------------------------------------------------------------------------
// The design system the user can retune from the Style tab (accent / roundness /
// typeface / mode / fidelity). It is the live equivalent of the prototype's
// --dac / --dr / --df / .dk / .wf variables.
// ---------------------------------------------------------------------------
enum class TypefaceChoice { Sans, Serif, Mono }

data class DesignSystem(
    val accent: Color = Color(0xFFC0623B),
    val accent2: Color = Color(0xFFD8B98A),
    val radius: Int = 26,
    val typeface: TypefaceChoice = TypefaceChoice.Sans,
    val dark: Boolean = false,
    val wireframe: Boolean = false
) {
    val family: FontFamily
        get() = when (typeface) {
            TypefaceChoice.Sans -> DmSans
            TypefaceChoice.Serif -> InstrumentSerif
            TypefaceChoice.Mono -> GeistMono
        }
}

val LocalDesignSystem = compositionLocalOf { DesignSystem() }

/** Accent presets from the prototype's swatch row. */
val AccentPresets = listOf(
    Color(0xFFC0623B) to Color(0xFFD8B98A),
    Color(0xFFE8638C) to Color(0xFFFFB07A),
    Color(0xFF6F8F5C) to Color(0xFFCFE3B5),
    Color(0xFFD6A24A) to Color(0xFFFFE4A8),
    Color(0xFF3B322F) to Color(0xFFD8B98A)
)

@Composable
fun A0Theme(system: DesignSystem = DesignSystem(), content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalDesignSystem provides system, content = content)
}

/** Small helper: the app-level state that Settings → Font size mutates. */
object UiScale {
    var bodyScale by mutableStateOf(1f)
}
