package org.bob1.capacity.network

import android.annotation.SuppressLint
import android.content.Context
import dev.kindling.android.natif.ConnectivityHelper
import dev.kindling.android.natif.NetworkStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/** Online / offline as a simple boolean flow (ACCESS_NETWORK_STATE is declared in this module's manifest). */
@SuppressLint("MissingPermission")
class NetworkMonitor(context: Context) {
    private val helper = ConnectivityHelper(context)

    val online: Flow<Boolean> = helper.statusFlow
        .map { it is NetworkStatus.Available }
        .distinctUntilChanged()

    fun isOnline(): Boolean = helper.isOnline()
}
