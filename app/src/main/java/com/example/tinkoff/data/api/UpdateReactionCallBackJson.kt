package com.example.tinkoff.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateReactionCallBackJson(
    @SerialName("msg")
    val msg: String,
    @SerialName("result")
    val result: String
)
