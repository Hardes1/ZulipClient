package com.example.tinkoff.ui.fragments.people

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tinkoff.data.classes.User
import com.example.tinkoff.data.states.LoadingData
import com.example.tinkoff.network.Repository
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class PeopleViewModel : ViewModel() {
    private var actualUsersList: List<User> = listOf()
    val displayedUsersList: MutableLiveData<List<User>> = MutableLiveData()
    val state: MutableLiveData<LoadingData> = MutableLiveData(LoadingData.NONE)
    val isDownloaded: MutableLiveData<Boolean> = MutableLiveData(false)
    private var disposable: Disposable? = null
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var subject: PublishSubject<String>? = null


    fun refresh() {
        disposable?.dispose()
        if (isDownloaded.value == false) {
            state.value = LoadingData.LOADING
            Repository.generateUsersData().subscribeOn(Schedulers.computation())
                .delay(DELAY_TIME, TimeUnit.MILLISECONDS).observeOn(mainThread())
                .subscribe(object : SingleObserver<List<User>> {
                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onSuccess(users: List<User>) {
                        actualUsersList = users
                        subject = publishSubjectBuilder()
                        isDownloaded.value = true
                    }

                    override fun onError(e: Throwable) {
                        Timber.d("Error happened")
                    }
                })
        }
    }


    fun searchUsers(query: String = "") {
        subject?.onNext(query)
    }


    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    companion object {
        private const val DELAY_TIME: Long = 2500
        private const val DEBOUNCE_TIME: Long = 500
    }


    private fun publishSubjectBuilder(): PublishSubject<String> {
        return PublishSubject.create<String>().apply {
            observeOn(Schedulers.computation()).map {
                it.trim()
            }.debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
                .switchMapSingle { queryString ->
                    Timber.d("DEBUG: in filtering \"$queryString\"")
                    if (queryString.isEmpty())
                        Single.just(actualUsersList)
                    else
                        Single.just(actualUsersList.filter {
                            it.name.contains(
                                queryString,
                                ignoreCase = true
                            )
                        })
                }.observeOn(mainThread()).subscribeBy(
                    onNext = {
                        Timber.d("viewModelCalled")
                        displayedUsersList.value = it

                        state.value = LoadingData.FINISHED
                    },
                    onError = {
                        Timber.d("DEBUG: Error hapenned $it")
                    },
                ).addTo(compositeDisposable)
        }
    }
}