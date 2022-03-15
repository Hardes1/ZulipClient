package com.example.tinkoff.data

data class MessageContent(val content: String, val emotions: List<Emoji>, val type: SenderType) :
    MessageContentInterface

