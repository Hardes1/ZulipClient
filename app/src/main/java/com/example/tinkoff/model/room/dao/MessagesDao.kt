package com.example.tinkoff.model.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tinkoff.model.room.entities.MessageContentRoom
import com.example.tinkoff.presentation.classes.Reaction
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface MessagesDao {
    @Query(
        "SELECT * FROM MessageContent WHERE stream_name = :streamName " +
            "AND topic_name = :topicName ORDER BY timestamp DESC LIMIT 50"
    )
    fun getMessagesByStreamAndTopic(
        streamName: String,
        topicName: String
    ): Single<List<MessageContentRoom>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessages(list: List<MessageContentRoom>): Completable

    @Query(
        "DELETE FROM MessageContent WHERE stream_name = :streamName" +
            " AND topic_name = :topicName"
    )
    fun deleteMessagesByStreamAndTopic(streamName: String, topicName: String): Completable

    @Query("UPDATE MessageContent SET reactions = :reactions WHERE message_id = :messageId")
    fun updateMessageReaction(messageId: Int, reactions: List<Reaction>): Completable
}
