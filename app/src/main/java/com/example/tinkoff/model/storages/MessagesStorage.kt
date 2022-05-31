package com.example.tinkoff.model.storages

import com.example.tinkoff.presentation.classes.MessageContent
import com.example.tinkoff.presentation.classes.MessageContentInterface
import io.reactivex.Completable
import io.reactivex.Single

interface MessagesStorage : UpdateElement {
    var messagesList: List<MessageContent>
    var filterString: String
    var messageId: Int
    fun isCacheEmpty(): Single<Boolean>
    fun prepareMessagesForDisplay(list: List<MessageContent>): Single<List<MessageContentInterface>>
    fun setFilter(filter: String) : Completable
    fun convertDateToString(timestamp: Long): String
    fun updateList(list: List<MessageContent>) : Completable
    fun getCurrentMessages(): Single<List<MessageContentInterface>>
    fun getAllMessagesList(): Single<List<MessageContent>>
    fun setLastClickedMessageId(id: Int) : Completable
}
