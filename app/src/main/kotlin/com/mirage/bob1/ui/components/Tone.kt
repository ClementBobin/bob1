package com.mirage.bob1.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class Tone { Success, Pending, Joined, Danger, Neutral }

@Composable
fun toneColor(tone: Tone): Color = when (tone) {
    Tone.Success -> MaterialTheme.colorScheme.secondary
    Tone.Pending -> MaterialTheme.colorScheme.primary
    Tone.Joined -> MaterialTheme.colorScheme.tertiary
    Tone.Danger -> MaterialTheme.colorScheme.error
    Tone.Neutral -> MaterialTheme.colorScheme.onSurfaceVariant
}
