package com.example.tinkoff.presentation.fragments.stream.elm

import vivid.money.elmslie.core.ElmStoreCompat

class StreamsStoreFactory {
    private val store =
        ElmStoreCompat(
            initialState = StreamsState(),
            reducer = StreamsReducer(),
            actor = StreamsActor()
        )

    fun provide() = store
}
