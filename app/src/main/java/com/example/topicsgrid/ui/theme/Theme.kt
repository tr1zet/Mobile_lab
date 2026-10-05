package com.example.topicsgrid.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TopicsColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

@Composable
fun TopicsGridTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TopicsColorScheme,
        content = content
    )
}

