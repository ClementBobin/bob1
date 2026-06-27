package com.bob1.app.domain.usecase

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.bob1.app.data.dto.NotificationType

@Composable
fun NotificationType.icon() = when (this) {
    NotificationType.J15_REMINDER -> Icons.Default.Schedule
    NotificationType.J4_REMINDER  -> Icons.Default.NotificationImportant
    NotificationType.EMERGENCY    -> Icons.Default.Warning
    NotificationType.GENERAL      -> Icons.Default.Info
}

@Composable
fun NotificationType.iconBg(): Color = when (this) {
    NotificationType.J15_REMINDER -> Color(0xFF2196F3)
    NotificationType.J4_REMINDER  -> Color(0xFF4CAF50)
    NotificationType.EMERGENCY    -> Color(0xFFF44336)
    NotificationType.GENERAL      -> Color(0xFF9E9E9E)
}