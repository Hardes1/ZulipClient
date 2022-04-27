package com.example.tinkoff.data.classes

import com.example.tinkoff.data.states.SenderType

data class MessageContent(
    val id: Int,
    val content: String,
    val senderName: String,
    val avatarUrl: String?,
    var reactions: List<Reaction>,
    val type: SenderType
) :
    MessageContentInterface
