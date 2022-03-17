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
        return if (oldItem is MessageContent && newItem is MessageContent) {
            var flag =
                oldItem.id == newItem.id &&
                        oldItem.type == newItem.type &&
                        oldItem.content == newItem.content &&
                        oldItem.reactions.size == newItem.reactions.size
            if (flag) {
                for (i in 0 until oldItem.reactions.size) {
                    flag = flag and (oldItem.reactions[i] == newItem.reactions[i])
                }
            }
            flag
        } else if (oldItem is Date && newItem is Date) {
            oldItem.id == newItem.id && oldItem.date == newItem.date
        } else
            false
    }

}
