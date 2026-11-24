package com.mirage.bob1.domain.model

data class BiometricStatus(
    /** Biometric hardware present and a fingerprint / face registered in the phone settings. */
    val available: Boolean,
    /** A token is stored on this device. */
    val enrolled: Boolean,
    /** The person declined the post-login offer. */
    val offerDismissed: Boolean,
) {
    val canSignIn get() = available && enrolled
    val canOffer get() = available && !enrolled && !offerDismissed
}
