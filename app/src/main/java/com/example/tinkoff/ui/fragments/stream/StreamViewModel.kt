package com.example.tinkoff.ui.fragments.stream

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tinkoff.R
import com.example.tinkoff.data.classes.Stream
import com.example.tinkoff.data.classes.StreamsInterface
import com.example.tinkoff.data.states.LoadingData
import com.example.tinkoff.data.states.StreamsType
import com.example.tinkoff.network.Repository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class StreamViewModel : ViewModel() {
    private var streamsList: List<Stream> = emptyList()
    private var filteredStreamsList: List<Stream> = emptyList()
    var type: StreamsType? = null
    val displayedStreamsList: MutableLiveData<List<StreamsInterface>> = MutableLiveData()
    val state: MutableLiveData<LoadingData> = MutableLiveData()
    val isDownloaded: MutableLiveData<Boolean> = MutableLiveData(false)
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var streamSubject: PublishSubject<String>? = null
    private val streamInterfaceSubject: PublishSubject<List<Stream>> =
        PublishSubject.create()

    fun refresh(context: Context) {
        if (isDownloaded.value == false) {
            compositeDisposable.clear()
            state.value = LoadingData.LOADING
            Repository.tryGenerateStreamsData(type ?: StreamsType.SUBSCRIBED)
                .subscribeOn(Schedulers.computation())
                .delay(DELAY_TIME, TimeUnit.MILLISECONDS).observeOn(
                    AndroidSchedulers.mainThread()
                )
                .subscribeBy(
                    onSuccess = { streams ->
                        streamsList = streams
                        streamSubject = initializeSearchSubject()
                        initializeDisplaySubject(context)
                        isDownloaded.value = true
                    },
                    onError = {
                        state.value = LoadingData.ERROR
                    }
                ).addTo(compositeDisposable)
        }
    }

    fun searchStreamsAndTopics(query: String = "") {
        streamSubject?.onNext(query)
    }

    private fun initializeDisplaySubject(context: Context) {
        streamInterfaceSubject.apply {
            observeOn(Schedulers.computation())
                .switchMapSingle { Single.just(prepareListForAdapter(it)) }
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                    onNext = {
                        displayedStreamsList.value = it
                    },
                    onError = {
                        Timber.d(context.getString(R.string.error_streams_loading))
                    }
                ).addTo(compositeDisposable)
        }
    }

    private fun searchStreamsByFilter(filter: String): Single<List<Stream>> {
        return if (filter.isEmpty())
            Single.just(streamsList)
        else
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

    private fun initializeSearchSubject(): PublishSubject<String> {
        return PublishSubject.create<String>().apply {
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
                    onError = {
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
