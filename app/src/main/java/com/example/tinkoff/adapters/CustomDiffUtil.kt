package com.example.tinkoff.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.tinkoff.data.Date
import com.example.tinkoff.data.MessageContent
import com.example.tinkoff.data.MessageContentInterface

class CustomDiffUtil : DiffUtil.ItemCallback<MessageContentInterface>() {

    override fun areItemsTheSame(
        oldItem: MessageContentInterface,
        newItem: MessageContentInterface
    ): Boolean {
        return if (oldItem is MessageContent && newItem is MessageContent) {
            oldItem.id == newItem.id && oldItem.type == newItem.type
        } else if (oldItem is Date && newItem is Date) {
            oldItem.id == newItem.id
        } else
            false
    }

    override fun areContentsTheSame(
        oldItem: MessageContentInterface,
        newItem: MessageContentInterface
    ): Boolean {
        return oldItem == newItem
    }

}
