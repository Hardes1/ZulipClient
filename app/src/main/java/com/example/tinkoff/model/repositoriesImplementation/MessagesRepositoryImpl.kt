package com.example.tinkoff.model.repositoriesImplementation

import com.example.tinkoff.model.network.api.MessagesListJson
import com.example.tinkoff.model.network.api.QueryJson
import com.example.tinkoff.model.network.api.ReactionJson
import com.example.tinkoff.model.network.repositories.MessagesApiRepository
import com.example.tinkoff.model.network.repositoriesImplementation.RepositoryInformation
import com.example.tinkoff.model.repositories.MessagesRepository
import com.example.tinkoff.model.room.repositories.MessagesRoomRepository
import com.example.tinkoff.model.room.repositoriesImplementation.MessagesRoomRepositoryImpl
import com.example.tinkoff.model.states.DataSource
import com.example.tinkoff.model.states.SenderType
import com.example.tinkoff.model.storages.MessagesStorage
import com.example.tinkoff.presentation.classes.MessageContent
import com.example.tinkoff.presentation.classes.MessageContentInterface
import com.example.tinkoff.presentation.classes.Reaction
import com.example.tinkoff.presentation.classes.ReactionFilter
import com.example.tinkoff.presentation.classes.ReactionsData
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class MessagesRepositoryImpl @Inject constructor() : MessagesRepository {
    @Inject
    lateinit var api: MessagesApiRepository

    @Inject
    lateinit var room: MessagesRoomRepository

    @Inject
    lateinit var storage: MessagesStorage
    override fun setLastClickedMessageId(id: Int): Completable {
        return storage.setLastClickedMessageId(id)
    }

    private fun tryGetMessagesFromDatabase(
        streamHeader: String,
        topicHeader: String
    ): Single<List<MessageContentInterface>> {
        return storage.isCacheEmpty().flatMap { empty ->
            if (empty) {
                getMessagesFromDatabase(streamHeader, topicHeader)
            } else {
                storage.getCurrentMessages()
            }
        }
    }

    private fun getMessagesFromDatabase(
        streamHeader: String,
        topicHeader: String
    ): Single<List<MessageContentInterface>> {
        return room.getMessagesByStreamAndTopic(
            streamHeader,
            topicHeader
        )
            .flatMapCompletable { messagesContent ->
                storage.updateList(messagesContent.reversed())
            }
            .andThen(storage.getCurrentMessages())
    }

    override fun getMessagesListWithReaction(
        messagesId: Int,
        reactionName: String,
        isEnabled: Boolean
    ): Single<List<MessageContentInterface>> {
        return storage.getCurrentMessages().map { messagesListConst ->
            var messagesList = messagesListConst
            val indexInMessages =
                messagesListConst.indexOfFirst { messageContentInterface ->
                    messageContentInterface is MessageContent && messageContentInterface.id == messagesId
                }
            val message = messagesListConst[indexInMessages]
            require(message is MessageContent)
            var reactions = message.reactions
            val reactionPosition =
                reactions.indexOfFirst { reaction -> reaction.emojiName == reactionName }
            if (reactionPosition != -1) {
                reactions = reactions.updated(
                    reactionPosition,
                    reactions[reactionPosition].copy(isEnabled = isEnabled)
                )
                messagesList =
                    messagesList.updated(
                        indexInMessages,
                        message.copy(reactions = reactions)
                    )
            }
            messagesList
        }
    }

    private fun tryGetMessagesFromInternet(
        streamHeader: String,
        topicHeader: String
    ): Single<List<MessageContentInterface>> {
        return getAndTransformMessagesFromInternet(
            streamHeader,
            topicHeader,
            SORTING_TYPE,
            BEFORE_MESSAGES
        )
    }

    override fun filterMessages(filter: String): Single<List<MessageContentInterface>> {
        return storage.setFilter(filter)
            .andThen(storage.getCurrentMessages())
    }

    private fun updateMessagesInDatabase(streamHeader: String, topicHeader: String): Completable {
        return deleteMessagesFromDatabase(streamHeader, topicHeader)
            .andThen(storage.getAllMessagesList())
            .flatMapCompletable { list ->
                insertMessagesInDatabase(
                    streamHeader,
                    topicHeader,
                    list
                )
            }
    }

    private fun insertMessagesInDatabase(
        streamHeader: String,
        topicHeader: String,
        list: List<MessageContentInterface>
    ): Completable {
        return room.insertMessages(
            list.filterIsInstance<MessageContent>(),
            streamHeader,
            topicHeader
        )
    }

    private fun deleteMessagesFromDatabase(
        streamHeader: String,
        topicHeader: String
    ): Completable {
        return room.deleteMessagesByStreamAndTopic(
            streamHeader,
            topicHeader
        )
    }

    private fun getNewMessagesFromInternet(
        streamHeader: String,
        topicHeader: String,
        sortingType: String,
        quantity: Int,
    ): Single<MessagesListJson> {
        val streamRequest = QueryJson(STREAMS_NAME, streamHeader)
        val topicRequest = QueryJson(TOPICS_NAME, topicHeader)
        val preRequest = listOf(streamRequest, topicRequest)
        val json = Json.encodeToString(preRequest)
        return api.getMessages(
            sortingType,
            quantity,
            AFTER_MESSAGES,
            json
        )
    }

    private fun getAndTransformMessagesFromInternet(
        streamHeader: String,
        topicHeader: String,
        sortingType: String,
        quantity: Int
    ): Single<List<MessageContentInterface>> {
        return getNewMessagesFromInternet(
            streamHeader,
            topicHeader,
            sortingType,
            quantity
        )
            .map { messagesListJson ->
                parseMessagesFromListJson(messagesListJson)
            }
            .flatMapCompletable { messagesContent ->
                storage.updateList(messagesContent)
            }
            .andThen(
                updateMessagesInDatabase(streamHeader, topicHeader)
            )
            .andThen(
                storage.getCurrentMessages()
            )
    }

    private fun parseMessagesFromListJson(messagesListJson: MessagesListJson): List<MessageContent> {
        return messagesListJson.messages.map { message ->
            val reactions = parseReactions(message.reactions)
            MessageContent(
                message.id,
                message.content,
                message.senderFullName,
                message.avatarUrl,
                reactions,
                when (message.senderId) {
                    RepositoryInformation.MY_ID -> {
                        SenderType.OWN
                    }
                    else -> {
                        SenderType.OTHER
                    }
                },
                message.timestamp
            )
        }
    }

    private fun parseReactions(list: List<ReactionJson>): List<Reaction> {
        return list.filter { reaction ->
            reaction.reactionType == REACTION_TYPE && ReactionsData.reactionsNameSet.contains(
                reaction.emojiName
            )
        }.groupBy {
            ReactionFilter(it.emojiName, it.emojiCode)
        }.map { (key, value) ->
            val emoji = String(Character.toChars(key.emojiCode.toInt(BASE)))
            Reaction(key.emojiName, emoji, value.map { reaction -> reaction.user.id })
        }
    }

    override fun trySendMessage(
        streamHeader: String,
        topicHeader: String,
        message: String
    ): Single<List<MessageContentInterface>> {
        return api.sendMessage(
            streamHeader,
            topicHeader,
            message
        )
            .flatMapCompletable { callback ->
                addNewMessageToStorage(callback.id, message)
            }
            .andThen(
                updateMessagesInDatabase(streamHeader, topicHeader)
            )
            .andThen(
                storage.getCurrentMessages()
            )
    }

    override fun updateReaction(reactionIndexValue: Int): Single<List<MessageContentInterface>> {
        return storage.getAllMessagesList().flatMap { messagesList ->
            val actualMessageId = storage.messageId
            val indexInMessages =
                messagesList.indexOfFirst { it.id == actualMessageId }
            val currentMessage = messagesList[indexInMessages]
            val currentReactions =
                currentMessage.reactions
            val reactionPosition =
                currentReactions.indexOfFirst { reaction ->
                    reaction.emojiCode == ReactionsData.reactionsStringList[reactionIndexValue].second
                }
            if (reactionPosition == -1 || currentReactions[reactionPosition].usersId.firstOrNull
                { id -> id == RepositoryInformation.MY_ID } == null
            ) {
                addReaction(
                    actualMessageId,
                    reactionIndexValue,
                    indexInMessages
                )
            } else {
                removeReaction(
                    actualMessageId,
                    reactionIndexValue,
                    indexInMessages
                )
            }.flatMap {
                updateMessageReactionInDatabase(actualMessageId)
                    .andThen(
                        Single.fromCallable {
                            it
                        }
                    )
            }
        }
    }

    private fun removeReaction(
        actualMessageId: Int,
        reactionIndexValue: Int,
        indexInMessages: Int,
    ): Single<List<MessageContentInterface>> {
        return api.removeReaction(
            actualMessageId,
            ReactionsData.reactionsStringList[reactionIndexValue].first
        )
            .flatMapCompletable {
                removeReactionToStorage(
                    reactionIndexValue,
                    indexInMessages
                )
            }
            .andThen(
                storage.getCurrentMessages()
            )
    }

    private fun addReaction(
        actualMessageId: Int,
        reactionIndexValue: Int,
        indexInMessages: Int,
    ): Single<List<MessageContentInterface>> {
        return api.addReaction(
            actualMessageId,
            ReactionsData.reactionsStringList[reactionIndexValue].first
        ).flatMapCompletable {
            addReactionToStorage(
                reactionIndexValue,
                indexInMessages
            )
        }.andThen(
            storage.getCurrentMessages()
        )
    }

    private fun updateMessageReactionInDatabase(
        actualMessageId: Int,
    ): Completable {
        return storage.getAllMessagesList().flatMapCompletable { allMessagesList ->
            val reactions = allMessagesList.find { actualMessageId == it.id }?.reactions
            require(reactions != null)
            room.updateMessageReactions(
                actualMessageId,
                reactions
            )
        }
    }

    override fun getNewMessages(
        streamHeader: String,
        topicHeader: String
    ): Single<List<MessageContentInterface>> {
        return storage.getAllMessagesList().map { messagesList ->
            messagesList[0].id.toString()
        }
            .flatMap { oldestMessageId ->
                getNewMessagesFromInternet(
                    streamHeader,
                    topicHeader,
                    oldestMessageId,
                    BEFORE_MESSAGES_UPDATE
                )
            }
            .map { messagesListJson ->
                parseMessagesFromListJson(messagesListJson).dropLast(1)
            }
            .flatMapCompletable { messages ->
                storage.getAllMessagesList().flatMapCompletable { allMessages ->
                    storage.updateList(messages + allMessages)
                }
            }
            .andThen(
                storage.getCurrentMessages()
            )
    }

    private fun addReactionToStorage(
        reactionIndexValue: Int,
        indexInMessages: Int
    ): Completable {
        return storage.getAllMessagesList().flatMapCompletable { messagesListConst ->
            var messagesList = messagesListConst
            val currentMessage = messagesList[indexInMessages]
            val currentReactions = currentMessage.reactions
            val reactionPosition = currentMessage.reactions.indexOfFirst {
                it.emojiName == ReactionsData.reactionsStringList[reactionIndexValue].first
            }
            if (reactionPosition == -1) {
                val actualReactions = currentReactions + Reaction(
                    ReactionsData.reactionsStringList[reactionIndexValue].first,
                    ReactionsData.reactionsStringList[reactionIndexValue].second,
                    listOf(RepositoryInformation.MY_ID)
                )
                messagesList =
                    messagesList.updated(
                        indexInMessages,
                        currentMessage.copy(
                            reactions = actualReactions
                        )
                    )
            } else if (currentReactions[reactionPosition].usersId.firstOrNull
                { id -> id == RepositoryInformation.MY_ID } == null
            ) {
                val actualReactions = currentReactions.updated(
                    reactionPosition,
                    currentReactions[reactionPosition]
                        .copy(
                            usersId =
                            currentReactions[reactionPosition].usersId + RepositoryInformation.MY_ID
                        )
                )
                messagesList = messagesList.updated(
                    indexInMessages,
                    currentMessage.copy(
                        reactions = actualReactions
                    )
                )
            }
            storage.updateList(messagesList)
        }
    }

    private fun removeReactionToStorage(
        reactionIndexValue: Int,
        indexInMessages: Int
    ): Completable {
        return storage.getAllMessagesList().flatMapCompletable { messagesListConst ->
            var messagesList = messagesListConst
            val currentMessage = messagesList[indexInMessages]
            val currentReactions = currentMessage.reactions
            val reactionPosition =
                currentReactions.indexOfFirst {
                    it.emojiName ==
                        ReactionsData.reactionsStringList[reactionIndexValue].first
                }
            val currentReaction = currentReactions[reactionPosition]
            val reactionsWithoutMyId =
                currentReaction.usersId.filter { it != RepositoryInformation.MY_ID }
            val actualReactions = currentMessage.reactions.updated(
                reactionPosition,
                currentReaction.copy(usersId = reactionsWithoutMyId)
            ).filter { it.usersId.isNotEmpty() }
            messagesList = messagesList.updated(
                indexInMessages,
                currentMessage.copy(
                    reactions = actualReactions
                )
            )
            storage.updateList(messagesList)
        }
    }

    private fun addNewMessageToStorage(id: Int, message: String): Completable {
        return storage.getAllMessagesList().flatMapCompletable { messagesListConst ->
            var messagesList = messagesListConst
            val myMessage = MessageContent(
                id,
                message,
                "",
                null,
                mutableListOf(),
                SenderType.OWN,
                System.currentTimeMillis() / MILLISECONDS_MULTIPLIER
            )
            messagesList = messagesList + myMessage
            storage.updateList(messagesList)
        }
    }

    override fun getMessagesFromSource(
        streamHeader: String,
        topicHeader: String,
        dataSource: DataSource
    ): Single<List<MessageContentInterface>> {
        return when (dataSource) {
            DataSource.DATABASE -> {
                tryGetMessagesFromDatabase(
                    streamHeader,
                    topicHeader
                )
            }
            DataSource.INTERNET -> {
                tryGetMessagesFromInternet(
                    streamHeader,
                    topicHeader
                )
            }
        }
    }

    companion object {
        private const val MILLISECONDS_MULTIPLIER: Long = 1000
        private const val STREAMS_NAME = "stream"
        private const val TOPICS_NAME = "topic"
        private const val SORTING_TYPE = "newest"
        private const val BEFORE_MESSAGES = 50
        private const val BEFORE_MESSAGES_UPDATE = 21
        private const val AFTER_MESSAGES = 0
        private const val BASE = 16
        private const val REACTION_TYPE = "unicode_emoji"
    }
}
