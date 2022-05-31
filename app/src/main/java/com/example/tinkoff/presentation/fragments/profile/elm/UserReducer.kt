package com.example.tinkoff.presentation.fragments.profile.elm

import com.example.tinkoff.model.states.LoadingData
import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer

class UserReducer :
    ScreenDslReducer<UserEvent, UserEvent.UI, UserEvent.Internal, UserState, UserEffect, UserCommand>(
        UserEvent.UI::class,
        UserEvent.Internal::class
    ) {
    override fun Result.internal(event: UserEvent.Internal): Any {
        return when (event) {
            is UserEvent.Internal.UserLoaded -> {
                state {
                    copy(
                        user = event.user,
                        status = LoadingData.FINISHED,
                        isRefreshVisible = false
                    )
                }
            }
            is UserEvent.Internal.ErrorLoading -> {
                state {
                    copy(
                        user = null,
                        status = LoadingData.ERROR,
                        isRefreshVisible = true
                    )
                }
                effects {
                    +UserEffect.UserLoadError(event.error)
                }
            }
        }
    }

    override fun Result.ui(event: UserEvent.UI): Any {
        return when (event) {
            is UserEvent.UI.InitUser -> {
                if (event.user != null) {
                    state {
                        copy(
                            user = event.user,
                            status = LoadingData.FINISHED,
                            isRefreshVisible = false
                        )
                    }
                } else {
                    state {
                        copy(
                            user = null,
                            status = LoadingData.LOADING,
                            isRefreshVisible = false
                        )
                    }
                    commands { +UserCommand.LoadOwnUser }
                }
            }
        }
    }
}
