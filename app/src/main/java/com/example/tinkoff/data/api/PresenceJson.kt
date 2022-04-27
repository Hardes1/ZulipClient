package com.example.tinkoff.data.api

import kotlinx.serialization.Serializable

@Serializable
data class PresenceJson(
    val aggregated: AggregatedJson
)
