package com.example.tinkoff.model.network.api

import kotlinx.serialization.Serializable

@Serializable
data class PresenceJson(
    val aggregated: AggregatedJson
)
