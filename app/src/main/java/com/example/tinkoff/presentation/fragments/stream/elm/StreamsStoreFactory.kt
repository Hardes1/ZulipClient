package com.example.tinkoff.presentation.fragments.stream.elm

import vivid.money.elmslie.core.ElmStoreCompat
import javax.inject.Inject

class StreamsStoreFactory @Inject constructor(
    private val streamState: StreamsState,
    private val reducer: StreamsReducer,
    private val actor: StreamsActor
) {
    private val store =
        ElmStoreCompat(
            initialState = streamState,
            reducer = reducer,
            actor = actor
        )

    fun provide() = store
}
