package com.example.tinkoff.ui.fragments.profile

import android.content.Context
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tinkoff.R
import com.example.tinkoff.data.classes.User
import com.example.tinkoff.data.states.LoadingData
import com.example.tinkoff.network.Repository
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ProfileViewModel : ViewModel() {
    val ownUser: MutableLiveData<User> = MutableLiveData()
    val state: MutableLiveData<LoadingData> = MutableLiveData(LoadingData.NONE)
    private var disposable: Disposable? = null

    fun refreshProfile(context: Context) {
        disposable?.dispose()
        if (state.value != LoadingData.FINISHED) {
            state.value = LoadingData.LOADING
            Repository.generatePersonalUserData(context)
                .delay(ProfileFragment.DELAY_TIME, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<User> {
                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onSuccess(value: User) {
                        ownUser.value = value
                        state.value = LoadingData.FINISHED
                    }

                    override fun onError(e: Throwable) {
                        Timber.d(context.getString(R.string.error_profile_loading))
                    }
                })
        }
    }

    override fun onCleared() {
        disposable?.dispose()
    }
}