package com.example.tinkoff.model.network.api

import kotlinx.serialization.Serializable

@Serializable
data class StatusJson(
    val presence: PresenceJson
)
