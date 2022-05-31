package com.example.tinkoff.presentation.fragments.stream.elm

import com.example.tinkoff.model.states.DataSource
import com.example.tinkoff.model.states.LoadingData
import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer

class StreamsReducer :
    ScreenDslReducer<StreamsEvent, StreamsEvent.UI, StreamsEvent.Internal, StreamsState, StreamsEffect, StreamsCommand>(
        StreamsEvent.UI::class,
        StreamsEvent.Internal::class
    ) {
    override fun Result.internal(event: StreamsEvent.Internal): Any {
        return when (event) {
            is StreamsEvent.Internal.StreamsLoaded -> {
                if (event.dataSource == DataSource.DATABASE) {
                    commands {
                        +StreamsCommand.LoadStreams(event.type, DataSource.INTERNET)
                    }
                }
                state {
                    copy(
                        streamsList = event.streams,
                        status =
                        if (event.streams.isNotEmpty()) {
                            LoadingData.FINISHED
                        } else {
                            LoadingData.LOADING
                        },
                        needToSearch = true
                    )
                }
            }
            is StreamsEvent.Internal.ErrorLoading -> {
                state {
                    copy(
                        status = LoadingData.ERROR,
                        needToSearch = false
                    )
                }
                effects {
                    +StreamsEffect.StreamsLoadError(event.error)
                }
            }
            is StreamsEvent.Internal.StreamsFiltered -> {
                state {
                    copy(
                        streamsList = event.streams,
                        status = LoadingData.FINISHED,
                        needToSearch = false
                    )
                }
            }
            is StreamsEvent.Internal.ErrorSaving -> {
                effects {
                    +StreamsEffect.StreamsSaveError(event.error)
                }
            }
        }
    }

    override fun Result.ui(event: StreamsEvent.UI): Any {
        return when (event) {
            is StreamsEvent.UI.LoadStreams -> {
                state {
                    copy(
                        streamsList = emptyList(),
                        status = LoadingData.LOADING,
                        needToSearch = false
                    )
                }
                commands {
                    +StreamsCommand.LoadStreams(event.type, DataSource.DATABASE)
                }
            }
            is StreamsEvent.UI.FilterStreams -> {
                commands {
                    +StreamsCommand.FilterStreams(event.type, event.word)
                }
            }
            is StreamsEvent.UI.SelectStream -> {
                commands {
                    +StreamsCommand.SelectStream(event.type, event.id, event.isSelected)
                }
            }
            is StreamsEvent.UI.Refresh -> {
                commands {
                    +StreamsCommand.Refresh(event.type)
                }
            }
        }
    }
}
