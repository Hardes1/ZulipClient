package com.example.tinkoff.presentation.fragments.profile.elm

import vivid.money.elmslie.core.ElmStoreCompat
import javax.inject.Inject

class UserStoreFactory @Inject constructor(
    private val userState: UserState,
    private val reducer: UserReducer,
    private val actor: UserActor
) {

    private val store by lazy {
        ElmStoreCompat(
            initialState = userState,
            reducer = reducer,
            actor = actor
        )
    }

    fun provide() = store
}
