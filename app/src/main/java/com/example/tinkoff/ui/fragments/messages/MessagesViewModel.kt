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
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MessagesViewModel : ViewModel() {
    private var messagesList: MutableList<MessageContentInterface> = mutableListOf()
    val displayedMessagesList: MutableLiveData<List<MessageContentInterface>> = MutableLiveData()
    val state: MutableLiveData<LoadingData> = MutableLiveData(LoadingData.NONE)
    var messageIndex: Int = -1
    private var disposable: Disposable? = null


    fun updateReactions(reactionIndexValue: Int) {
        val messageIndexValue = messageIndex
        val reactionCondition = reactionIndexValue >= 0 &&
                reactionIndexValue < ReactionsData.reactionsStringList.size
        val messageCondition =
            messageIndexValue >= 0 && messageIndexValue < messagesList.size
        if (reactionCondition && messageCondition) {
            val size = messagesList.size
            val currentReactions =
                (messagesList[size - 1 - messageIndexValue] as MessageContent)
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
            updateAdapter()
        }
    }


    fun updateElementCallBack(
        invertedAdapterPosition: Int,
        reactionPosition: Int,
        isAdd: Boolean
    ) {
        val adapterPosition =
            messagesList.size - 1 - invertedAdapterPosition
        val currentReaction =
            (messagesList[adapterPosition] as MessageContent).reactions[reactionPosition]
        if (isAdd) {
            if (currentReaction.usersId.find { it == MY_ID } == null)
                currentReaction.usersId.add(MY_ID)
        } else {
            currentReaction.usersId.removeIf { it == MY_ID }
        }
        if (currentReaction.usersId.size == 0)
            (messagesList[adapterPosition] as MessageContent).reactions.remove(currentReaction)
        updateAdapter()
    }


    fun refreshMessages() {
        disposable?.dispose()
        state.value = LoadingData.LOADING
        Single.create<MutableList<MessageContentInterface>> { emitter ->
            emitter.onSuccess(
                Repository.generateMessagesData()
            )
        }.delay(DELAY_TIME, TimeUnit.MILLISECONDS)
            .subscribeOn(
                Schedulers.computation()
            ).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : SingleObserver<MutableList<MessageContentInterface>> {
                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onSuccess(value: MutableList<MessageContentInterface>) {
                        messagesList = value
                        updateAdapter()
                        state.value = LoadingData.FINISHED
                    }

                    override fun onError(e: Throwable) {
                        Timber.d("Error loading messages")
                    }
                })

    }


    private fun copyList(messagesList: MutableList<MessageContentInterface>): MutableList<MessageContentInterface> {
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


    fun addMessage(message: CharSequence) {
        messagesList.add(
            MessageContent(
                messagesList.size,
                message.toString(),
                mutableListOf(),
                SenderType.OWN
            )
        )
        updateAdapter()
    }


    fun updateAdapter() {
        // TODO: rewrite it to reactive
        displayedMessagesList.value = copyList(messagesList)
    }

    override fun onCleared() {
        disposable?.dispose()
    }

    companion object {
        private const val DELAY_TIME: Long = 1000
    }
}