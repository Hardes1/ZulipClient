package com.example.tinkoff.presentation.fragments.messages.elm

import vivid.money.elmslie.core.ElmStoreCompat

class MessagesStoreFactory {
    private val store =
        ElmStoreCompat(
            initialState = MessagesState(),
            reducer = MessagesReducer(),
            actor = MessagesActor()
        )

    fun provide() = store
}
