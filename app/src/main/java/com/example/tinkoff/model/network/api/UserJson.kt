package com.example.tinkoff.model.network.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserJson(
    @SerialName("email")
    val email: String,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("id")
    val id: Int
)
