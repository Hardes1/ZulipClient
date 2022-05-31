package com.example.tinkoff.model.network.api

import com.example.tinkoff.presentation.classes.TopicHeader
import kotlinx.serialization.Serializable

@Serializable
data class TopicsJson(
    val topics: List<TopicHeader>
)
