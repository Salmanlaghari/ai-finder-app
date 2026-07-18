package com.princelaghari.ailatestfinder.di

import android.content.Context
import androidx.room.Room
import com.princelaghari.ailatestfinder.data.local.AiLatestFinderDatabase
import com.princelaghari.ailatestfinder.data.local.dao.AiToolDao
import com.princelaghari.ailatestfinder.data.repository.FirebaseFirestoreRepositoryImpl
import com.princelaghari.ailatestfinder.domain.repository.AiToolRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    companion object {

        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): AiLatestFinderDatabase {
            return Room.databaseBuilder(
                context,
                AiLatestFinderDatabase::class.java,
                "ai_latest_finder_db"
            )
            .fallbackToDestructiveMigration()
            .build()
        }

        @Provides
        @Singleton
        fun provideAiToolDao(database: AiLatestFinderDatabase): AiToolDao {
            return database.aiToolDao()
        }
    }
}
