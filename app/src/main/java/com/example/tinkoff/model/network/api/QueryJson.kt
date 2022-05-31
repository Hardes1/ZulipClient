package com.example.tinkoff.model.network.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QueryJson(
    @SerialName("operator")
    val operator: String,
    @SerialName("operand")
    val operand: String
)
