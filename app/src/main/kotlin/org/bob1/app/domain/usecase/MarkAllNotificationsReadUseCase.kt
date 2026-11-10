package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.*

class MarkAllNotificationsReadUseCase @Inject constructor(private val repo: NotificationRepository) { suspend operator fun invoke() = repo.markAllRead() }
