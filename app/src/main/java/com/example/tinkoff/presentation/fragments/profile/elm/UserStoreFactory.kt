package com.example.tinkoff.presentation.fragments.profile.elm

import vivid.money.elmslie.core.ElmStoreCompat

class UserStoreFactory {

    private val store by lazy {
        ElmStoreCompat(
            initialState = UserState(),
            reducer = UserReducer(),
            actor = UserActor()
        )
    }

    fun provide() = store
}
