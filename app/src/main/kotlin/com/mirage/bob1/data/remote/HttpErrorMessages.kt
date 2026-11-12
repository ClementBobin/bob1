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
    "match.already_started" -> "Ce match a déjà commencé."
    "match.cancelled" -> "Ce match est annulé."
    "season.closed" -> "Cette saison est terminée : elle n'est plus modifiable."
    "participation.not_in_team" -> "Aucun de vos joueurs ne fait partie des équipes de ce match."
    "participation.no_family", "family.none" -> "Votre compte n'est rattaché à aucune famille."
    "slot.full" -> "Ce poste est déjà complet."
    "slot.already_joined" -> "Vous êtes déjà inscrit à ce poste."
    "slot.not_joined" -> "Vous n'êtes pas inscrit à ce poste."
    "slot.wrong_audience" -> "Ce poste n'est pas ouvert à votre profil."
    "slot.contention" -> "Trop de demandes en même temps, réessayez."
    "ride.full" -> "Il n'y a plus assez de places dans ce covoiturage."
    "ride.own_ride" -> "Vous ne pouvez pas réserver votre propre covoiturage."
    else -> when (status) {
        403 -> "Action non autorisée."
        else -> serverMessage
    }
}
