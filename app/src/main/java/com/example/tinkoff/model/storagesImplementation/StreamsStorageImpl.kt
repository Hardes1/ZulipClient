package com.example.tinkoff.model.storagesImplementation

import com.example.tinkoff.model.room.entities.StreamHeaderRoom
import com.example.tinkoff.model.room.entities.TopicHeaderRoom
import com.example.tinkoff.model.states.StreamType
import com.example.tinkoff.model.storages.StreamsStorage
import com.example.tinkoff.presentation.classes.Stream
import com.example.tinkoff.presentation.classes.StreamsInterface
import io.reactivex.Completable
import io.reactivex.Single

class StreamsStorageImpl : StreamsStorage {
    override var streamsList: List<Stream> = emptyList()
    override var needToLoad: Boolean = true
    override var filteredString: String = ""
    override fun setStreams(streams: List<Stream>): Completable {
        return Completable.fromAction {
            streamsList = streams
        }
    }

    override fun setFilter(filter: String): Completable {
        return Completable.fromAction {
            filteredString = filter
        }
    }

    override fun getCurrentStreams(): Single<List<StreamsInterface>> {
        return Single.fromCallable {
            if (filteredString.isEmpty()) {
                streamsList
            } else {
                streamsList.filter { stream ->
                    stream.streamHeader.name.contains(
                        filteredString,
                        ignoreCase = true
                    ) || stream.topics.any { topic ->
                        topic.name.contains(
                            filteredString,
                            ignoreCase = true
                        )
                    }
                }
            }
        }.flatMap { streams ->
            prepareStreamsForDisplay(streams)
        }
    }

    override fun prepareStreamsForDisplay(streams: List<Stream>): Single<List<StreamsInterface>> {
        return Single.fromCallable {
            val list: MutableList<StreamsInterface> = mutableListOf()
            streams.forEach { stream ->
                list.add(stream.streamHeader.copy())
                if (stream.streamHeader.isSelected) {
                    stream.topics.forEach { topicHeader ->
                        list.add(topicHeader.copy())
                    }
                }
            }
            list
        }
    }

    override fun setShouldLoadFromInternet(shouldLoad: Boolean): Completable {
        return Completable.fromAction {
            if (shouldLoad) {
                needToLoad = shouldLoad
            }
        }
    }

    override fun getAllTopicsRoom(): Single<List<TopicHeaderRoom>> {
        return Single.fromCallable {
            streamsList.flatMap { it.topics }.map { it.toTopicHeaderRoom() }
        }
    }

    override fun getAllStreamsRoom(type: StreamType): Single<List<StreamHeaderRoom>> {
        return Single.fromCallable {
            streamsList.map { it.streamHeader.toStreamHeaderRoom(type) }
        }
    }

    override fun getAllStreamsId(): Single<List<Int>> {
        return Single.fromCallable {
            streamsList.map { it.streamHeader.id }
        }
    }

    override fun getSelectedStreamsId(): Single<Set<Int>> {
        return Single.fromCallable {
            streamsList.filter { it.streamHeader.isSelected }.map { it.streamHeader.id }.toSet()
        }
    }

    override fun isCacheEmpty(): Single<Boolean> {
        return Single.fromCallable {
            streamsList.isEmpty()
        }
    }


    override fun selectStreamById(id: Int, isSelected: Boolean): Completable {
        return Completable.fromAction {
            val index = streamsList.indexOfFirst { it.streamHeader.id == id }
            require(index != -1)
            val header = streamsList[index].streamHeader.copy(isSelected = isSelected)
            streamsList = streamsList.updated(index, streamsList[index].copy(streamHeader = header))
        }
    }
}
