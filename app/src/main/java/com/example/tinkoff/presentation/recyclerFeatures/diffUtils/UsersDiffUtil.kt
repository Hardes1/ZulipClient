package com.example.tinkoff.presentation.recyclerFeatures.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.example.tinkoff.presentation.classes.User

class UsersDiffUtil : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}
