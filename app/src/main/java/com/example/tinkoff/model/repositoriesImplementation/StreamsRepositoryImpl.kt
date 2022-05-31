package com.example.tinkoff.model.repositoriesImplementation

import com.example.tinkoff.model.network.repositories.zipSingles
import com.example.tinkoff.model.repositories.StreamsRepository
import com.example.tinkoff.model.states.DataSource
import com.example.tinkoff.model.states.StreamType
import com.example.tinkoff.model.storages.StreamsStorage
import com.example.tinkoff.presentation.classes.Stream
import com.example.tinkoff.presentation.classes.StreamHeader
import com.example.tinkoff.presentation.classes.StreamsInterface
import com.example.tinkoff.presentation.classes.TopicHeader
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber

class StreamsRepositoryImpl(private val storage: StreamsStorage) : StreamsRepository {
    override fun selectStreamsById(id: Int, isSelected: Boolean): Single<List<StreamsInterface>> {
        return storage.selectStreamById(id, isSelected)
            .andThen(
                storage.getCurrentStreams()
            )
    }

    private fun tryGetStreamsFromInternet(type: StreamType): Single<List<StreamsInterface>> {
        return if (storage.needToLoad) {
            getStreamsFromInternet(type)
        } else {
            storage.getCurrentStreams()
        }
    }

    private fun getStreamsFromInternet(type: StreamType): Single<List<StreamsInterface>> {
        return (
            if (type == StreamType.SUBSCRIBED) {
                DataRepositoriesImpl.api.getSubscribedStreams()
            } else {
                DataRepositoriesImpl.api.getAllStreams()
            })
            .flatMap {
                val allStreamsList = it.streams
                val result = allStreamsList.map { header ->
                    DataRepositoriesImpl.api.getTopicsOfTheStream(header.id)
                }
                    .zipSingles().flatMap { topics ->
                        Single.just(
                            allStreamsList.zip(topics) { stream, topicJson ->
                                val topicsHeaderList =
                                    topicJson.topics.map { topic -> topic.copy(streamId = stream.id) }
                                Stream(
                                    stream,
                                    topicsHeaderList
                                )
                            }
                        )
                    }
                result
            }
            .flatMap { streams ->
                storage.getSelectedStreamsId().map { setOfIds ->
                    streams.map { stream ->
                        if (stream.streamHeader.id in setOfIds) {
                            stream.copy(
                                streamHeader = stream.streamHeader.copy(
                                    isSelected = true
                                )
                            )
                        } else {
                            stream
                        }
                    }
                }
            }
            .flatMapCompletable {
                storage.setStreams(it)
            }
            .andThen(
                storage.setShouldLoadFromInternet(false)
            )
            .andThen(
                updateStreams(type)
            )
            .andThen(
                storage.getCurrentStreams()
            )
    }

    private fun tryGetStreamsFromDatabase(type: StreamType): Single<List<StreamsInterface>> {
        return storage.isCacheEmpty().flatMap { isEmpty ->
            if (isEmpty) {
                getStreamsFromDataBase(type)
            } else {
                storage.getCurrentStreams()
            }
        }

    }

    private fun getStreamsFromDataBase(type: StreamType): Single<List<StreamsInterface>> {
        return getStreamsByType(type).flatMap { streamHeaders ->
            getTopicsByStream(streamHeaders.map { it.id })
                .map { topicHeaders ->
                    val list: MutableList<Stream> = mutableListOf()
                    topicHeaders.groupBy { it.streamId }.forEach { (id, headers) ->
                        val index = streamHeaders.indexOfFirst { it.id == id }
                        list.add(Stream(streamHeaders[index], headers))
                    }
                    list.sortedBy { it.streamHeader.name }.toList()
                }
        }
            .flatMapCompletable { list ->
                storage.setStreams(list)
            }
            .andThen(
                storage.getCurrentStreams()
            )
    }


    override fun getFilteredStreams(filter: String): Single<List<StreamsInterface>> {
        return storage.setFilter(filter)
            .andThen(storage.getCurrentStreams())
    }

    private fun getStreamsByType(currentType: StreamType): Single<List<StreamHeader>> {
        val types = mutableListOf(currentType)
        if (currentType == StreamType.ALL_STREAMS)
            types.add(StreamType.SUBSCRIBED)
        return DataRepositoriesImpl.room.getStreamsByType(
            types
        )
    }

    private fun getTopicsByStream(ids: List<Int>): Single<List<TopicHeader>> {
        return DataRepositoriesImpl.room.getTopicsByStreamId(ids)
    }

    private fun updateTopicsAndStreamsInDatabase(type: StreamType): Completable {
        return if (type == StreamType.SUBSCRIBED) {
            deleteStreams(type)
                .andThen(
                    updateStreams(type)
                )
                .andThen(
                    insertStreams(type)
                )
                .andThen(
                    insertTopics()
                )
        } else {
            deleteStreams(type)
                .andThen(
                    insertStreams(type)
                )
                .andThen(
                    insertTopics()
                )
        }
    }

    private fun updateStreams(streamType: StreamType): Completable {
        return storage.getAllStreamsId().flatMapCompletable { streams ->
            DataRepositoriesImpl.room.updateStreamsByTypeAndIndex(
                streamType,
                streams
            )
        }

    }

    private fun insertStreams(type: StreamType): Completable {
        return storage.getAllStreamsRoom(type).flatMapCompletable { streams ->
            DataRepositoriesImpl.room.insertStreams(streams)
        }
    }

    private fun insertTopics(): Completable {
        return storage.getAllTopicsRoom().flatMapCompletable { topics ->
            DataRepositoriesImpl.room.insertTopics(topics)
        }
    }

    private fun deleteStreams(type: StreamType): Completable {
        return DataRepositoriesImpl.room.deleteStreamsByType(type)
    }

    override fun getStreamsFromSource(
        streamType: StreamType,
        dataSource: DataSource,
        needToRefresh: Boolean
    ): Single<List<StreamsInterface>> {
        return storage.setShouldLoadFromInternet(needToRefresh)
            .andThen(
                when (dataSource) {
                    DataSource.DATABASE -> {
                        tryGetStreamsFromDatabase(streamType)
                    }
                    DataSource.INTERNET -> {
                        tryGetStreamsFromInternet(streamType)
                    }
                }
            )
    }
}
