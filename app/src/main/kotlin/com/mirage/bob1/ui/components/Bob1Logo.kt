package com.mirage.bob1.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** The BOB1 ball (ring + ball + seams), drawn from the design system's logo path. */
@Composable
fun Bob1Logo(size: Dp = 32.dp) {
    val ring = MaterialTheme.colorScheme.secondary
    val ball = MaterialTheme.colorScheme.primary
    val seam = MaterialTheme.colorScheme.onPrimary
    Canvas(Modifier.size(size)) {
        val s = this.size.width / 48f
        fun p(x: Float, y: Float) = Offset(x * s, y * s)
        drawCircle(ring, radius = 21f * s, center = p(24f, 24f), style = Stroke(4f * s))
        drawCircle(ball, radius = 15f * s, center = p(24f, 24f))
        val stroke = Stroke(1.8f * s, cap = StrokeCap.Round)
        drawLine(seam, p(9f, 24f), p(39f, 24f), strokeWidth = 1.8f * s, cap = StrokeCap.Round)
        val left = Path().apply {
            moveTo(24f * s, 9f * s); cubicTo(19f * s, 13f * s, 17f * s, 18f * s, 17f * s, 24f * s)
            cubicTo(17f * s, 30f * s, 19f * s, 35f * s, 24f * s, 39f * s)
        }
        val right = Path().apply {
            moveTo(24f * s, 9f * s); cubicTo(29f * s, 13f * s, 31f * s, 18f * s, 31f * s, 24f * s)
            cubicTo(31f * s, 30f * s, 29f * s, 35f * s, 24f * s, 39f * s)
        }
        drawPath(left, seam, style = stroke)
        drawPath(right, seam, style = stroke)
    }
}
