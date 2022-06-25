package com.example.tinkoff.presentation.fragments.people.elm

import com.example.tinkoff.presentation.applications.di.FragmentScope
import vivid.money.elmslie.core.ElmStoreCompat
import javax.inject.Inject

@FragmentScope
class UsersStoreFactory @Inject constructor(
    initialState: UsersState,
    reducer: UsersReducer,
    actor: UsersActor
) {
    val store =
        ElmStoreCompat(
            initialState = initialState,
            reducer = reducer,
            actor = actor
        )

    fun provide() = store
}
