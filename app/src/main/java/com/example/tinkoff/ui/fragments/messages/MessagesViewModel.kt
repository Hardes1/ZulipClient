package com.example.tinkoff.ui.fragments.messages

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tinkoff.data.classes.*
import com.example.tinkoff.data.states.LoadingData
import com.example.tinkoff.data.states.SenderType
import com.example.tinkoff.network.Repository
import com.example.tinkoff.ui.fragments.messages.MessageFragment.Companion.MY_ID
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MessagesViewModel : ViewModel() {
    private var messagesList: MutableList<MessageContentInterface> = mutableListOf()
    private var filteredMessagesList: MutableList<MessageContentInterface> = mutableListOf()
    val displayedMessagesList: MutableLiveData<List<MessageContentInterface>> = MutableLiveData()
    val state: MutableLiveData<LoadingData> = MutableLiveData(LoadingData.NONE)
    var needToScroll: Boolean = false
    private var filteredMessageSubject: PublishSubject<String>? = null
    private val messagePrepareSubject: PublishSubject<List<MessageContentInterface>> =
        PublishSubject.create()
    private var messageId: Int = -1
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun updateReactions(reactionIndexValue: Int) {
        val reactionCondition = reactionIndexValue >= 0 &&
                reactionIndexValue < ReactionsData.reactionsStringList.size
        if (reactionCondition && messageId != -1) {
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
                currentReactions.add(
                    Reaction(
                        ReactionsData.reactionsStringList[reactionIndexValue],
                        mutableListOf(MY_ID)
                    )
                )

            } else if (currentReactions[pressedReactionIndex].usersId.firstOrNull
                { id -> id == MY_ID } == null
            ) {
                currentReactions[pressedReactionIndex].usersId.add(MY_ID)
            }
            messagePrepareSubject.onNext(filteredMessagesList)
        }
    }


    fun updateElementCallBack(
        elementId: Int,
        reactionPosition: Int,
        isAdd: Boolean
    ) {

        val currentMessage =
            messagesList.find { it is MessageContent && it.id == elementId } as MessageContent
        val currentReaction =
            currentMessage.reactions[reactionPosition]
        if (isAdd) {
            if (currentReaction.usersId.find { it == MY_ID } == null)
                currentReaction.usersId.add(MY_ID)
        } else {
            currentReaction.usersId.removeIf { it == MY_ID }
        }
        if (currentReaction.usersId.size == 0)
            currentMessage.reactions.remove(currentReaction)
        messagePrepareSubject.onNext(filteredMessagesList)
    }


    fun refreshMessages() {
        compositeDisposable.clear()
        state.value = LoadingData.LOADING
        Repository.generateMessagesData().delay(DELAY_TIME, TimeUnit.MILLISECONDS)
            .subscribeOn(
                Schedulers.computation()
            ).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : SingleObserver<MutableList<MessageContentInterface>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(value: MutableList<MessageContentInterface>) {
                        messagesList = value
                        initializePrepareSubject()
                        initializeFilteredSubject()
                        searchMessages("")
                        state.value = LoadingData.FINISHED
                    }

                    override fun onError(e: Throwable) {
                        Timber.d("Error loading messages")
                    }
                })

    }


    fun searchMessages(query: String) {
        filteredMessageSubject?.onNext(query)
    }


    private fun copyList(messagesList: List<MessageContentInterface>): MutableList<MessageContentInterface> {
        val result: MutableList<MessageContentInterface> = mutableListOf()
        for (element in messagesList) {
            if (element is Date)
                result.add(Date(element.id, element.date))
            else if (element is MessageContent) {
                val reactionsContent = mutableListOf<Reaction>()
                for (reaction in element.reactions) {
                    val usersId = mutableListOf<Int>()
                    usersId.addAll(reaction.usersId)
                    reactionsContent.add(Reaction(reaction.emoji, usersId))
                }
                result.add(
                    MessageContent(
                        element.id,
                        element.content,
                        reactionsContent,
                        element.type
                    )
                )
            }
        }
        return result
    }


    fun setMessageId(id: Int) {
        messageId = id
    }


    private fun initializeFilteredSubject() {
        filteredMessageSubject =
            PublishSubject.create<String>().apply {
                observeOn(Schedulers.computation()).map {
                    Timber.d("DEBUG: entered to filtered list")
                    it.trim()
                }.distinctUntilChanged()
                    .debounce(
                        DEBOUNCE_TIME, TimeUnit.MILLISECONDS
                    )
                    .switchMapSingle { filter ->
                        Timber.d("DEBUG: filter string is \"$filter\"")
                        filteredMessagesList =
                            if (filter.isEmpty()) {
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
                        Single.just(filteredMessagesList)
                    }.observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(onNext = { messagePrepareSubject.onNext(it) })
                    .addTo(compositeDisposable)
            }
    }

    private fun initializePrepareSubject() {
        messagePrepareSubject.observeOn(Schedulers.computation())
            .switchMapSingle {
                Single.just(copyList(it))
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { displayedMessagesList.value = it },
                onError = { Timber.d("error happened") }).addTo(compositeDisposable)
    }

    fun addMessage(message: CharSequence) {
        needToScroll = true
        messagesList.add(
            MessageContent(
                messagesList.size,
                message.toString(),
                mutableListOf(),
                SenderType.OWN
            )
        )
        messagePrepareSubject.onNext(messagesList)
    }


    override fun onCleared() {
        compositeDisposable.clear()
    }

    companion object {
        private const val DELAY_TIME: Long = 1000
        private const val DEBOUNCE_TIME: Long = 400
    }
}