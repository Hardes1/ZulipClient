package com.example.tinkoff.model.room.client

import android.content.Context
import androidx.room.Room
import com.example.tinkoff.model.room.dao.MessagesDao
import com.example.tinkoff.model.room.dao.StreamsDao
import com.example.tinkoff.model.room.dao.TopicsDao
import com.example.tinkoff.model.room.database.AppDatabase

object RoomClient {
    private lateinit var database: AppDatabase

    fun initDatabase(context: Context) {
        database = Room
            .databaseBuilder(context, AppDatabase::class.java, "database.db")
            .build()
    }

    fun getStreamsDao(): StreamsDao {
        return database.getStreamsDao()
    }

    fun getTopicsDao(): TopicsDao {
        return database.getTopicsDao()
    }

    fun getMessagesDao(): MessagesDao {
        return database.getMessagesDao()
    }
}
