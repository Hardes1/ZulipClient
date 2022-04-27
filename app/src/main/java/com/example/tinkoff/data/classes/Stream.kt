package com.example.tinkoff.data.classes

data class Stream(
    val streamHeader: StreamHeader,
    val topics: List<TopicHeader>
)
