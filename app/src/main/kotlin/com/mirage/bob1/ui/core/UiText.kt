package org.bob1.app.ui.core

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import org.bob1.app.data.remote.ApiException

private val fr = Locale.FRANCE
private val zone get() = ZoneId.systemDefault()

fun Instant.toLocalDate(): LocalDate = atZone(zone).toLocalDate()
fun Instant.formatTime(): String = DateTimeFormatter.ofPattern("HH:mm", fr).format(atZone(zone))
fun Instant.formatDayTime(): String = DateTimeFormatter.ofPattern("EEEE d MMMM · HH:mm", fr).format(atZone(zone)).capitalized()
fun Instant.formatShort(): String = DateTimeFormatter.ofPattern("d MMM · HH:mm", fr).format(atZone(zone))
fun LocalDate.formatLong(): String = DateTimeFormatter.ofPattern("EEEE d MMMM", fr).format(this).capitalized()
fun java.time.YearMonth.formatTitle(): String = DateTimeFormatter.ofPattern("MMMM yyyy", fr).format(this).capitalized()
private fun String.capitalized() = replaceFirstChar { it.titlecase(fr) }

/** What the user reads when something fails. Known API codes get a French sentence, the rest falls back to the server detail. */
fun Throwable.userMessage(): String = when (this) {
    is ApiException -> when (code) {
        "network.unreachable" -> "Vous êtes hors-ligne. Réessayez dans un instant."
        "auth.invalid_credentials" -> "E-mail ou mot de passe incorrect."
        "ride.contention" -> "Trop de réservations en même temps, réessayez."
        "ride.departed" -> "Ce covoiturage est déjà parti."
        "ride.already_booked" -> "Vous avez déjà réservé ce covoiturage."
        "email.taken" -> "Un compte existe déjà avec cette adresse e-mail."
        "email.invalid" -> "Adresse e-mail invalide."
        "password.too_short" -> "Le mot de passe doit contenir au moins 8 caractères."
        "invitation.invalid" -> "Ce code d'invitation est invalide ou a expiré."
        "match.closed" -> "Ce match n'est plus ouvert aux inscriptions."
        else -> when (status) {
            401 -> "Session expirée, reconnectez-vous."
            403 -> "Action non autorisée."
            else -> message ?: "Une erreur est survenue."
        }
    }
    else -> message ?: "Une erreur est survenue."
}
