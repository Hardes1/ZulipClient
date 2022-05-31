package com.example.tinkoff.presentation.fragments.messages.elm

import com.example.tinkoff.model.states.DataSource
import com.example.tinkoff.model.states.LoadingData
import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer

class MessagesReducer :
    ScreenDslReducer<
        MessagesEvent,
        MessagesEvent.UI,
        MessagesEvent.Internal,
        MessagesState,
        MessagesEffect,
        MessagesCommand
        >(
        MessagesEvent.UI::class,
        MessagesEvent.Internal::class
    ) {
    override fun Result.internal(event: MessagesEvent.Internal): Any {
        return when (event) {
            is MessagesEvent.Internal.ErrorLoading -> {
                state {
                    copy(
                        status = LoadingData.ERROR,
                        isSearchVisible = false,
                        isRefreshVisible = true,
                        isInputVisible = false,
                        isMessageSending = false
                    )
                }
                effects {
                    +MessagesEffect.MessagesLoadError(event.error)
                }
            }
            is MessagesEvent.Internal.ErrorPaginating -> {
                state {
                    copy(
                        status = LoadingData.ERROR,
                        isSearchVisible = true,
                        isInputVisible = true
                    )
                }
                effects {
                    +MessagesEffect.MessagesLoadError(event.error)
                }
            }
            is MessagesEvent.Internal.ErrorSaving -> {
                state {
                    copy(
                        status = LoadingData.ERROR,
                        isSearchVisible = false,
                        isRefreshVisible = true,
                        isInputVisible = false,
                        isMessageSending = false,
                        needToScroll = false
                    )
                }
                effects {
                    +MessagesEffect.MessagesLoadError(event.error)
                }
            }
            is MessagesEvent.Internal.MessagesFiltered -> {
                state {
                    copy(
                        messagesList = event.messages,
                        isInputVisible = !isActionExpanded
                    )
                }
            }
            is MessagesEvent.Internal.MessagesLoaded -> {
                if (event.dataSource == DataSource.DATABASE) {
                    commands {
                        +MessagesCommand.LoadMessages(
                            event.streamHeader,
                            event.topicHeader,
                            DataSource.INTERNET
                        )
                    }
                    state {
                        copy(
                            messagesList = event.messages,
                            status =
                            if (event.messages.isNotEmpty()) {
                                LoadingData.FINISHED
                            } else {
                                LoadingData.LOADING
                            },
                            isSearchVisible = false,
                            isRefreshVisible = false,
                            isInputVisible = false
                        )
                    }
                } else {
                    state {
                        copy(
                            messagesList = event.messages,
                            status = LoadingData.FINISHED,
                            isSearchVisible = true,
                            isRefreshVisible = false,
                            isInputVisible = true,
                            isMessageSending = false
                        )
                    }
                }
            }
            is MessagesEvent.Internal.ErrorSending -> {
                state {
                    copy(
                        isMessageSending = false,
                        needToScroll = false
                    )
                }
                effects {
                    +MessagesEffect.MessagesLoadError(event.error)
                }
            }
            is MessagesEvent.Internal.MessageSended -> {
                state {
                    copy(
                        messagesList = event.messages,
                        status = LoadingData.FINISHED,
                        isSearchVisible = true,
                        isRefreshVisible = false,
                        isInputVisible = true,
                        isMessageSending = false,
                        needToScroll = true
                    )
                }
            }
            is MessagesEvent.Internal.ReactionUpdated -> {
                state {
                    copy(
                        messagesList = event.messages,
                        status = LoadingData.FINISHED
                    )
                }
            }
            is MessagesEvent.Internal.ErrorUpdatingReaction -> {
                state {
                    copy(
                        status = LoadingData.ERROR,
                    )
                }
                effects {
                    +MessagesEffect.MessagesReactionError(event.error)
                }
            }
            is MessagesEvent.Internal.ReactionStateUpdated -> {
                state {
                    copy(
                        messagesList = event.messagesList
                    )
                }
            }
        }
    }

    override fun Result.ui(event: MessagesEvent.UI): Any {
        return when (event) {
            is MessagesEvent.UI.FilterMessages -> {
                state {
                    copy(
                        isInputVisible = false
                    )
                }
                commands {
                    +MessagesCommand.FilterMessages(event.word)
                }
            }
            is MessagesEvent.UI.LoadStreams -> {
                state {
                    copy(
                        status = LoadingData.LOADING,
                        isSearchVisible = false,
                        isRefreshVisible = false,
                        isInputVisible = false
                    )
                }
                commands {
                    +MessagesCommand.LoadMessages(
                        event.streamHeader,
                        event.topicHeader,
                        DataSource.DATABASE
                    )
                }
            }
            is MessagesEvent.UI.Paginate -> {
                state {
                    copy(
                        status = LoadingData.LOADING,
                        isInputVisible = false
                    )
                }
                commands {
                    +MessagesCommand.Paginate(event.streamHeader, event.topicHeader)
                }
            }
            is MessagesEvent.UI.Refresh -> {
                state {
                    copy(
                        status = LoadingData.LOADING,
                        isSearchVisible = false,
                        isRefreshVisible = false,
                        isInputVisible = false
                    )
                }
                commands {
                    +MessagesCommand.LoadMessages(
                        event.streamHeader,
                        event.topicHeader,
                        DataSource.DATABASE
                    )
                }
            }
            is MessagesEvent.UI.SendMessage -> {
                state {
                    copy(
                        isMessageSending = true
                    )
                }
                commands {
                    +MessagesCommand.SendMessage(
                        event.streamHeader,
                        event.topicHeader,
                        event.message
                    )
                }
            }
            is MessagesEvent.UI.SetLastClickedMessageId -> {
                commands {
                    +MessagesCommand.SetLastClickedMessageId(event.id)
                }
            }
            is MessagesEvent.UI.UpdateReactionByBottomSheet -> {
                state {
                    copy(status = LoadingData.LOADING)
                }
                commands {
                    +MessagesCommand.UpdateReactionByBottomSheetFragment(event.reactionIndexValue)
                }
            }
            is MessagesEvent.UI.UpdateReactionByClick -> {
                state {
                    copy(status = LoadingData.LOADING)
                }
                commands {
                    +MessagesCommand.UpdateReactionByClick(
                        event.message,
                        event.reactionPosition
                    )
                }
            }
            is MessagesEvent.UI.ActionExpanded -> {
                state {
                    copy(
                        isActionExpanded = event.expanded,
                        isInputVisible = !event.expanded
                    )
                }
            }
            is MessagesEvent.UI.TextChanged -> {
                state {
                    copy(
                        isTextEmpty = event.text.isBlank() || event.text.isEmpty(),
                        needToScroll = false
                    )
                }
            }
        }
    }
}
