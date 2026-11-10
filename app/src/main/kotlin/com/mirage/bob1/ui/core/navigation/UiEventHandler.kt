package org.bob1.app.ui.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import dev.kindling.core.components.ui.toast.KToastManager
import org.bob1.capacity.haptics.Haptics
import org.bob1.capacity.intent.Directions
import org.bob1.capacity.intent.ShareText as ShareIntent

/** Turns the non-navigation events of a ViewModel into toasts and native capabilities (maps, share sheet, haptics). */
@Composable
fun rememberUiEventHandler(): (Any) -> Unit {
    val context = LocalContext.current
    val view = LocalView.current
    val haptics = remember { Haptics() }
    return remember(context, view) {
        { event: Any ->
            when (event) {
                is ShowMessage -> if (event.isError) KToastManager.error(event.text) else KToastManager.success(event.text)
                is OpenDirections -> if (!Directions.open(context, event.address)) KToastManager.error("Aucune application de cartes trouvée.")
                is ShareText -> ShareIntent.open(context, event.text, "Inviter un parent")
                HapticConfirm -> haptics.confirm(view)
                HapticReject -> haptics.reject(view)
            }
        }
    }
}
