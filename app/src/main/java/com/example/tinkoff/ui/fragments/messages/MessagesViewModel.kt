package com.example.tinkoff.ui.fragments.messages

import android.icu.text.SimpleDateFormat
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tinkoff.data.api.MessagesListJson
import com.example.tinkoff.data.classes.MessageContent
import com.example.tinkoff.data.classes.MessageContentInterface
import com.example.tinkoff.data.classes.MessageDate
import com.example.tinkoff.data.api.QueryJson
import com.example.tinkoff.data.classes.Reaction
import com.example.tinkoff.data.classes.ReactionFilter
import com.example.tinkoff.data.api.ReactionJson
import com.example.tinkoff.data.classes.ReactionsData
import com.example.tinkoff.data.states.LoadingData
import com.example.tinkoff.data.states.MessageState
import com.example.tinkoff.data.states.SenderType
import com.example.tinkoff.network.client.Repository
import com.example.tinkoff.network.client.Repository.MY_ID
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.sql.Date
import java.util.concurrent.TimeUnit

class MessagesViewModel(
    private val streamHeader: String,
    private val topicHeader: String
) : ViewModel() {
    private var messagesList: List<MessageContentInterface> = listOf()
    private var filteredMessagesList: List<MessageContentInterface> = listOf()
    val displayedMessagesList: MutableLiveData<List<MessageContentInterface>> = MutableLiveData()
    val messageState: MutableLiveData<MessageState> = MutableLiveData(MessageState.SUCCESSFUL)
    val loadingDataState: MutableLiveData<LoadingData> = MutableLiveData()
    val updatingReactionState: MutableLiveData<LoadingData> = MutableLiveData()
    val needToScroll: MutableLiveData<Boolean> = MutableLiveData()
    private val filteredMessageSubject: PublishSubject<String> = PublishSubject.create()
    private val messageDisplaySubject: PublishSubject<List<MessageContentInterface>> =
        PublishSubject.create()
    private var messageId: Int = -1
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun updateReactionByBottomSheet(reactionIndexValue: Int) {
        updatingReactionState.value = LoadingData.LOADING
        val reactionCondition = reactionIndexValue >= 0 &&
            reactionIndexValue < ReactionsData.reactionsStringList.size
        if (reactionCondition && messageId != -1) {
            val indexInMessages =
                messagesList.indexOfFirst { it is MessageContent && it.id == messageId }
            val indexInFilteredMessages =
                filteredMessagesList.indexOfFirst { it is MessageContent && it.id == messageId }
            require(indexInMessages != -1)
            val currentMessage =
                messagesList.find { it is MessageContent && it.id == messageId } as MessageContent
            val currentReactions =
                currentMessage
                    .reactions
            val pressedReactionIndex =
                currentReactions.indexOfFirst { reaction ->
                    reaction.emojiCode == ReactionsData.reactionsStringList[reactionIndexValue].second
                }
            if (pressedReactionIndex == -1 || currentReactions[pressedReactionIndex].usersId.firstOrNull
                { id -> id == MY_ID } == null
            ) {
                addReactionByBottomSheet(
                    reactionIndexValue,
                    pressedReactionIndex,
                    indexInMessages,
                    indexInFilteredMessages,
                    currentMessage
                )
            } else {
                removeReactionByBottomSheet(
                    reactionIndexValue,
                    indexInMessages,
                    indexInFilteredMessages,
                    currentMessage
                )
            }
        }
    }

    private fun removeReactionByClickOnSuccess(
        currentReaction: Reaction,
        currentMessage: MessageContent,
        indexInMessages: Int,
        indexInFilteredMessages: Int,
        reactionPosition: Int
    ) {
        val reactionsWithoutMyId = currentReaction.usersId.filter { it != MY_ID }
        messagesList = messagesList.updated(
            indexInMessages,
            currentMessage.copy(
                reactions = currentMessage.reactions.updated(
                    reactionPosition,
                    currentReaction.copy(usersId = reactionsWithoutMyId)
                )
            )
        )
        prepareListAfterReactionUpdate(
            indexInMessages,
            indexInFilteredMessages,
            currentMessage
        )
        updatingReactionState.value = LoadingData.FINISHED
    }

    private fun addReactionByClickOnSuccess(
        currentReaction: Reaction,
        currentMessage: MessageContent,
        reactionPosition: Int,
        indexInMessages: Int,
        indexInFilteredMessages: Int,
    ) {
        val copiedList = currentReaction.usersId + MY_ID
        messagesList =
            messagesList.updated(
                indexInMessages,
                currentMessage.copy(
                    reactions = currentMessage.reactions.updated(
                        reactionPosition,
                        currentReaction.copy(usersId = copiedList)
                    )
                )
            )
        prepareListAfterReactionUpdate(
            indexInMessages,
            indexInFilteredMessages,
            currentMessage
        )
        updatingReactionState.value = LoadingData.FINISHED
    }

    private fun removeReactionByBottomSheetOnSuccess(
        currentReactions: List<Reaction>,
        reactionIndexValue: Int,
        indexInMessages: Int,
        indexInFilteredMessages: Int,
        currentMessage: MessageContent,
    ) {
        val reactionPosition =
            currentReactions.indexOfFirst {
                it.emojiName ==
                    ReactionsData.reactionsStringList[reactionIndexValue].first
            }
        val currentReaction = currentReactions[reactionPosition]
        val reactionsWithoutMyId = currentReaction.usersId.filter { it != MY_ID }
        messagesList = messagesList.updated(
            indexInMessages,
            currentMessage.copy(
                reactions = currentMessage.reactions.updated(
                    reactionPosition,
                    currentReaction.copy(usersId = reactionsWithoutMyId)
                )
            )
        )
        prepareListAfterReactionUpdate(
            indexInMessages,
            indexInFilteredMessages,
            currentMessage
        )
        updatingReactionState.value = LoadingData.FINISHED
    }

    private fun addReactionByBottomSheetOnSuccess(
        currentMessage: MessageContent,
        currentReactions: List<Reaction>,
        pressedReactionIndex: Int,
        reactionIndexValue: Int,
        indexInMessages: Int,
        indexInFilteredMessages: Int
    ) {
        if (pressedReactionIndex == -1) {
            messagesList =
                messagesList.updated(
                    indexInMessages,
                    currentMessage.copy(
                        reactions = currentReactions + Reaction(
                            ReactionsData.reactionsStringList[reactionIndexValue].first,
                            ReactionsData.reactionsStringList[reactionIndexValue].second,
                            listOf(MY_ID)
                        )
                    )
                )
        } else if (currentReactions[pressedReactionIndex].usersId.firstOrNull
            { id -> id == MY_ID } == null
        ) {
            messagesList = messagesList.updated(
                indexInMessages,
                currentMessage.copy(
                    reactions = currentReactions.updated(
                        pressedReactionIndex,
                        currentReactions[pressedReactionIndex]
                            .copy(
                                usersId =
                                currentReactions[pressedReactionIndex].usersId + MY_ID
                            )
                    )
                )
            )
        }

        prepareListAfterReactionUpdate(
            indexInMessages,
            indexInFilteredMessages,
            currentMessage
        )
        updatingReactionState.value = LoadingData.FINISHED
    }


    private fun removeReactionByBottomSheet(
        reactionIndexValue: Int,
        indexInMessages: Int,
        indexInFilteredMessages: Int,
        currentMessage: MessageContent
    ) {
        val currentReactions = currentMessage.reactions
        Repository.removeReaction(
            messageId,
            ReactionsData.reactionsStringList[reactionIndexValue].first
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    removeReactionByBottomSheetOnSuccess(
                        currentReactions,
                        reactionIndexValue,
                        indexInMessages,
                        indexInFilteredMessages,
                        currentMessage
                    )
                },
                onError = { e ->
                    updatingReactionState.value = LoadingData.ERROR
                    Timber.e(e, "Error during removing reaction")
                }).addTo(compositeDisposable)
    }

    private fun addReactionByBottomSheet(
        reactionIndexValue: Int,
        pressedReactionIndex: Int,
        indexInMessages: Int,
        indexInFilteredMessages: Int,
        currentMessage: MessageContent
    ) {
        val currentReactions = currentMessage.reactions
        Repository.addReaction(
            messageId,
            ReactionsData.reactionsStringList[reactionIndexValue].first
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    addReactionByBottomSheetOnSuccess(
                        currentMessage,
                        currentReactions,
                        pressedReactionIndex,
                        reactionIndexValue,
                        indexInMessages,
                        indexInFilteredMessages
                    )
                },
                onError = { e ->
                    Timber.e(e, "Error during adding reaction")
                    updatingReactionState.value = LoadingData.ERROR
                }).addTo(compositeDisposable)
    }

    fun updateReactionByClick(
        reactionPosition: Int,
        isAdd: Boolean,
        emoji: View
    ) {
        updatingReactionState.value = LoadingData.LOADING
        val indexInMessages =
            messagesList.indexOfFirst { it is MessageContent && it.id == messageId }
        val indexInFilteredMessages =
            filteredMessagesList.indexOfFirst { it is MessageContent && it.id == messageId }
        require(indexInMessages != -1)
        val currentMessage =
            messagesList.find { it is MessageContent && it.id == messageId } as MessageContent
        if (isAdd) {
            addReactionByClick(
                indexInMessages,
                indexInFilteredMessages,
                currentMessage,
                reactionPosition,
                emoji
            )
        } else {
            removeReactionByClick(
                indexInMessages,
                indexInFilteredMessages,
                currentMessage,
                reactionPosition,
                emoji
            )
        }
    }

    private fun removeReactionByClick(
        indexInMessages: Int,
        indexInFilteredMessages: Int,
        currentMessage: MessageContent,
        reactionPosition: Int,
        emoji: View
    ) {
        val currentReactions = currentMessage.reactions
        val currentReaction = currentReactions[reactionPosition]
        Repository.removeReaction(messageId, currentReaction.emojiName).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onSuccess = {
                    removeReactionByClickOnSuccess(
                        currentReaction,
                        currentMessage,
                        indexInMessages,
                        indexInFilteredMessages,
                        reactionPosition
                    )
                }, onError = { e ->
                    Timber.e(e, "Error during removing reaction")
                    emoji.isEnabled = true
                    updatingReactionState.value = LoadingData.ERROR
                }
            ).addTo(compositeDisposable)
    }


    private fun addReactionByClick(
        indexInMessages: Int,
        indexInFilteredMessages: Int,
        currentMessage: MessageContent,
        reactionPosition: Int,
        emoji: View
    ) {
        val currentReaction = currentMessage.reactions[reactionPosition]
        Repository.addReaction(messageId, currentReaction.emojiName).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onSuccess = {
                    addReactionByClickOnSuccess(
                        currentReaction,
                        currentMessage,
                        reactionPosition,
                        indexInMessages,
                        indexInFilteredMessages
                    )
                }, onError = { e ->
                    Timber.e(e, "Error during adding reaction")
                    emoji.isEnabled = true
                    updatingReactionState.value = LoadingData.ERROR
                }
            ).addTo(compositeDisposable)
    }

    private fun prepareListAfterReactionUpdate(
        indexInMessages: Int,
        indexInFilteredMessages: Int,
        currentMessage: MessageContent
    ) {
        val listWithRemovedReaction =
            (messagesList[indexInMessages] as MessageContent).reactions.filter { it.usersId.isNotEmpty() }
        messagesList = messagesList.updated(
            indexInMessages,
            currentMessage.copy(reactions = listWithRemovedReaction)
        )
        if (indexInFilteredMessages != -1) {
            filteredMessagesList = filteredMessagesList.updated(
                indexInFilteredMessages,
                messagesList[indexInMessages]
            )
        }
        needToScroll.value = false
        messageDisplaySubject.onNext(filteredMessagesList)
    }

    private fun parseMessages(messagesListJson: MessagesListJson): List<MessageContentInterface> {
        val resultList = mutableListOf<MessageContentInterface>()
        var counter = 0
        messagesListJson.messages.map { messageJson ->
            val simpleDateFormat = SimpleDateFormat(DATE_PATTERN)
            val netDate = Date(messageJson.timestamp * MILLISECONDS_MULTIPLIER)
            simpleDateFormat.format(netDate)
        }.zip(messagesListJson.messages).groupBy { pair ->
            pair.first
        }.forEach { (key, value) ->
            resultList.add(MessageDate(counter++, key.dropLast(YEAR_SPACE)))
            value.forEach { (_, message) ->
                val reactions = parseReactions(message.reactions)
                resultList.add(
                    MessageContent(
                        message.id,
                        message.content,
                        message.senderFullName,
                        message.avatarUrl,
                        reactions,
                        when (message.senderId) {
                            MY_ID -> {
                                SenderType.OWN
                            }
                            else -> {
                                SenderType.OTHER
                            }
                        }
                    )
                )
            }
        }
        return resultList
    }

    fun refreshMessages() {
        compositeDisposable.clear()
        loadingDataState.value = LoadingData.LOADING
        val streamRequest = QueryJson("stream", streamHeader)
        val topicRequest = QueryJson("topic", topicHeader)
        val preRequest = listOf(streamRequest, topicRequest)
        val json = Json.encodeToString(preRequest)
        Repository.getMessages(
            SORTING_TYPE,
            BEFORE_MESSAGES,
            AFTER_MESSAGES,
            json
        )
            .subscribeOn(
                Schedulers.computation()
            ).map { messagesListJson ->
                parseMessages(messagesListJson)
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { messageContent ->
                    messagesList = messageContent
                    initializeDisplaySubject()
                    initializeSearchSubject()
                    loadingDataState.value = LoadingData.FINISHED
                },
                onError = { e ->
                    Timber.e(e, "Error during refreshing messages")
                    loadingDataState.value = LoadingData.ERROR
                }
            ).addTo(compositeDisposable)
    }

    fun searchMessages(query: String) {
        filteredMessageSubject.onNext(query)
    }

    fun setMessageId(id: Int) {
        messageId = id
    }

    private fun findMessagesByFilter(filter: String): List<MessageContentInterface> {
        return if (filter.isEmpty()) {
            messagesList
        } else {
            val filteredList: MutableList<MessageContentInterface> =
                mutableListOf()
            var needToAddDate = false
            var lastDate = MessageDate(-1, "")
            messagesList.forEach { element ->
                when (element) {
                    is MessageDate -> {
                        lastDate = element
                        needToAddDate = true
                    }
                    is MessageContent -> {
                        if (element.content.contains(
                                filter,
                                ignoreCase = true
                            )
                        ) {
                            if (needToAddDate) {
                                filteredList.add(lastDate)
                                needToAddDate = false
                            }
                            filteredList.add(element)
                        }
                    }
                }
            }
            filteredList
        }
    }

    private fun initializeSearchSubject() {
        filteredMessageSubject.apply {
            observeOn(Schedulers.computation()).map {
                it.trim()
            }.distinctUntilChanged()
                .debounce(
                    DEBOUNCE_TIME, TimeUnit.MILLISECONDS
                )
                .switchMapSingle { filter ->
                    filteredMessagesList = findMessagesByFilter(filter)
                    Single.just(filteredMessagesList)
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onNext = {
                    messageDisplaySubject.onNext(it)
                },
                    onError = { e ->
                        Timber.e(e, "Error during initialize search subject")
                    })
                .addTo(compositeDisposable)
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
            Timber.d("\"${key.emojiName}\",")
            Reaction(key.emojiName, emoji, value.map { reaction -> reaction.user.id })
        }
    }

    private fun initializeDisplaySubject() {
        messageDisplaySubject.apply {
            subscribeBy(
                onNext = {
                    if (messageState.value == MessageState.SENDING) {
                        filteredMessagesList = messagesList
                        messageState.value = MessageState.SUCCESSFUL
                    }
                    displayedMessagesList.value = it
                },
                onError = {
                    Timber.d("display subject $it")
                }
            )
                .addTo(compositeDisposable)
        }
    }

    fun addMessage(message: CharSequence) {
        needToScroll.value = true
        messageState.value = MessageState.SENDING
        val content = message.toString()
        Repository.sendMessage(streamHeader, topicHeader, content).subscribeOn(
            Schedulers.io()
        ).observeOn(AndroidSchedulers.mainThread()).subscribeBy(
            onSuccess = { callback ->
                messagesList = messagesList + MessageContent(
                    callback.id,
                    message.toString(),
                    "",
                    null,
                    mutableListOf(),
                    SenderType.OWN
                )
                messageDisplaySubject.onNext(messagesList)
            },
            onError = {
                Timber.d("error during connection")
                messageState.value = MessageState.FAILED
            }).addTo(compositeDisposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }

    companion object {
        private const val SORTING_TYPE = "newest"
        private const val BEFORE_MESSAGES = 1000
        private const val AFTER_MESSAGES = 0
        private const val BASE = 16
        private const val REACTION_TYPE = "unicode_emoji"
        private const val DATE_PATTERN = "d MMM yyyy"
        private const val YEAR_SPACE = 5
        private const val MILLISECONDS_MULTIPLIER: Long = 1000
        private const val DEBOUNCE_TIME: Long = 400
    }

    private fun <E> Iterable<E>.updated(index: Int, elem: E) =
        mapIndexed { i, existing -> if (i == index) elem else existing }
}
