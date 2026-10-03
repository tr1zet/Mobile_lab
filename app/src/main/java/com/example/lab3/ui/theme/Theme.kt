package com.example.lab3.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LabColorScheme = lightColorScheme(
    primary = Color(0xFF006A6A),
    background = Color.White,
    surface = Color.White,
    onBackground = Color(0xFF1A1C1C),
    onSurface = Color(0xFF1A1C1C)
)

@Composable
fun Lab3Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LabColorScheme,
        content = content
    )
}

