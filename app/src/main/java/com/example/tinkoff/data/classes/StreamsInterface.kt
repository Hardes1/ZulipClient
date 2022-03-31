package com.example.tinkoff.data.classes

sealed interface StreamsInterface {
    fun checkId(newItem: StreamsInterface): Boolean {
        val oldItem = this
        return if (oldItem is TopicHeader && newItem is TopicHeader) {
            oldItem.id == newItem.id
        } else if (oldItem is StreamHeader && newItem is StreamHeader)
            oldItem.id == newItem.id
        else
            false
    }
}