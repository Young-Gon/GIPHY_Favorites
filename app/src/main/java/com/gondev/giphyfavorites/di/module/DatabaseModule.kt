package com.gondev.giphyfavorites.di.module

import android.app.Application
import androidx.room.Room
import com.gondev.giphyfavorites.model.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(application: Application) =
        Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "GIPHY_Database"
        ).build()

    @Singleton
    @Provides
    fun provideFavoriteDao(appDatabase: AppDatabase) =
        appDatabase.getFavoriteDao()
}