package com.example.tinkoff.presentation.fragments.stream.elm

import com.example.tinkoff.model.repositories.StreamsRepository
import com.example.tinkoff.model.repositoriesImplementation.StreamsRepositoryImpl
import com.example.tinkoff.model.states.DataSource
import com.example.tinkoff.model.storagesImplementation.StreamsStorageImpl
import io.reactivex.Observable
import vivid.money.elmslie.core.Actor
import vivid.money.elmslie.core.switcher.Switcher
import vivid.money.elmslie.core.switcher.observable

class StreamsActor : Actor<StreamsCommand, StreamsEvent> {
    private val filterSwitcher = Switcher()
    private val repository: StreamsRepository by lazy {
        StreamsRepositoryImpl(StreamsStorageImpl())
    }

    override fun execute(command: StreamsCommand): Observable<StreamsEvent> {
        return when (command) {
            is StreamsCommand.LoadStreams -> {
                repository.getStreamsFromSource(command.type, command.dataSource, false).mapEvents(
                    eventMapper = { streams ->
                        StreamsEvent.Internal.StreamsLoaded(
                            command.type,
                            streams,
                            command.dataSource
                        )
                    },
                    errorMapper = { error ->
                        StreamsEvent.Internal.ErrorLoading(error)
                    }
                )
            }
            is StreamsCommand.FilterStreams -> {
                filterSwitcher.observable(DEBOUNCE_TIME) {
                    repository.getFilteredStreams(command.word).mapEvents(
                        eventMapper = { streams ->
                            StreamsEvent.Internal.StreamsLoaded(
                                command.type,
                                streams,
                                DataSource.INTERNET
                            )
                        },
                        errorMapper = { error ->
                            StreamsEvent.Internal.ErrorLoading(error)
                        }
                    )
                }
            }
            is StreamsCommand.SelectStream -> {
                repository.selectStreamsById(command.id, command.isSelected).mapEvents(
                    eventMapper = { streams ->
                        StreamsEvent.Internal.StreamsLoaded(
                            command.type,
                            streams,
                            DataSource.INTERNET
                        )
                    },
                    errorMapper = { error ->
                        StreamsEvent.Internal.ErrorSaving(error)
                    }
                )
            }
            is StreamsCommand.Refresh -> {
                repository.getStreamsFromSource(command.type, DataSource.INTERNET, true)
                    .mapEvents(
                        eventMapper = { streams ->
                            StreamsEvent.Internal.StreamsLoaded(
                                command.type,
                                streams,
                                DataSource.INTERNET
                            )
                        },
                        errorMapper = { error ->
                            StreamsEvent.Internal.ErrorLoading(error)
                        }
                    )
            }
        }
    }

    companion object {
        private const val DEBOUNCE_TIME: Long = 300
    }
}
