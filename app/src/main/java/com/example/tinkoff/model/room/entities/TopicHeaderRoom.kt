package com.example.tinkoff.model.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.tinkoff.presentation.classes.TopicHeader

@Entity(
    tableName = "Topics",
    foreignKeys = [
        ForeignKey(
            entity = StreamHeaderRoom::class,
            parentColumns = ["stream_id"],
            childColumns = ["stream_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class TopicHeaderRoom(
    @PrimaryKey @ColumnInfo(name = "topic_id") val id: Int,
    @ColumnInfo(name = "stream_id") val streamId: Int,
    @ColumnInfo(name = "name") val name: String
) {
    fun toTopicHeader() = TopicHeader(
        id = id,
        streamId = streamId,
        name = name
    )
}
