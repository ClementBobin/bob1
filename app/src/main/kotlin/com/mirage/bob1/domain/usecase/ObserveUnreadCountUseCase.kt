package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class ObserveUnreadCountUseCase @Inject constructor(private val repo: NotificationRepository) { operator fun invoke() = repo.observeUnreadCount() }
