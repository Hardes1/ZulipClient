package com.example.tinkoff.recyclerFeatures.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.example.tinkoff.data.classes.StreamHeader
import com.example.tinkoff.data.classes.StreamsInterface
import com.example.tinkoff.data.classes.TopicHeader

class StreamsDiffUtil : DiffUtil.ItemCallback<StreamsInterface>() {
    override fun areItemsTheSame(oldItem: StreamsInterface, newItem: StreamsInterface): Boolean {
        return if (oldItem is TopicHeader && newItem is TopicHeader) {
            oldItem.id == newItem.id
        } else if (oldItem is StreamHeader && newItem is StreamHeader)
            oldItem.id == newItem.id
        else
            false
    }

    override fun areContentsTheSame(oldItem: StreamsInterface, newItem: StreamsInterface): Boolean {
        return oldItem == newItem
    }
}
