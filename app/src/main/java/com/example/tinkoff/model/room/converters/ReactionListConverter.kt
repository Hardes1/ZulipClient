package com.example.tinkoff.model.room.converters

import androidx.room.TypeConverter
import com.example.tinkoff.presentation.classes.Reaction
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ReactionListConverter {
    @TypeConverter
    fun fromListReactionToString(list: List<Reaction>): String {
        return Json.encodeToString(list)
    }

    @TypeConverter
    fun fromStringToReactionList(str: String): List<Reaction> {
        return Json.decodeFromString(str)
    }
}
