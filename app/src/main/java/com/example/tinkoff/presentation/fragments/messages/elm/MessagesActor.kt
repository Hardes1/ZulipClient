package com.example.tinkoff.presentation.fragments.messages.elm

import com.example.tinkoff.model.repositories.MessagesRepository
import com.example.tinkoff.model.repositoriesImplementation.MessagesRepositoryImpl
import com.example.tinkoff.model.states.DataSource
import com.example.tinkoff.model.storagesImplementation.MessagesStorageImpl
import com.example.tinkoff.presentation.classes.MessageContent
import com.example.tinkoff.presentation.classes.ReactionsData
import io.reactivex.Observable
import io.reactivex.Single
import vivid.money.elmslie.core.Actor
import vivid.money.elmslie.core.switcher.Switcher
import vivid.money.elmslie.core.switcher.observable

class MessagesActor : Actor<MessagesCommand, MessagesEvent> {
    private val filterSwitcher = Switcher()
    private val repository: MessagesRepository by lazy {
        MessagesRepositoryImpl(MessagesStorageImpl())
    }

    override fun execute(command: MessagesCommand): Observable<MessagesEvent> {
        return when (command) {
            is MessagesCommand.FilterMessages -> {
                filterSwitcher.observable(DEBOUNCE_TIME) {
                    repository.filterMessages(command.word).mapEvents(
                        eventMapper = { messagesContent ->
                            MessagesEvent.Internal.MessagesFiltered(messagesContent)
                        },
                        errorMapper = { error ->
                            MessagesEvent.Internal.ErrorLoading(error)
                        }
                    )
                }
            }
            is MessagesCommand.LoadMessages -> {
                repository.getMessagesFromSource(
                    command.streamHeader,
                    command.topicHeader,
                    command.dataSource
                ).mapEvents(
                    eventMapper = { messages ->
                        MessagesEvent.Internal.MessagesLoaded(
                            command.streamHeader,
                            command.topicHeader,
                            messages,
                            command.dataSource
                        )
                    },
                    errorMapper = { error ->
                        MessagesEvent.Internal.ErrorLoading(error)
                    }
                )
            }
            is MessagesCommand.SendMessage -> {
                repository.trySendMessage(
                    command.streamHeader,
                    command.topicHeader,
                    command.message
                ).mapEvents(
                    eventMapper = { messages ->
                        MessagesEvent.Internal.MessageSended(
                            messages
                        )
                    },
                    errorMapper = { error ->
                        MessagesEvent.Internal.ErrorSending(error)
                    }
                )
            }
            is MessagesCommand.SetLastClickedMessageId -> {
                repository.setLastClickedMessageId(command.id).mapEvents(
                    errorMapper = { error ->
                        MessagesEvent.Internal.ErrorSaving(error)
                    }
                )
            }
            is MessagesCommand.UpdateReactionByBottomSheetFragment -> {
                repository.updateReaction(command.reactionIndexValue)
                    .mapEvents(
                        eventMapper = { messagesList ->
                            MessagesEvent.Internal.ReactionUpdated(messagesList)
                        },
                        errorMapper = { error ->
                            MessagesEvent.Internal.ErrorUpdatingReaction(error)
                        }
                    )
            }
            is MessagesCommand.UpdateReactionByClick -> {
                Observable.mergeDelayError(
                    getReactionNameByPosition(
                        command.message,
                        command.reactionPosition
                    )
                        .flatMap { reactionName ->
                            repository.getMessagesListWithReaction(
                                command.message.id,
                                reactionName,
                                isEnabled = false
                            )
                        }
                        .mapEvents(
                            eventMapper = { messagesList ->
                                MessagesEvent.Internal.ReactionStateUpdated(messagesList)
                            },
                            errorMapper = { error ->
                                MessagesEvent.Internal.ErrorUpdatingReaction(error)
                            }
                        ),
                    getReactionIndexByPosition(
                        command.message,
                        command.reactionPosition
                    )
                        .flatMapObservable { reactionIndexValue ->
                            repository.updateReaction(reactionIndexValue).mapEvents(
                                eventMapper = { messages ->
                                    MessagesEvent.Internal.ReactionUpdated(messages)
                                },
                                errorMapper = { error ->
                                    MessagesEvent.Internal.ErrorUpdatingReaction(error)
                                }
                            )
                        },
                    getReactionNameByPosition(
                        command.message,
                        command.reactionPosition
                    )
                        .flatMap { reactionName ->
                            repository.getMessagesListWithReaction(
                                command.message.id,
                                reactionName,
                                isEnabled = true
                            )
                        }
                        .mapEvents(
                            eventMapper = { messagesList ->
                                MessagesEvent.Internal.ReactionStateUpdated(messagesList)
                            },
                            errorMapper = { error ->
                                MessagesEvent.Internal.ErrorUpdatingReaction(error)
                            }
                        )
                )
            }
            is MessagesCommand.Paginate -> {
                repository.getNewMessages(
                    command.streamHeader,
                    command.topicHeader
                ).mapEvents(
                    eventMapper = { messages ->
                        MessagesEvent.Internal.MessagesLoaded(
                            command.streamHeader,
                            command.topicHeader,
                            messages,
                            DataSource.INTERNET
                        )
                    },
                    errorMapper = { error ->
                        MessagesEvent.Internal.ErrorPaginating(error)
                    }
                )
            }
        }
    }

    private fun getReactionIndexByPosition(
        currentMessage: MessageContent,
        reactionPosition: Int
    ): Single<Int> {
        return Single.fromCallable {
            ReactionsData.reactionsStringList.indexOfFirst {
                currentMessage.reactions[reactionPosition].emojiName == it.first
            }
        }
    }

    private fun getReactionNameByPosition(
        currentMessage: MessageContent,
        reactionPosition: Int
    ): Single<String> {
        return Single.fromCallable {
            currentMessage.reactions[reactionPosition].emojiName
        }
    }

    companion object {
        private const val DEBOUNCE_TIME: Long = 300
    }
}
