package com.example.tinkoff.presentation.fragments.messages.elm

import vivid.money.elmslie.core.ElmStoreCompat
import javax.inject.Inject

class MessagesStoreFactory @Inject constructor(
    messagesState: MessagesState,
    reducer: MessagesReducer,
    actor: MessagesActor
) {
    private val store =
        ElmStoreCompat(
            initialState = messagesState,
            reducer = reducer,
            actor = actor
        )

    fun provide() = store
}
