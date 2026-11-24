package org.bob1.capacity.biometric

/** Outcome of a [BiometricVault] operation; the prompt itself is shown by the system. */
sealed interface VaultResult<out T> {
    data class Success<T>(val value: T) : VaultResult<T>

    /** The person dismissed the prompt. */
    data object Cancelled : VaultResult<Nothing>

    /** No usable biometric hardware (or it is temporarily unavailable). */
    data object Unavailable : VaultResult<Nothing>

    /** Hardware present, but no fingerprint / face registered in the phone settings. */
    data object NoneEnrolled : VaultResult<Nothing>

    /** Nothing is stored on this device. */
    data object NotEnrolled : VaultResult<Nothing>

    /** The key was invalidated (a new fingerprint was added, the lock screen changed...): the stored token is gone for good. */
    data object Invalidated : VaultResult<Nothing>

    data class Error(val message: String) : VaultResult<Nothing>
}
