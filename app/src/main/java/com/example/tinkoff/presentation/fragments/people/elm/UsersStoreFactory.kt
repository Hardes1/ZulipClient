package com.example.tinkoff.presentation.fragments.people.elm

import vivid.money.elmslie.core.ElmStoreCompat

class UsersStoreFactory {
    private val store =
        ElmStoreCompat(
            initialState = UsersState(),
            reducer = UsersReducer(),
            actor = UsersActor()
        )

    fun provide() = store
}
