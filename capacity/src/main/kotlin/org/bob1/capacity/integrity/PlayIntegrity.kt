package org.bob1.capacity.integrity

import android.content.Context
import dev.kindling.android.natif.PlayIntegrityHelper
import kotlin.coroutines.cancellation.CancellationException

/**
 * Play Integrity capacity, built on Kindling :android `PlayIntegrityHelper`
 * (https://clementbobin.github.io/Kindling/#/android/play-integrity).
 *
 * The app only requests the signed token (a fresh nonce is generated per call) and forwards it to the API.
 * The verdict is decoded SERVER-SIDE (`playintegrity.googleapis.com/v1/{packageName}:decodeIntegrityToken`),
 * never on the device. Which fields the decoded verdict carries is configured in Play Console
 * (Play Console -> App integrity -> Integrity API -> "Response settings"):
 *
 * | Group               | Field                         | Values |
 * |---------------------|-------------------------------|--------|
 * | accountDetails      | appLicensingVerdict           | LICENSED, UNLICENSED, UNEVALUATED |
 * | appIntegrity        | appRecognitionVerdict         | PLAY_RECOGNIZED, UNRECOGNIZED_VERSION, UNEVALUATED |
 * | deviceIntegrity     | deviceRecognitionVerdict      | MEETS_BASIC_INTEGRITY, MEETS_DEVICE_INTEGRITY, MEETS_STRONG_INTEGRITY |
 * | deviceIntegrity     | recentDeviceActivity          | UNEVALUATED, LEVEL_1 .. LEVEL_4 |
 * | deviceIntegrity     | deviceAttributes.sdkVersion   | 19 .. 37 |
 * | environmentDetails  | playProtectVerdict            | UNEVALUATED, NO_ISSUES, NO_DATA, MEDIUM_RISK, HIGH_RISK, POSSIBLE_RISK |
 * | environmentDetails  | appAccessRiskVerdict          | KNOWN_/UNKNOWN_ x INSTALLED, CAPTURING, OVERLAYS, CONTROLLING |
 */
class PlayIntegrity(context: Context) {

    private val helper = PlayIntegrityHelper(context.applicationContext)

    /**
     * Requests a signed integrity token, or `null` when the Integrity service fails
     * (no Play Services, not recognised by Play, network...). Cancellation is rethrown, never swallowed.
     */
    suspend fun requestToken(): String? = try {
        helper.requestToken()
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        null
    }
}
