package com.mirage.bob1.domain.model

data class PasswordRule(val label: String, val ok: Boolean)

/**
 * Rules a new password must satisfy. Mirrors the API's PasswordPolicy, which stays the authority (it also refuses reusing the
 * current password on reset, and answers with a code translated in HttpErrorMessages).
 */
object PasswordPolicy {
    const val MIN_LENGTH = 12
    const val MAX_LENGTH = 128

    // A password containing one of these is guessed in seconds, whatever else it contains.
    private val COMMON_WORDS = listOf(
        "password", "motdepasse", "azerty", "qwerty", "123456", "000000", "admin", "welcome", "bienvenue", "bob1", "basket",
    )
    private val FOUR_IN_A_ROW = Regex("(.)\\1{3,}")
    private val NON_ALPHANUMERIC = Regex("[^\\p{L}\\p{N}]+")

    /** The pieces of who the person is that make a password guessable (3 characters or more). */
    private fun personalParts(firstName: String, lastName: String, email: String): List<String> =
        "${email.substringBefore('@')} $firstName $lastName".split(NON_ALPHANUMERIC)
            .filter { it.length >= 3 }.distinctBy { it.lowercase() }

    fun rules(password: String, firstName: String = "", lastName: String = "", email: String = ""): List<PasswordRule> {
        val personal = personalParts(firstName, lastName, email)
        return buildList {
            add(PasswordRule("Entre $MIN_LENGTH et $MAX_LENGTH caractères", password.length in MIN_LENGTH..MAX_LENGTH))
            add(PasswordRule("Une lettre minuscule", password.any { it.isLowerCase() }))
            add(PasswordRule("Une lettre majuscule", password.any { it.isUpperCase() }))
            add(PasswordRule("Un chiffre", password.any { it.isDigit() }))
            add(PasswordRule("Un caractère spécial (! - _ ? # espace…)", password.any { !it.isLetterOrDigit() }))
            add(PasswordRule("Pas de caractère répété 4 fois de suite", !FOUR_IN_A_ROW.containsMatchIn(password)))
            add(PasswordRule("Aucun mot trop courant (password, azerty, 123456, basket…)", COMMON_WORDS.none { password.contains(it, ignoreCase = true) }))
            if (personal.isNotEmpty())
                add(PasswordRule("Ne contient pas votre nom, prénom ou e-mail", personal.none { password.contains(it, ignoreCase = true) }))
        }
    }

    fun isValid(password: String, firstName: String = "", lastName: String = "", email: String = "") =
        rules(password, firstName, lastName, email).all { it.ok }
}
