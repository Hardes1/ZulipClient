package com.example.tinkoff.model.network.api

import com.example.tinkoff.model.network.serializers.StatusSerializer
import com.example.tinkoff.model.states.UserStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AggregatedJson(
    @Serializable(StatusSerializer::class)
    @SerialName("status")
    val status: UserStatus,
    @SerialName("timestamp")
    val timestamp: Int
)
