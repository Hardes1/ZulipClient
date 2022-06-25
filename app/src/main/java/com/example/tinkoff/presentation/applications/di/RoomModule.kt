package com.example.tinkoff.presentation.applications.di

import android.content.Context
import androidx.room.Room
import com.example.tinkoff.model.room.dao.MessagesDao
import com.example.tinkoff.model.room.dao.StreamsDao
import com.example.tinkoff.model.room.dao.TopicsDao
import com.example.tinkoff.model.room.database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {
    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, "database.db")
            .build()
    }

    @Provides
    @Singleton
    fun provideTopicsDao(database: AppDatabase): TopicsDao {
        return database.getTopicsDao()
    }

    @Provides
    @Singleton
    fun provideStreamsDao(database: AppDatabase): StreamsDao {
        return database.getStreamsDao()
    }

    @Provides
    @Singleton
    fun provideMessagesDao(database: AppDatabase): MessagesDao {
        return database.getMessagesDao()
    }
}
