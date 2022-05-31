package com.example.tinkoff.presentation.fragments.people.elm

import com.example.tinkoff.model.states.DataSource
import com.example.tinkoff.model.states.LoadingData
import com.example.tinkoff.presentation.classes.User

data class UsersState(
    val usersList: List<User>? = emptyList(),
    val status: LoadingData = LoadingData.LOADING,
    val needToSearch: Boolean = false
)

sealed class UsersEvent {
    sealed class UI : UsersEvent() {
        object LoadUsers : UI()

        object InitUsers : UI()

        data class FilterUsers(val word: String) : UI()
    }

    sealed class Internal : UsersEvent() {

        data class UsersLoaded(val users: List<User>?) : Internal()

        data class UsersFiltered(val users: List<User>?) : Internal()

        data class ErrorLoading(val error: Throwable) : Internal()
    }
}

sealed class UsersEffect {
    data class UsersLoadError(val error: Throwable) : UsersEffect()
}

sealed class UsersCommand {
    data class LoadUsers(val dataSource: DataSource) : UsersCommand()

    object InitUsers : UsersCommand()

    data class FilterUsers(val word: String) : UsersCommand()
}
