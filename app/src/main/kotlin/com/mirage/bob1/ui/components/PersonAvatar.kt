package com.mirage.bob1.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import dev.kindling.core.components.ui.avatar.KAvatar
import dev.kindling.core.components.ui.avatar.KAvatarFallback
import dev.kindling.core.components.ui.avatar.KAvatarSize

@Composable
fun PersonAvatar(initials: String, size: KAvatarSize = KAvatarSize.Default) {
    KAvatar(size = size) { KAvatarFallback(size = size, initials = initials) }
}
