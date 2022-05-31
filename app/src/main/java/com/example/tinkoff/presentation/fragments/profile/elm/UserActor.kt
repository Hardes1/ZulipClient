package com.example.tinkoff.presentation.fragments.profile.elm

import com.example.tinkoff.model.repositories.UserRepository
import com.example.tinkoff.model.repositoriesImplementation.UserRepositoryImpl
import io.reactivex.Observable
import vivid.money.elmslie.core.Actor

class UserActor : Actor<UserCommand, UserEvent.Internal> {
    private val repository: UserRepository by lazy {
        UserRepositoryImpl
    }

    override fun execute(command: UserCommand): Observable<UserEvent.Internal> {
        return when (command) {
            is UserCommand.LoadOwnUser -> {
                repository.getOwnUser().mapEvents(
                    eventMapper = { user -> UserEvent.Internal.UserLoaded(user) },
                    errorMapper = { e -> UserEvent.Internal.ErrorLoading(e) }
                )
            }
        }
    }
}
