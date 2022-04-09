package com.example.tinkoff.ui.fragments.stream

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tinkoff.data.classes.Stream
import com.example.tinkoff.data.classes.StreamsInterface
import com.example.tinkoff.data.states.LoadingData
import com.example.tinkoff.data.states.StreamsType
import com.example.tinkoff.network.Repository
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

class StreamViewModel : ViewModel() {
    private var streamsList: List<Stream> = emptyList()
    private var filteredStreamsList: List<Stream> = emptyList()
    var type: StreamsType? = null
    val displayedStreamsList: MutableLiveData<List<StreamsInterface>> = MutableLiveData()
    val state: MutableLiveData<LoadingData> = MutableLiveData(LoadingData.NONE)
    val isDownloaded: MutableLiveData<Boolean> = MutableLiveData(false)
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var streamSubject: PublishSubject<String>? = null
    private val streamInterfaceSubject: PublishSubject<List<Stream>> =
        PublishSubject.create()

    fun refresh() {
        if (isDownloaded.value == false) {
            compositeDisposable.clear()
            state.value = LoadingData.LOADING
            Repository.generateStreamsData(type ?: StreamsType.SUBSCRIBED)
                .subscribeOn(Schedulers.computation())
                .delay(DELAY_TIME, TimeUnit.MILLISECONDS).observeOn(
                    AndroidSchedulers.mainThread()
                )
                .subscribe(object : SingleObserver<List<Stream>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(streams: List<Stream>) {
                        streamsList = streams
                        streamSubject = streamSubjectBuilder()
                        streamDisplaySubjectBuilder()
                        isDownloaded.value = true
                    }

                    override fun onError(e: Throwable) {
                        Timber.d("Error happened")
                    }
                })
        }
    }


    fun searchStreamsAndTopics(query: String = "") {
        streamSubject?.onNext(query)
    }


    private fun streamDisplaySubjectBuilder() {
        streamInterfaceSubject.apply {
            observeOn(Schedulers.computation())
                .switchMapSingle { Single.just(prepareListForAdapter(it)) }
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                    onNext = { displayedStreamsList.value = it },
                    onError = { Timber.d("Error happened") }
                ).addTo(compositeDisposable)
        }
    }


    private fun streamSubjectBuilder(): PublishSubject<String> {
        return PublishSubject.create<String>().apply {
            observeOn(Schedulers.computation()).map {
                it.trim()
            }.distinctUntilChanged().debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
                .switchMapSingle { queryString ->
                    if (queryString.isEmpty())
                        Single.just(streamsList)
                    else
                        Single.just(streamsList.filter { stream ->
                            stream.streamHeader.name.contains(
                                queryString,
                                ignoreCase = true
                            ) || stream.topics.any { topic ->
                                topic.name.contains(
                                    queryString,
                                    ignoreCase = true
                                )
                            }
                        })
                }.observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                    onNext = {
                        Timber.d("viewModelCalled")
                        filteredStreamsList = it
                        streamInterfaceSubject.onNext(filteredStreamsList)
                    },
                    onError = {
                        Timber.d("Error hapenned $it")
                    },
                ).addTo(compositeDisposable)
        }
    }


    private fun prepareListForAdapter(streams: List<Stream>): List<StreamsInterface> {
        val list: MutableList<StreamsInterface> = mutableListOf()
        Timber.d("list $streams")
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