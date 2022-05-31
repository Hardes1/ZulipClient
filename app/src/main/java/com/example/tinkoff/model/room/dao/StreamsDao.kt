package com.example.tinkoff.model.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tinkoff.model.room.entities.StreamHeaderRoom
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface StreamsDao {
    @Query("SELECT * FROM Streams WHERE stream_type IN (:type)")
    fun getStreamsByType(type: List<Int>): Single<List<StreamHeaderRoom>>

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = StreamHeaderRoom::class)
    fun insertStreams(list: List<StreamHeaderRoom>): Completable

    @Query("DELETE FROM Streams WHERE stream_type = :type")
    fun deleteStreamsByType(type: Int): Completable

    @Query("UPDATE Streams SET stream_type = :type WHERE stream_id IN (:listOfIndexes)")
    fun updatedStreams(type: Int, listOfIndexes: List<Int>): Completable
}
