package com.example.tinkoff.data.api

import com.example.tinkoff.data.classes.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsersJson(
    @SerialName("members") val users: List<User>
)
