package com.mirage.bob1.ui.core

import android.app.Application
import dev.kindling.compose.KDestination
import dev.kindling.compose.KViewModel
import com.mirage.bob1.data.remote.HttpException
import com.mirage.bob1.ui.core.navigation.Navigate
import com.mirage.bob1.ui.core.navigation.ShowMessage

/**
 * Kindling's [KViewModel] (state + one-shot events + fetchData) plus the two things every Bob1 screen does:
 * run a use case that returns [Result], and surface failures as a toast.
 */
abstract class Bob1ViewModel<S>(initialState: S, application: Application) : KViewModel<S>(initialState, application) {

    protected fun <T> call(
        block: suspend () -> Result<T>,
        onError: (Throwable) -> Unit = ::sendError,
        onOk: (T) -> Unit = {},
    ) {
        fetchData(source = { block().getOrThrow() }) {
            onSuccess(onOk)
            onFailure(onError)
        }
    }

    /** HTTP failures were already shown (toast + vibration) by the Ktor client; only the other errors are toasted here. */
    protected fun sendError(t: Throwable) {
        if (t is HttpException) return
        sendEvent(ShowMessage(t.userMessage(), isError = true))
    }
    protected fun sendInfo(text: String) = sendEvent(ShowMessage(text, isError = false))
    protected fun navigate(destination: KDestination) = sendEvent(Navigate(destination))
}
