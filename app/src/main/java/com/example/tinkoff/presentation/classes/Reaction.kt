package com.example.tinkoff.presentation.classes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Reaction(
    @SerialName("emoji_name")
    val emojiName: String,
    @SerialName("emoji_code")
    val emojiCode: String,
    @SerialName("users_list")
    val usersId: List<Int>,
    @Transient
    val isEnabled: Boolean = true
)
