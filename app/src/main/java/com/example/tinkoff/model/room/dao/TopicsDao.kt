package com.example.tinkoff.model.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tinkoff.model.room.entities.TopicHeaderRoom
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface TopicsDao {
    @Query("SELECT * FROM Topics WHERE stream_id IN (:streamId)")
    fun getTopicsByStream(streamId: List<Int>): Single<List<TopicHeaderRoom>>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = TopicHeaderRoom::class)
    fun insertTopics(list: List<TopicHeaderRoom>): Completable

    @Query("DELETE FROM Topics WHERE stream_id = :streamId")
    fun deleteTopicsByStreamId(streamId: Int): Completable
}
