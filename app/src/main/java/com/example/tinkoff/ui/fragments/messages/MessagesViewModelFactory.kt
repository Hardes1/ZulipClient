package com.example.tinkoff.ui.fragments.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MessagesViewModelFactory(val streamHeader: String, val topicHeader: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessagesViewModel::class.java)) {
            return MessagesViewModel(streamHeader, topicHeader) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
