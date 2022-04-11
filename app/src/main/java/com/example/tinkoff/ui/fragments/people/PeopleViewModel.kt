package com.example.tinkoff.ui.fragments.people

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tinkoff.R
import com.example.tinkoff.data.classes.User
import com.example.tinkoff.data.states.LoadingData
import com.example.tinkoff.network.Repository
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
    private var subject: PublishSubject<String>? = null

    fun refreshPeopleData(context: Context) {
        if (isDownloaded.value == false) {
            compositeDisposable.clear()
            state.value = LoadingData.LOADING
            Repository.tryGenerateUsersData().subscribeOn(Schedulers.computation())
                .delay(DELAY_TIME, TimeUnit.MILLISECONDS).observeOn(mainThread())
                .subscribeBy(
                    onSuccess = { users ->
                        actualUsersList = users
                        subject = initializeDisplaySubject(context)
                        isDownloaded.value = true
                    },
                    onError = {
                        state.value = LoadingData.ERROR
                    }
                ).addTo(compositeDisposable)
        }
    }

    fun searchUsers(query: String = "") {
        subject?.onNext(query)
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

    private fun initializeDisplaySubject(context: Context): PublishSubject<String> {
        return PublishSubject.create<String>().apply {
            observeOn(Schedulers.computation()).map {
                it.trim()
            }.debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS).distinctUntilChanged()
                .switchMapSingle { queryString ->
                    searchPeopleByFilter(queryString)
                }.observeOn(mainThread()).subscribeBy(
                    onNext = {
                        displayedUsersList.value = it
                    },
                    onError = {
                        Timber.d(context.getString(R.string.error_people_loading))
                    },
                ).addTo(compositeDisposable)
        }
    }

    companion object {
        private const val DELAY_TIME: Long = 2500
        private const val DEBOUNCE_TIME: Long = 500
    }
}
