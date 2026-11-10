package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class ObserveNotificationsUseCase @Inject constructor(private val repo: NotificationRepository) { operator fun invoke() = repo.observe() }
