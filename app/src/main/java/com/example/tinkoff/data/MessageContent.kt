package com.example.tinkoff.data

data class MessageContent(val id : Int, val content: String, val emotions: List<Reactions>, val type: SenderType) :
    MessageContentInterface

