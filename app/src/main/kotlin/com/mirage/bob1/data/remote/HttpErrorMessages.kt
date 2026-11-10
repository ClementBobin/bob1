package com.mirage.bob1.data.remote

/** French sentence for an API error `code`; falls back to what the server said. */
fun friendlyMessage(status: Int, code: String?, serverMessage: String): String = when (code) {
    "auth.invalid_credentials" -> "E-mail ou mot de passe incorrect."
    "email.taken" -> "Un compte existe déjà avec cette adresse e-mail."
    "email.invalid" -> "Adresse e-mail invalide."
    "password.too_short" -> "Le mot de passe doit contenir au moins 8 caractères."
    "invitation.invalid" -> "Ce code d'invitation est invalide ou a expiré."
    "ride.contention" -> "Trop de réservations en même temps, réessayez."
    "ride.departed" -> "Ce covoiturage est déjà parti."
    "ride.already_booked" -> "Vous avez déjà réservé ce covoiturage."
    "match.closed" -> "Ce match n'est plus ouvert aux inscriptions."
    else -> when (status) {
        403 -> "Action non autorisée."
        else -> serverMessage
    }
}
