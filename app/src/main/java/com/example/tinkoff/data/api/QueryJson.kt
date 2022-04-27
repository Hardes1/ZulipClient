package com.example.tinkoff.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QueryJson(
    @SerialName("operator")
    val operator: String,
    @SerialName("operand")
    val operand: String
)
