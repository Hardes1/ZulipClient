package com.example.tinkoff.model.network.api

import com.example.tinkoff.presentation.classes.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsersJson(
    @SerialName("members") val users: List<User>
)
