package com.example.tinkoff.data.api

import com.example.tinkoff.data.states.UserStatus
import com.example.tinkoff.network.serializers.StatusSerializer
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
