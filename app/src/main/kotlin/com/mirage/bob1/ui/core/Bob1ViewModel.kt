package org.bob1.app.ui.core

import android.app.Application
import dev.kindling.compose.KDestination
import dev.kindling.compose.KViewModel
import org.bob1.app.ui.core.navigation.Navigate
import org.bob1.app.ui.core.navigation.ShowMessage

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

    protected fun sendError(t: Throwable) = sendEvent(ShowMessage(t.userMessage(), isError = true))
    protected fun sendInfo(text: String) = sendEvent(ShowMessage(text, isError = false))
    protected fun navigate(destination: KDestination) = sendEvent(Navigate(destination))
}
