package com.example.tinkoff.ui.fragments.people

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tinkoff.data.classes.User
import com.example.tinkoff.data.states.LoadingData
import com.example.tinkoff.network.client.Client
import com.example.tinkoff.network.client.Repository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class PeopleViewModel : ViewModel() {
    private var actualUsersList: List<User> = listOf()
    val displayedUsersList: MutableLiveData<List<User>> = MutableLiveData()
    val state: MutableLiveData<LoadingData> = MutableLiveData()
    val isDownloaded: MutableLiveData<Boolean> = MutableLiveData(false)
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val subject: PublishSubject<String> = PublishSubject.create()

    fun refreshPeopleData() {
        if (isDownloaded.value == false) {
            compositeDisposable.clear()
            state.value = LoadingData.LOADING
            Repository.getAllUsers().subscribeOn(Schedulers.io())
                .flatMap { data ->
                    data.users.filter { !it.isBot && it.id != Repository.MY_ID }.map { user ->
                        Client.usersService.getUserOnlineStatus(user.id).map {
                            User(
                                user.id,
                                user.name,
                                user.email,
                                it.presence.aggregated.status,
                                user.avatarUrl,
                                user.isBot
                            )
                        }
                    }.zipSingles()
                }
                .observeOn(mainThread())
                .subscribeBy(
                    onSuccess = { users ->
                        actualUsersList = users
                        initializeDisplaySubject()
                        isDownloaded.value = true
                    },
                    onError = { e ->
                        Timber.e(e, "Error during refreshing people")
                        state.value = LoadingData.ERROR
                    }
                ).addTo(compositeDisposable)
        }
    }

    fun searchUsers(query: String) {
        subject.onNext(query)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    private fun searchPeopleByFilter(filter: String): Single<List<User>> {
        return if (filter.isEmpty())
            Single.just(actualUsersList)
        else
            Single.just(actualUsersList.filter {
                it.name.contains(
                    filter,
                    ignoreCase = true
                )
            })
    }

    private fun initializeDisplaySubject() {
        subject.apply {
            observeOn(Schedulers.computation()).map {
                it.trim()
            }.debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS).distinctUntilChanged()
                .switchMapSingle { queryString ->
                    searchPeopleByFilter(queryString)
                }.observeOn(mainThread()).subscribeBy(
                    onNext = {
                        displayedUsersList.value = it
                    },
                    onError = { e ->
                        Timber.e(e, "Error during initialize display subject")
                    },
                ).addTo(compositeDisposable)
        }
    }

    companion object {
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
