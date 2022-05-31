package com.example.tinkoff.presentation.fragments.people.elm

import com.example.tinkoff.model.states.DataSource
import com.example.tinkoff.model.states.LoadingData
import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer

class UsersReducer :
    ScreenDslReducer<
        UsersEvent,
        UsersEvent.UI,
        UsersEvent.Internal,
        UsersState,
        UsersEffect,
        UsersCommand
        >(
        UsersEvent.UI::class,
        UsersEvent.Internal::class
    ) {

    override fun Result.internal(event: UsersEvent.Internal): Any {
        return when (event) {
            is UsersEvent.Internal.UsersLoaded -> {
                state {
                    copy(
                        usersList = event.users,
                        status = LoadingData.FINISHED,
                        needToSearch = true
                    )
                }
            }
            is UsersEvent.Internal.ErrorLoading -> {
                state {
                    copy(
                        status = LoadingData.ERROR,
                        needToSearch = false
                    )
                }
                effects {
                    +UsersEffect.UsersLoadError(event.error)
                }
                commands {
                    +UsersCommand.LoadUsers(DataSource.DATABASE)
                }
            }
            is UsersEvent.Internal.UsersFiltered -> {
                state {
                    copy(
                        usersList = event.users,
                        needToSearch = false
                    )
                }
            }
        }
    }

    override fun Result.ui(event: UsersEvent.UI): Any {
        return when (event) {
            is UsersEvent.UI.LoadUsers -> {
                state {
                    copy(
                        usersList = emptyList(),
                        status = LoadingData.LOADING,
                        needToSearch = false
                    )
                }
                commands {
                    +UsersCommand.LoadUsers(DataSource.INTERNET)
                }
            }
            is UsersEvent.UI.FilterUsers -> {
                commands {
                    +UsersCommand.FilterUsers(event.word)
                }
            }
            is UsersEvent.UI.InitUsers -> {
                commands {
                    +UsersCommand.InitUsers
                }
            }
        }
    }
}
