package com.example.tinkoff.data.api

import kotlinx.serialization.Serializable

@Serializable
data class StatusJson(
    val presence: PresenceJson
)
