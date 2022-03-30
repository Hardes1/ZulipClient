package com.example.tinkoff.data.classes

sealed interface MessageContentInterface {
    fun checkId(newItem: MessageContentInterface): Boolean {
        val oldItem = this
        return if (oldItem is MessageContent && newItem is MessageContent) {
            oldItem.id == newItem.id && oldItem.type == newItem.type
        } else if (oldItem is Date && newItem is Date) {
            oldItem.id == newItem.id
        } else
            false
    }

}
