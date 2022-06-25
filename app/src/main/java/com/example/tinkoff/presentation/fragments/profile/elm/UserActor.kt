package com.example.tinkoff.presentation.fragments.profile.elm

import com.example.tinkoff.model.repositories.UserRepository
import io.reactivex.Observable
import vivid.money.elmslie.core.Actor

class UserActor(private val repository: UserRepository) : Actor<UserCommand, UserEvent.Internal> {

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
