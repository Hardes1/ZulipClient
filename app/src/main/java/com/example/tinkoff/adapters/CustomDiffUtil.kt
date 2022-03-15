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
        if (oldItem is MessageContent && newItem is MessageContent) {
            return oldItem.id == newItem.id
        } else if (oldItem is Date && newItem is Date) {
            return oldItem.id == newItem.id
        }
        return false
    }

    override fun areContentsTheSame(
        oldItem: MessageContentInterface,
        newItem: MessageContentInterface
    ): Boolean {
        if (oldItem is MessageContent && newItem is MessageContent) {
            var flag =
                oldItem.id == newItem.id &&
                        oldItem.type == newItem.type &&
                        oldItem.content == newItem.content &&
                        oldItem.emotions.size == newItem.emotions.size
            for (i in 0 until oldItem.emotions.size)
                flag = flag and (oldItem.emotions[i] == newItem.emotions[i])
            return flag
        } else if (oldItem is Date && newItem is Date) {
            return oldItem.id == newItem.id && oldItem.date == newItem.date
        }
        return false
    }
}