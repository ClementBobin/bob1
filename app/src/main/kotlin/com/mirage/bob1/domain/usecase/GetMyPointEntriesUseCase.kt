package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.StaffRepository

class GetMyPointEntriesUseCase @Inject constructor(private val repo: StaffRepository) { suspend operator fun invoke() = repo.myPointEntries() }
