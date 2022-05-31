package com.example.tinkoff.model.storages

import com.example.tinkoff.model.room.entities.StreamHeaderRoom
import com.example.tinkoff.model.room.entities.TopicHeaderRoom
import com.example.tinkoff.model.states.StreamType
import com.example.tinkoff.presentation.classes.Stream
import com.example.tinkoff.presentation.classes.StreamsInterface
import io.reactivex.Completable
import io.reactivex.Single

interface StreamsStorage : UpdateElement {
    var streamsList: List<Stream>
    var needToLoad: Boolean
    var filteredString: String
    fun setStreams(streams: List<Stream>) : Completable
    fun setFilter(filter: String) : Completable
    fun getCurrentStreams(): Single<List<StreamsInterface>>
    fun prepareStreamsForDisplay(streams: List<Stream>): Single<List<StreamsInterface>>
    fun setShouldLoadFromInternet(shouldLoad: Boolean) : Completable
    fun getAllTopicsRoom(): Single<List<TopicHeaderRoom>>
    fun getAllStreamsRoom(type : StreamType): Single<List<StreamHeaderRoom>>
    fun getAllStreamsId(): Single<List<Int>>
    fun getSelectedStreamsId(): Single<Set<Int>>
    fun isCacheEmpty(): Single<Boolean>
    fun selectStreamById(id: Int, isSelected: Boolean) : Completable
}
