package com.mirage.bob1.domain.model

/** Why a biometric operation did not happen. [message] is a French sentence meant for the user; [Cancelled] is silent. */
sealed class BiometricException(message: String) : Exception(message) {
    class Cancelled : BiometricException("Authentification annulée.")
    class Unavailable : BiometricException("La biométrie n'est pas disponible sur cet appareil.")
    class NoneEnrolled : BiometricException("Aucune empreinte ou visage enregistré : ajoutez-en un dans les réglages du téléphone.")
    class NotEnrolled : BiometricException("La connexion biométrique n'est pas activée sur cet appareil.")
    class Invalidated : BiometricException("Vos données biométriques ont changé : connectez-vous avec votre mot de passe, puis réactivez la connexion biométrique.")
    class Failed(message: String) : BiometricException(message)
}
