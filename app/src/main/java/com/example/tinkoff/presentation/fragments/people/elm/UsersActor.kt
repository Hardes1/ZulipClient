package com.example.tinkoff.presentation.fragments.people.elm

import com.example.tinkoff.model.repositories.UsersRepository
import com.example.tinkoff.model.states.DataSource
import io.reactivex.Observable
import vivid.money.elmslie.core.Actor
import vivid.money.elmslie.core.switcher.Switcher
import vivid.money.elmslie.core.switcher.observable
import javax.inject.Inject

class UsersActor @Inject constructor(
    private val repository: UsersRepository,
    private val filterSwitcher: Switcher,
    private val dataSwitcher: Switcher
) : Actor<UsersCommand, UsersEvent.Internal> {

    override fun execute(command: UsersCommand): Observable<UsersEvent.Internal> {
        return when (command) {
            is UsersCommand.LoadUsers -> {
                dataSwitcher.observable(DEBOUNCE_TIME) {
                    when (command.dataSource) {
                        DataSource.INTERNET -> repository.getUsersFromInternet()
                        DataSource.DATABASE -> repository.getUsersFromCache()
                    }
                        .mapEvents(
                            eventMapper = { users ->
                                UsersEvent.Internal.UsersLoaded(users = users)
                            },
                            errorMapper = {
                                UsersEvent.Internal.ErrorLoading(it)
                            }
                        )
                }
            }
            is UsersCommand.FilterUsers -> {
                filterSwitcher.observable(DEBOUNCE_TIME) {
                    repository.filterUsersByString(command.word)
                        .mapEvents(
                            eventMapper = { users ->
                                UsersEvent.Internal.UsersFiltered(users = users)
                            },
                            errorMapper = { e ->
                                UsersEvent.Internal.ErrorLoading(e)
                            }
                        )
                }
            }
            is UsersCommand.InitUsers -> {
                repository.init().mapEvents(
                    eventMapper = { users ->
                        UsersEvent.Internal.UsersLoaded(users = users)
                    },
                    errorMapper = { e ->
                        UsersEvent.Internal.ErrorLoading(e)
                    }
                )
            }
        }
    }

    companion object {
        private const val DEBOUNCE_TIME: Long = 300
    }
}
