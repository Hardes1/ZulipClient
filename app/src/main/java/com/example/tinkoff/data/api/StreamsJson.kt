package com.example.tinkoff.data.api

import com.example.tinkoff.data.classes.StreamHeader
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class StreamsJson(
    @JsonNames("subscriptions")
    val streams: List<StreamHeader>
)
