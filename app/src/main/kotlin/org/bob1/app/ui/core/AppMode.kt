package org.bob1.app.ui.core

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** A person with both the Parent and Staff roles switches between the two experiences (see the design's role picker). */
enum class AppMode { Parent, Staff }

@Singleton
class AppModeStore @Inject constructor() {
    private val _mode = MutableStateFlow(AppMode.Parent)
    val mode: StateFlow<AppMode> = _mode.asStateFlow()
    fun set(mode: AppMode) { _mode.value = mode }
}
