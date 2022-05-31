package com.example.tinkoff.presentation.classes

data class Stream(
    val streamHeader: StreamHeader,
    val topics: List<TopicHeader>
)
