package com.example.tinkoff.model.network.api

import com.example.tinkoff.presentation.classes.StreamHeader
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class StreamsJson(
    @JsonNames("subscriptions")
    val streams: List<StreamHeader>
)
