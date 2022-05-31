package com.example.tinkoff.presentation.fragments.profile.elm

import com.example.tinkoff.model.states.LoadingData
import com.example.tinkoff.presentation.classes.User

data class UserState(
    val user: User? = null,
    val status: LoadingData = LoadingData.LOADING,
    val isRefreshVisible: Boolean = true
)

sealed class UserEvent {
    sealed class UI : UserEvent() {
        data class InitUser(val user: User?) : UI()
    }

    sealed class Internal : UserEvent() {

        data class UserLoaded(val user: User) : Internal()

        data class ErrorLoading(val error: Throwable) : Internal()
    }
}

sealed class UserEffect {
    data class UserLoadError(val error: Throwable) : UserEffect()
}

sealed class UserCommand {
    object LoadOwnUser : UserCommand()
}
