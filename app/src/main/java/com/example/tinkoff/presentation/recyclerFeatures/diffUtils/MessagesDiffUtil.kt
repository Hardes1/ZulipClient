package com.example.tinkoff.presentation.recyclerFeatures.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.example.tinkoff.presentation.classes.MessageContent
import com.example.tinkoff.presentation.classes.MessageContentInterface
import com.example.tinkoff.presentation.classes.MessageDate

class MessagesDiffUtil : DiffUtil.ItemCallback<MessageContentInterface>() {

    override fun areItemsTheSame(
        oldItem: MessageContentInterface,
        newItem: MessageContentInterface
    ): Boolean {
        return if (oldItem is MessageContent && newItem is MessageContent) {
            oldItem.id == newItem.id && oldItem.type == newItem.type
        } else if (oldItem is MessageDate && newItem is MessageDate) {
            oldItem.id == newItem.id
        } else {
            false
        }
    }

    override fun areContentsTheSame(
        oldItem: MessageContentInterface,
        newItem: MessageContentInterface
    ): Boolean {
        return oldItem == newItem
    }
}
