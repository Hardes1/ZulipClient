package com.example.tinkoff.data.api

import androidx.annotation.Nullable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageJson(
    @Nullable
    @SerialName("avatar_url")
    val avatarUrl: String?,
    @SerialName("content")
    val content: String,
    @SerialName("content_type")
    val contentType: String,
    @SerialName("id")
    val id: Int,
    @SerialName("is_me_message")
    val isMeMessage: Boolean,
    @SerialName("reactions")
    val reactions: List<ReactionJson>,
    @SerialName("sender_email")
    val senderEmail: String,
    @SerialName("sender_full_name")
    val senderFullName: String,
    @SerialName("sender_id")
    val senderId: Int,
    @SerialName("stream_id")
    val streamId: Int,
    @SerialName("subject")
    val subject: String,
    @SerialName("timestamp")
    val timestamp: Long,
)
