package com.bob1.app.ui.core.components.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// No "success" role exists in the app's color scheme (Theme.kt / Color.kt only
// define background/primary/secondary/accent/border/destructive). These two
// constants are used purely for the "criterion met" check icon/text below.
// If you'd like this to be theme-driven too, add success/onSuccess colors to
// Color.kt + Theme.kt and swap these for MaterialTheme.colorScheme.success.
private val SuccessGreen = Color(0xFF22C55E)
private val SuccessGreenText = Color(0xFF15803D)

@Composable
fun PasswordCriterionRow(label: String, met: Boolean) {
    val cs = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (met) Icons.Default.Check else Icons.Default.Circle,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = if (met) SuccessGreen else cs.outline
        )
        Text(
            text = label,
            fontSize = 13.sp,
            color = if (met) SuccessGreenText else cs.onSurfaceVariant
        )
    }
}