package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class MarkAllNotificationsReadUseCase @Inject constructor(private val repo: NotificationRepository) { suspend operator fun invoke() = repo.markAllRead() }
