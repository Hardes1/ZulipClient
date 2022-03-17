package com.example.tinkoff.data

data class MessageContent(val id : Int, val content: String,
                          val reactions: MutableList<Reaction>,
                          val type: SenderType) :
    MessageContentInterface


