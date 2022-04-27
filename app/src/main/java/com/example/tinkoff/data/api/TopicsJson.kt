package com.example.tinkoff.data.api

import com.example.tinkoff.data.classes.TopicHeader
import kotlinx.serialization.Serializable

@Serializable
data class TopicsJson(
    val topics: List<TopicHeader>
)
