package com.example.tinkoff.model.storagesImplementation

import android.icu.text.SimpleDateFormat
import com.example.tinkoff.model.network.repositories.ApiRepository
import com.example.tinkoff.model.states.SenderType
import com.example.tinkoff.model.storages.MessagesStorage
import com.example.tinkoff.presentation.classes.MessageContent
import com.example.tinkoff.presentation.classes.MessageContentInterface
import com.example.tinkoff.presentation.classes.MessageDate
import com.example.tinkoff.presentation.classes.Reaction
import com.example.tinkoff.presentation.classes.ReactionsData
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.internal.operators.completable.CompletableFromAction
import timber.log.Timber
import java.sql.Date

class MessagesStorageImpl : MessagesStorage {
    override var messagesList: List<MessageContent> = emptyList()
    override var filterString: String = ""
    override var messageId: Int = -1

    override fun isCacheEmpty(): Single<Boolean> {
        return Single.fromCallable {
            messagesList.isEmpty()
        }
    }

    override fun prepareMessagesForDisplay(list: List<MessageContent>): Single<List<MessageContentInterface>> {
        return Single.create { emitter ->
            val resultList = mutableListOf<MessageContentInterface>()
            var counter = 0
            list.sortedBy { it.timestamp }.map { message ->
                convertDateToString(message.timestamp)
            }.zip(list).groupBy { pair ->
                pair.first
            }.forEach { (key, value) ->
                resultList.add(MessageDate(counter++, key.dropLast(YEAR_SPACE)))
                value.map {
                    it.second
                }.sortedBy {
                    it.timestamp
                }.forEach { message ->
                    resultList.add(message)
                }
            }
            emitter.onSuccess(resultList)
        }
    }

    override fun setFilter(filter: String): Completable {
        return Completable.fromAction {
            this.filterString = filter
        }
    }

    override fun convertDateToString(timestamp: Long): String {
        val simpleDateFormat = SimpleDateFormat(DATE_PATTERN)
        val netDate = Date(timestamp * MILLISECONDS_MULTIPLIER)
        return simpleDateFormat.format(netDate)
    }

    override fun updateList(list: List<MessageContent>): Completable {
        return Completable.fromAction {
            messagesList = list
        }
    }

    override fun getCurrentMessages(): Single<List<MessageContentInterface>> {
        return Single.fromCallable {
            filterString
        }
            .flatMap {
                if (filterString.isEmpty()) {
                    prepareMessagesForDisplay(messagesList)
                } else {
                    val list: MutableList<MessageContent> = mutableListOf()
                    messagesList.forEach { messageContent ->
                        val containsMessage = messageContent.content.contains(
                            filterString,
                            ignoreCase = true
                        )
                        val containsSender = messageContent.senderName.contains(
                            filterString,
                            ignoreCase = true
                        )
                        if (containsMessage || containsSender) {
                            list.add(messageContent)
                        }
                    }
                    prepareMessagesForDisplay(list)
                }
            }
    }

    override fun getAllMessagesList(): Single<List<MessageContent>> {
        return Single.fromCallable {
            messagesList
        }
    }

    override fun setLastClickedMessageId(id: Int): Completable {
        return Completable.fromAction {
            messageId = id
        }
    }

    companion object {
        private const val MILLISECONDS_MULTIPLIER : Long = 1000
        private const val DATE_PATTERN = "d MMM yyyy"
        private const val YEAR_SPACE = 5
    }
}
