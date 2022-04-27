package com.example.tinkoff.ui.fragments.stream

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tinkoff.data.classes.Stream
import com.example.tinkoff.data.classes.StreamsInterface
import com.example.tinkoff.data.states.LoadingData
import com.example.tinkoff.data.states.StreamType
import com.example.tinkoff.network.client.Repository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class StreamViewModel : ViewModel() {
    private var streamsList: List<Stream> = listOf()
    private var filteredStreamsList: List<Stream> = listOf()
    private var type: StreamType? = null
    val displayedStreamsList: MutableLiveData<List<StreamsInterface>> = MutableLiveData()
    val state: MutableLiveData<LoadingData> = MutableLiveData()
    val isDownloaded: MutableLiveData<Boolean> = MutableLiveData(false)
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val streamSubject: PublishSubject<String> = PublishSubject.create()
    private val streamInterfaceSubject: PublishSubject<List<Stream>> =
        PublishSubject.create()

    fun refresh() {
        if (isDownloaded.value == false) {
            compositeDisposable.clear()
            state.value = LoadingData.LOADING
            Repository.getStreams(type ?: StreamType.ALL_STREAMS)
                .subscribeOn(Schedulers.io()).flatMap {
                    val streams = it.streams
                    streams.map { header ->
                        Repository.getTopicsOfTheStream(header.id)
                    }.zipSingles().flatMap { topics ->
                        Single.just(streams.zip(topics) { stream, topicJson ->
                            val addedHeaderId =
                                topicJson.topics.map { topic -> topic.copy(streamId = stream.id) }
                            Stream(
                                stream,
                                addedHeaderId
                            )
                        })
                    }
                }
                .delay(DELAY_TIME, TimeUnit.MILLISECONDS).observeOn(
                    AndroidSchedulers.mainThread()
                )
                .subscribeBy(
                    onSuccess = { streams ->
                        Timber.d("$streams")
                        streamsList = streams
                        initializeSearchSubject()
                        initializeDisplaySubject()
                        isDownloaded.value = true
                    },
                    onError = { e ->
                        Timber.e(e, "Error happened during loading users")
                        state.value = LoadingData.ERROR
                    }
                ).addTo(compositeDisposable)
        }
    }

    fun searchStreamsAndTopics(query: String) {
        streamSubject.onNext(query)
    }

    private fun initializeDisplaySubject() {
        streamInterfaceSubject.apply {
            observeOn(Schedulers.computation())
                .switchMapSingle { Single.just(prepareListForAdapter(it)) }
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                    onNext = {
                        displayedStreamsList.value = it
                    },
                    onError = { e ->
                        Timber.e(e, "Error during display streams")
                    }
                ).addTo(compositeDisposable)
        }
    }

    private fun searchStreamsByFilter(filter: String): Single<List<Stream>> {
        return if (filter.isEmpty()) {
            Single.just(streamsList)
        } else {
            Single.just(streamsList.filter { stream ->
                stream.streamHeader.name.contains(
                    filter,
                    ignoreCase = true
                ) || stream.topics.any { topic ->
                    topic.name.contains(
                        filter,
                        ignoreCase = true
                    )
                }
            })
        }
    }

    private fun initializeSearchSubject() {
        streamSubject.apply {
            observeOn(Schedulers.computation()).map {
                it.trim()
            }.distinctUntilChanged().debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
                .switchMapSingle { queryString ->
                    searchStreamsByFilter(queryString)
                }
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                    onNext = {
                        filteredStreamsList = it
                        streamInterfaceSubject.onNext(filteredStreamsList)
                    },
                    onError = { e->
                        Timber.e(e, "Error during search streams")
                    },
                ).addTo(compositeDisposable)
        }
    }

    private fun prepareListForAdapter(streams: List<Stream>): List<StreamsInterface> {
        val list: MutableList<StreamsInterface> = mutableListOf()
        streams.forEach { stream ->
            list.add(stream.streamHeader.copy())
            if (stream.streamHeader.isSelected) {
                stream.topics.forEach { topicHeader ->
                    list.add(topicHeader.copy())
                }
            }
        }
        return list
    }

    fun trySetStreamType(type: StreamType) {
        if (this.type == null)
            this.type = type
    }

    fun selectItem(id: Int, isSelected: Boolean) {
        streamsList.find { it.streamHeader.id == id }?.streamHeader?.isSelected = isSelected
        streamInterfaceSubject.onNext(filteredStreamsList)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
    }

    companion object {
        private const val DELAY_TIME: Long = 1000
        private const val DEBOUNCE_TIME: Long = 500
    }
}

fun <T> List<Single<T>>.zipSingles(): Single<List<T>> {
    if (this.isEmpty()) return Single.just(emptyList())
    return Single.zip(this) {
        @Suppress("UNCHECKED_CAST")
        return@zip (it as Array<T>).toList()
    }
}
