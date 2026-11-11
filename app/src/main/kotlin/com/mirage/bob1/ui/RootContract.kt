package com.mirage.bob1.ui

import com.mirage.bob1.domain.model.UserRole
import com.mirage.bob1.ui.core.AppMode

/** `authenticated`: null while the encrypted session is being read. */
data class RootState(val authenticated: Boolean? = null, val roles: Set<UserRole>? = null, val mode: AppMode = AppMode.Parent)
