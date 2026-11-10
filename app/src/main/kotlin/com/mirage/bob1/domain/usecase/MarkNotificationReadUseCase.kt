package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.*

class MarkNotificationReadUseCase @Inject constructor(private val repo: NotificationRepository) { suspend operator fun invoke(id: String) = repo.markRead(id) }
