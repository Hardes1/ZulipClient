package com.example.tinkoff.recyclerFeatures.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.example.tinkoff.data.classes.StreamsInterface

class StreamsDiffUtil : DiffUtil.ItemCallback<StreamsInterface>() {
    override fun areItemsTheSame(oldItem: StreamsInterface, newItem: StreamsInterface): Boolean {
        return oldItem.checkId(newItem)
    }

    override fun areContentsTheSame(oldItem: StreamsInterface, newItem: StreamsInterface): Boolean {
        return oldItem == newItem
    }
}
