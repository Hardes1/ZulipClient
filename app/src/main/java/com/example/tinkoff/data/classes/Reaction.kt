package com.example.tinkoff.data.classes

data class Reaction(
    val emojiName: String,
    val emojiCode: String,
    val usersId: List<Int>
)
