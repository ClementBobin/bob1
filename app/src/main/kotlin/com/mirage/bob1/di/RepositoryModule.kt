package com.mirage.bob1.di

import com.mirage.bob1.data.repository.*
import com.mirage.bob1.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {
    @Binds abstract fun auth(impl: AuthRepositoryImpl): AuthRepository
    @Binds abstract fun matches(impl: MatchRepositoryImpl): MatchRepository
    @Binds abstract fun carpool(impl: CarpoolRepositoryImpl): CarpoolRepository
    @Binds abstract fun club(impl: ClubRepositoryImpl): ClubRepository
    @Binds abstract fun family(impl: FamilyRepositoryImpl): FamilyRepository
    @Binds abstract fun commitments(impl: CommitmentRepositoryImpl): CommitmentRepository
    @Binds abstract fun ranking(impl: RankingRepositoryImpl): RankingRepository
    @Binds abstract fun notifications(impl: NotificationRepositoryImpl): NotificationRepository
    @Binds abstract fun push(impl: PushRepositoryImpl): PushRepository
    @Binds abstract fun staff(impl: StaffRepositoryImpl): StaffRepository
}
