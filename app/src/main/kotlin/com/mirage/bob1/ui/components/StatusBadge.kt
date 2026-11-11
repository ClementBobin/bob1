package com.mirage.bob1.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.kindling.core.components.ui.badge.KBadge
import dev.kindling.core.components.ui.badge.KBadgeVariant

@Composable
fun StatusBadge(text: String, tone: Tone) {
    val color = toneColor(tone)
    KBadge(variant = KBadgeVariant(bg = { color.copy(alpha = .14f) }, fg = { color })) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}
