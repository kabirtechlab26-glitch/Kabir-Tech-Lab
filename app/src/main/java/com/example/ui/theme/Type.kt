package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val HindSiliguriFont = FontFamily(
    Font(R.font.hind_siliguri, FontWeight.Normal),
    Font(R.font.hind_siliguri, FontWeight.Medium),
    Font(R.font.hind_siliguri, FontWeight.SemiBold),
    Font(R.font.hind_siliguri, FontWeight.Bold),
)

val InterFont = FontFamily(
    Font(R.font.inter, FontWeight.Normal),
    Font(R.font.inter, FontWeight.Medium),
    Font(R.font.inter, FontWeight.SemiBold),
    Font(R.font.inter, FontWeight.Bold),
)

fun buildAppTypography(scale: Float = 1.0f): Typography {
    val s = scale.coerceIn(0.85f, 1.3f)
    return Typography(
        displaySmall = TextStyle(
            fontFamily = HindSiliguriFont,
            fontWeight = FontWeight.Bold,
            fontSize = (30 * s).sp,
            lineHeight = (38 * s).sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = HindSiliguriFont,
            fontWeight = FontWeight.Bold,
            fontSize = (24 * s).sp,
            lineHeight = (32 * s).sp,
        ),
        headlineSmall = TextStyle(
            fontFamily = HindSiliguriFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = (20 * s).sp,
            lineHeight = (28 * s).sp,
        ),
        titleLarge = TextStyle(
            fontFamily = HindSiliguriFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = (18 * s).sp,
            lineHeight = (26 * s).sp,
        ),
        titleMedium = TextStyle(
            fontFamily = HindSiliguriFont,
            fontWeight = FontWeight.Medium,
            fontSize = (16 * s).sp,
            lineHeight = (22 * s).sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = InterFont,
            fontWeight = FontWeight.Normal,
            fontSize = (15 * s).sp,
            lineHeight = (22 * s).sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = InterFont,
            fontWeight = FontWeight.Normal,
            fontSize = (13.5f * s).sp,
            lineHeight = (20 * s).sp,
        ),
        labelLarge = TextStyle(
            fontFamily = HindSiliguriFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = (13 * s).sp,
            lineHeight = (18 * s).sp,
        ),
        labelMedium = TextStyle(
            fontFamily = InterFont,
            fontWeight = FontWeight.Medium,
            fontSize = (11.5f * s).sp,
            lineHeight = (16 * s).sp,
        ),
        labelSmall = TextStyle(
            fontFamily = InterFont,
            fontWeight = FontWeight.Medium,
            fontSize = (10.5f * s).sp,
            lineHeight = (14 * s).sp,
        ),
    )
}
