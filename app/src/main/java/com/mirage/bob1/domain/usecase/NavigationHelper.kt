package com.mirage.bob1.domain.usecase

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.mirage.bob1.domain.model.Match
import androidx.core.net.toUri

/**
 * Launches the user's preferred navigation / map application for a [Match] location.
 *
 * Priority:
 * 1. Coordinates — `geo:lat,lng?q=lat,lng(Label)` — drops a precise pin
 * 2. Address     — `geo:0,0?q=address(Label)`     — map app geocodes it
 *
 * Works with Google Maps, Waze, OsmAnd, Here, and any app registered for ACTION_VIEW + geo: URI.
 * Shows a Toast if no map app is installed instead of crashing.
 *
 * Usage in a Composable:
 * ```kotlin
 * val context = LocalContext.current
 * Icon(
 *     imageVector = Icons.Default.Place,
 *     modifier    = Modifier.clickable { NavigationHelper.launch(context, match) }
 * )
 * ```
 */
object NavigationHelper {

    fun launch(context: Context, match: Match) {
        val intent = Intent(Intent.ACTION_VIEW, match.toGeoUri())
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "Aucune application de navigation trouvée.", Toast.LENGTH_SHORT).show()
        }
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private fun Match.toGeoUri(): Uri {
        val label = Uri.encode(location)
        return if (locationLat != null && locationLng != null) {
            "geo:$locationLat,$locationLng?q=$locationLat,$locationLng($label)".toUri()
        } else {
            val query = Uri.encode(locationAddress.ifBlank { location })
            "geo:0,0?q=$query($label)".toUri()
        }
    }
}