package com.mirage.bob1.ui.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.kindling.compose.KDestination
import dev.kindling.core.components.ui.avatar.KAvatar
import dev.kindling.core.components.ui.avatar.KAvatarFallback
import dev.kindling.core.components.ui.avatar.KAvatarSize
import dev.kindling.core.components.ui.badge.KBadge
import dev.kindling.core.components.ui.badge.KBadgeVariant
import dev.kindling.core.components.ui.button.KButton
import dev.kindling.core.components.ui.button.KButtonVariant
import dev.kindling.core.components.ui.card.KCard
import dev.kindling.core.components.ui.card.KCardContent
import dev.kindling.core.components.ui.empty.KEmptyState
import dev.kindling.core.components.ui.spinner.KSpinner
import com.mirage.bob1.domain.model.Match
import com.mirage.bob1.domain.model.MatchStatus
import com.mirage.bob1.ui.core.formatTime
import com.mirage.bob1.domain.model.UserRole
import com.mirage.bob1.ui.core.AppMode
import com.mirage.bob1.ui.core.navigation.*

// ── Brand ───────────────────────────────────────────────────

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

// ── Bars ────────────────────────────────────────────────────

@Composable
fun Bob1TopBar(
    title: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        Modifier.fillMaxWidth().statusBarsPadding().height(56.dp).padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour") }
        } else {
            Box(Modifier.padding(start = 8.dp)) { Bob1Logo(34.dp) }
        }
        Text(
            title.orEmpty(), Modifier.weight(1f).padding(horizontal = 8.dp),
            style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold,
            textAlign = if (onBack != null) TextAlign.Center else TextAlign.Start, maxLines = 1,
        )
        actions()
        if (onBack != null && title != null) Spacer(Modifier.width(48.dp)) // keeps the title centred
    }
}

@Composable
fun NotificationBell(unread: Int, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        BadgedBox(badge = { if (unread > 0) Badge { Text(if (unread > 9) "9+" else unread.toString()) } }) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
        }
    }
}

data class TabSpec(val destination: KDestination, val label: String, val icon: ImageVector)

private val CalendarTab = TabSpec(CalendarDestination, "Calendrier", Icons.Outlined.CalendarMonth)
private val CommitmentsTab = TabSpec(CommitmentsDestination, "Engagements", Icons.Outlined.VerifiedUser)
private val RankingTab = TabSpec(RankingDestination, "Classement", Icons.Outlined.EmojiEvents)
private val ProfileTab = TabSpec(ProfileDestination, "Profil", Icons.Outlined.Person)
private val StaffCalendarTab = TabSpec(StaffCalendarDestination, "Calendrier", Icons.Outlined.CalendarMonth)
private val StaffAssignmentsTab = TabSpec(StaffAssignmentsDestination, "Affectations", Icons.Outlined.Shield)
private val StaffRankingTab = TabSpec(StaffRankingDestination, "Classement", Icons.Outlined.EmojiEvents)

/** Bottom bar content for a user: Parent or Staff experience (switchable when both roles). Administration lives on the web. */
fun tabsFor(roles: Set<UserRole>, mode: AppMode): List<TabSpec> {
    val parent = UserRole.Parent in roles
    val staff = UserRole.Staff in roles
    val base = when {
        parent && (mode == AppMode.Parent || !staff) -> listOf(CalendarTab, CommitmentsTab, RankingTab, ProfileTab)
        staff -> listOf(StaffCalendarTab, StaffAssignmentsTab, StaffRankingTab, ProfileTab)
        else -> listOf(ProfileTab)
    }
    return base
}

@Composable
fun Bob1BottomBar(tabs: List<TabSpec>, currentRoute: String?, onSelect: (KDestination) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.destination.route,
                onClick = { onSelect(tab.destination) },
                icon = { Icon(tab.icon, contentDescription = null) },
                label = { Text(tab.label, fontSize = 11.sp, maxLines = 1) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
            )
        }
    }
}

// ── Building blocks ─────────────────────────────────────────

enum class Tone { Success, Pending, Joined, Danger, Neutral }

@Composable
fun toneColor(tone: Tone): Color = when (tone) {
    Tone.Success -> MaterialTheme.colorScheme.secondary
    Tone.Pending -> MaterialTheme.colorScheme.primary
    Tone.Joined -> MaterialTheme.colorScheme.tertiary
    Tone.Danger -> MaterialTheme.colorScheme.error
    Tone.Neutral -> MaterialTheme.colorScheme.onSurfaceVariant
}

@Composable
fun StatusBadge(text: String, tone: Tone) {
    val color = toneColor(tone)
    KBadge(variant = KBadgeVariant(bg = { color.copy(alpha = .14f) }, fg = { color })) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
fun PersonAvatar(initials: String, size: KAvatarSize = KAvatarSize.Default) {
    KAvatar(size = size) { KAvatarFallback(size = size, initials = initials) }
}

/** A Kindling card with a title row (optional step number + trailing slot) and padded content. */
@Composable
fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    step: Int? = null,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    KCard(modifier = modifier.fillMaxWidth()) {
        KCardContent(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (step != null) {
                    Box(
                        Modifier.size(24.dp).background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) { Text(step.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer) }
                }
                Text(title, Modifier.weight(1f), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                trailing?.invoke()
            }
            content()
        }
    }
}

fun Match.tone(committed: Boolean): Tone = when {
    status == MatchStatus.Cancelled -> Tone.Danger
    status == MatchStatus.Played -> Tone.Neutral
    committed -> Tone.Joined
    else -> Tone.Pending
}

fun Match.statusLabel(committed: Boolean): String = when {
    status == MatchStatus.Cancelled -> "Annulé"
    status == MatchStatus.Played -> "Joué"
    committed -> "Inscrit"
    else -> "À confirmer"
}

@Composable
fun MatchRow(match: Match, committed: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(match.startsAt.formatTime(), fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.width(44.dp))
        Column(Modifier.weight(1f)) {
            Text(match.title, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(match.divisionName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
        StatusBadge(match.statusLabel(committed), match.tone(committed))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ── Screen states ───────────────────────────────────────────

@Composable
fun LoadingBox(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { KSpinner() }
}

@Composable
fun ErrorBox(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(message, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        KButton(text = "Réessayer", onClick = onRetry, variant = KButtonVariant.Outline)
    }
}

@Composable
fun EmptyBox(icon: ImageVector, title: String, description: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
        KEmptyState(icon = icon, title = title, description = description)
    }
}

// ── Dialogs ─────────────────────────────────────────────────

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dismissText: String = "Annuler",
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        confirmButton = { KButton(text = confirmText, onClick = onConfirm) },
        dismissButton = { KButton(text = dismissText, onClick = onDismiss, variant = KButtonVariant.Ghost) },
    )
}
