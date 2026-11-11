package com.mirage.bob1.ui.components.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.mirage.bob1.ui.core.formatTitle
import java.time.YearMonth

@Composable
fun CalendarMonthHeader(month: YearMonth, onPrev: () -> Unit, onNext: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onPrev) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Mois précédent") }
        Text(month.formatTitle(), Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        IconButton(onNext) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Mois suivant") }
    }
}
