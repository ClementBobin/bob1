package com.mirage.bob1.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirage.bob1.domain.model.PasswordRule

/** The password rules with a tick next to each one that is already satisfied. */
@Composable
fun PasswordChecklist(rules: List<PasswordRule>, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        rules.forEach { rule ->
            val color = if (rule.ok) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(if (rule.ok) "✓" else "○", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
                Text(rule.label, fontSize = 12.sp, color = color)
            }
        }
    }
}
