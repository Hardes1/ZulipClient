package com.example.tinkoff.model.network.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendCallBackJson(
    @SerialName("id")
    val id: Int,
)
