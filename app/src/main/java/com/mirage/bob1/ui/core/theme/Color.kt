package com.mirage.bob1.ui.core.theme

import androidx.compose.ui.graphics.Color

// ============================================================
// Palette Bob1 — basée sur le logo (orange ballon + vert sapin)
// Orange: #E86602  (sampled from basketball)
// Vert  : #056037  (sampled from ring/text)
// ============================================================

val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

// ============================================================
// MODE CLAIR
// ============================================================

val LightBackground   = Color(0xFFFBFAF9)   // oklch(0.985 0.004 60) — léger tint orange
val LightForeground   = Color(0xFF120B07)   // oklch(0.12  0.02  60)
val LightCard         = Color(0xFFFFFFFF)   // oklch(1 0 0)

val LightPrimary      = Color(0xFFE86602)   // oklch(0.66 0.18 50) — orange bob1
val LightPrimaryFg    = Color(0xFFFAF7F5)   // oklch(0.98 0.005 60)

val LightSecondary    = Color(0xFFF2E9E0)   // oklch(0.94 0.015 60)
val LightSecondaryFg  = Color(0xFF251A11)   // oklch(0.20 0.025 60)

val LightMuted        = Color(0xFFF0EAE3)   // oklch(0.94 0.01  60)
val LightMutedFg      = Color(0xFF6B645C)   // oklch(0.50 0.015 60)

val LightAccent       = Color(0xFFCCE5D6)   // oklch(0.91 0.06  150) — tint vert
val LightAccentFg     = Color(0xFF0A4527)   // oklch(0.35 0.10  152)

val LightBorder       = Color(0xFFDCD6D0)   // oklch(0.88 0.015 60)
val LightDestructive  = Color(0xFFDF202E)   // oklch(0.58 0.22 25) — inchangé

val LightSidebar       = Color(0xFFF6F2EE)  // oklch(0.97 0.01  60)
val LightSidebarAccent = Color(0xFFCCE5D6)  // oklch(0.91 0.06  150)

// ============================================================
// MODE SOMBRE
// ============================================================

val DarkBackground    = Color(0xFF080503)   // oklch(0.09 0.015 60)
val DarkForeground    = Color(0xFFF4F0EF)   // oklch(0.96 0.008 60)
val DarkCard          = Color(0xFF110B08)   // oklch(0.14 0.02  60)

val DarkPrimary       = Color(0xFFFF8A33)   // oklch(0.72 0.17 50) — orange lumineux
val DarkPrimaryFg     = Color(0xFFFAF7F5)

val DarkSecondary     = Color(0xFF140F0B)   // oklch(0.18 0.02  60)
val DarkSecondaryFg   = Color(0xFFE3DDD7)   // oklch(0.90 0.01  60)

val DarkMuted         = Color(0xFF140F0B)   // oklch(0.18 0.02  60)
val DarkMutedFg       = Color(0xFF8A8079)   // oklch(0.58 0.015 60)

val DarkAccent        = Color(0xFF0A2A1A)   // oklch(0.22 0.06  152) — vert foncé
val DarkAccentFg      = Color(0xFF9DDDB8)   // oklch(0.85 0.08  150)

val DarkBorder        = Color(0x1AFFFFFF)   // oklch(1 0 0 / 10%)
val DarkInput         = Color(0x24FFFFFF)   // oklch(1 0 0 / 14%)
val DarkDestructive   = Color(0xFFF94144)   // oklch(0.65 0.22 25) — inchangé

val DarkSidebar       = Color(0xFF110B08)   // oklch(0.14 0.02  60)
val DarkSidebarAccent = Color(0xFF0A2A1A)   // oklch(0.22 0.06  152)