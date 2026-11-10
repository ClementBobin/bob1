package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.StaffRepository

class GetMyPointEntriesUseCase @Inject constructor(private val repo: StaffRepository) { suspend operator fun invoke() = repo.myPointEntries() }
