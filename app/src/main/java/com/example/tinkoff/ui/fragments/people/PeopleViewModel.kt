package com.example.tinkoff.ui.fragments.people

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tinkoff.data.classes.User
import com.example.tinkoff.network.Repository
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class PeopleViewModel : ViewModel() {
    val usersList: MutableLiveData<List<User>> = MutableLiveData()
    var isFirstRefresh: Boolean = true
        private set
    private var disposable: Disposable? = null
    fun refresh() {
        disposable?.dispose()
        Repository.generateUsersData().subscribeOn(Schedulers.computation())
            .delay(DELAY_TIME, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<List<User>> {
                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onSuccess(users: List<User>) {
                    usersList.value = users
                    isFirstRefresh = false
                }

                override fun onError(e: Throwable) {
                    Timber.d("Error happened")
                }
            })
    }

    companion object {
        private const val DELAY_TIME: Long = 2500
    }

}