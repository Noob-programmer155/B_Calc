package com.amrtm.android.bcalc.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.amrtm.android.bcalc.R

// Set of Material typography styles to start with
val Typography = Typography(
    h1 = TextStyle(
        fontFamily = FontFamily(Font(R.font.balsamiq_sans_bold)),
        fontWeight = FontWeight.Bold,
        fontSize = 35.sp
    ),
    h2 = TextStyle(
        fontFamily = FontFamily(Font(R.font.balsamiq_sans_bold)),
        fontWeight = FontWeight.Bold,
        fontSize = 29.sp
    ),
    h3 = TextStyle(
        fontFamily = FontFamily(Font(R.font.balsamiq_sans_bold)),
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp
    ),
    h4 = TextStyle(
        fontFamily = FontFamily(Font(R.font.balsamiq_sans_bold)),
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp
    ),
    h5 = TextStyle(
        fontFamily = FontFamily(Font(R.font.balsamiq_sans_bold)),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    h6 = TextStyle(
        fontFamily = FontFamily(Font(R.font.balsamiq_sans_bold)),
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp
    ),
    body1 = TextStyle(
        fontFamily = FontFamily(Font(R.font.belgrano)),
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    button = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.W600,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily(Font(R.font.akaya_telivigala)),
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Italic,
        fontSize = 10.sp
    )
)