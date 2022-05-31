package com.example.tinkoff.model.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tinkoff.presentation.classes.StreamHeader

@Entity(tableName = "Streams")
data class StreamHeaderRoom(
    @PrimaryKey @ColumnInfo(name = "stream_id") val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "stream_type") val type: Int
) {
    fun toStreamHeader(): StreamHeader = StreamHeader(
        id = id,
        name = name
    )
}
