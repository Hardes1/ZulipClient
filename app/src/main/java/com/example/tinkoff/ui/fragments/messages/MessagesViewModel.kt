package com.example.tinkoff.ui.fragments.messages

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tinkoff.R
import com.example.tinkoff.data.classes.Date
import com.example.tinkoff.data.classes.MessageContent
import com.example.tinkoff.data.classes.MessageContentInterface
import com.example.tinkoff.data.classes.Reaction
import com.example.tinkoff.data.classes.ReactionsData
import com.example.tinkoff.data.states.LoadingData
import com.example.tinkoff.data.states.MessageState
import com.example.tinkoff.data.states.SenderType
import com.example.tinkoff.network.Repository
import com.example.tinkoff.ui.fragments.messages.MessageFragment.Companion.MY_ID
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MessagesViewModel : ViewModel() {
    private var messagesList: List<MessageContentInterface> = listOf()
    private var filteredMessagesList: List<MessageContentInterface> = listOf()
    val displayedMessagesList: MutableLiveData<List<MessageContentInterface>> = MutableLiveData()
    val messageState: MutableLiveData<MessageState> = MutableLiveData(MessageState.SUCCESSFUL)
    val loadingDataState: MutableLiveData<LoadingData> = MutableLiveData()
    val needToScroll: MutableLiveData<Boolean> = MutableLiveData()
    private val filteredMessageSubject: PublishSubject<String> = PublishSubject.create()
    private val messageDisplaySubject: PublishSubject<List<MessageContentInterface>> =
        PublishSubject.create()
    private var messageId: Int = -1
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun tryAddReaction(reactionIndexValue: Int) {
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
                    reaction.emoji == ReactionsData.reactionsStringList[reactionIndexValue]
                }
            if (pressedReactionIndex == -1) {
                messagesList =
                    messagesList.updated(
                        indexInMessages,
                        currentMessage.copy(
                            reactions = currentReactions + Reaction(
                                ReactionsData.reactionsStringList[reactionIndexValue],
                                mutableListOf(MY_ID)
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

            if (indexInFilteredMessages != -1) {
                filteredMessagesList = filteredMessagesList.updated(
                    indexInFilteredMessages,
                    messagesList[indexInMessages]
                )
            }
            needToScroll.value = false
            messageDisplaySubject.onNext(filteredMessagesList)
        }
    }

    fun reactionClickedCallBack(
        reactionPosition: Int,
        isAdd: Boolean
    ) {
        val indexInMessages =
            messagesList.indexOfFirst { it is MessageContent && it.id == messageId }
        val indexInFilteredMessages =
            filteredMessagesList.indexOfFirst { it is MessageContent && it.id == messageId }
        require(indexInMessages != -1)
        val currentMessage =
            messagesList.find { it is MessageContent && it.id == messageId } as MessageContent
        val currentReaction =
            currentMessage.reactions[reactionPosition]
        if (isAdd) {
            if (currentReaction.usersId.find { it == MY_ID } == null) {
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
            }
        } else {
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
        }
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

    fun refreshMessages(context: Context) {
        compositeDisposable.clear()
        loadingDataState.value = LoadingData.LOADING
        Repository.tryGenerateMessagesData().delay(DELAY_TIME, TimeUnit.MILLISECONDS)
            .subscribeOn(
                Schedulers.computation()
            ).observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    messagesList = it
                    initializeDisplaySubject()
                    initializeSearchSubject(context)
                    loadingDataState.value = LoadingData.FINISHED
                },
                onError = {
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
            var lastDate = Date(-1, "")
            messagesList.forEach { element ->
                when (element) {
                    is Date -> {
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

    private fun initializeSearchSubject(context: Context) {
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
                    onError = {
                        Timber.d(context.getString(R.string.error_messages_loading))
                    })
                .addTo(compositeDisposable)
        }
    }

    fun initializeDisplaySubject() {
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
                    messagesList = messagesList.subList(0, messagesList.size - 1)
                    messageState.value = MessageState.FAILED
                }
            )
                .addTo(compositeDisposable)
        }
    }

    fun addMessage(message: CharSequence) {
        needToScroll.value = true
        messageState.value = MessageState.SENDING
        messagesList = messagesList + MessageContent(
            messagesList.size,
            message.toString(),
            mutableListOf(),
            SenderType.OWN
        )
        if (true)
            messageDisplaySubject.onNext(messagesList)
        else
            messageDisplaySubject.onError(Throwable(Repository.ERROR))
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }

    companion object {
        private const val DELAY_TIME: Long = 1000
        private const val DEBOUNCE_TIME: Long = 400
    }

    private fun <E> Iterable<E>.updated(index: Int, elem: E) =
        mapIndexed { i, existing -> if (i == index) elem else existing }
}
