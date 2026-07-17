package com.princelaghari.ailatestfinder.di

import com.princelaghari.ailatestfinder.data.repository.FirebaseFirestoreRepositoryImpl
import com.princelaghari.ailatestfinder.domain.repository.AiToolRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindAiToolRepository(
        repositoryImpl: FirebaseFirestoreRepositoryImpl
    ): AiToolRepository
}
