package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.*

class ObserveUnreadCountUseCase @Inject constructor(private val repo: NotificationRepository) { operator fun invoke() = repo.observeUnreadCount() }
