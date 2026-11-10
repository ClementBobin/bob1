package org.bob1.app.domain.usecase

import java.time.Instant
import javax.inject.Inject
import org.bob1.app.domain.repository.StaffRepository

class GetOpenStaffMatchesUseCase @Inject constructor(private val repo: StaffRepository) {
    suspend operator fun invoke(from: Instant? = null, to: Instant? = null) = repo.openSlots(from, to)
}
