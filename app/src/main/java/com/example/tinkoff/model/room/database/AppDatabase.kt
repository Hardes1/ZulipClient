package com.example.tinkoff.model.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tinkoff.model.room.converters.ReactionListConverter
import com.example.tinkoff.model.room.dao.MessagesDao
import com.example.tinkoff.model.room.dao.StreamsDao
import com.example.tinkoff.model.room.dao.TopicsDao
import com.example.tinkoff.model.room.entities.MessageContentRoom
import com.example.tinkoff.model.room.entities.StreamHeaderRoom
import com.example.tinkoff.model.room.entities.TopicHeaderRoom

@Database(
    entities = [StreamHeaderRoom::class, TopicHeaderRoom::class, MessageContentRoom::class],
    version = 1
)
@TypeConverters(ReactionListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getStreamsDao(): StreamsDao

    abstract fun getTopicsDao(): TopicsDao

    abstract fun getMessagesDao(): MessagesDao
}
