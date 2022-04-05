package com.example.tinkoff.ui.fragments.messages

import androidx.lifecycle.ViewModel
import com.example.tinkoff.data.classes.MessageContentInterface

class MessagesViewModel : ViewModel() {
    var messagesList: MutableList<MessageContentInterface>? = null
}