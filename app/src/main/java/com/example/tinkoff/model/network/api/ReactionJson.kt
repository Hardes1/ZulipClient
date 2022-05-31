package com.example.tinkoff.model.network.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReactionJson(
    @SerialName("emoji_code")
    val emojiCode: String,
    @SerialName("emoji_name")
    val emojiName: String,
    @SerialName("reaction_type")
    val reactionType: String,
    @SerialName("user")
    val user: UserJson
)
