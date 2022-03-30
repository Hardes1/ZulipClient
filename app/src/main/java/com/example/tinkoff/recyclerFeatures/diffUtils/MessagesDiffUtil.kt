package com.example.tinkoff.recyclerFeatures.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.example.tinkoff.data.classes.Date
import com.example.tinkoff.data.classes.MessageContent
import com.example.tinkoff.data.classes.MessageContentInterface

class MessagesDiffUtil : DiffUtil.ItemCallback<MessageContentInterface>() {

    override fun areItemsTheSame(
        oldItem: MessageContentInterface,
        newItem: MessageContentInterface
    ): Boolean {
        return oldItem.checkId(newItem)
    }

    override fun areContentsTheSame(
        oldItem: MessageContentInterface,
        newItem: MessageContentInterface
    ): Boolean {
        return oldItem == newItem
    }

}
