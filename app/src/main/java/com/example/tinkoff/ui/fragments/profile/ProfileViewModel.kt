package com.example.tinkoff.ui.fragments.profile

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tinkoff.R
import com.example.tinkoff.data.classes.User
import com.example.tinkoff.data.states.LoadingData
import com.example.tinkoff.network.client.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ProfileViewModel : ViewModel() {
    val ownUser: MutableLiveData<User> = MutableLiveData()
    val state: MutableLiveData<LoadingData> = MutableLiveData()
    private var disposable: Disposable? = null

    fun refreshProfile(context: Context) {
        disposable?.dispose()
        if (state.value != LoadingData.FINISHED) {
            state.value = LoadingData.LOADING
            disposable = Repository.getOwnUser()
                .subscribeOn(Schedulers.io()).flatMap { user ->
                    Repository.getOnlineUserStatus(user.id).map {
                        User(
                            user.id,
                            user.name,
                            user.email,
                            it.presence.aggregated.status,
                            user.avatarUrl,
                            user.isBot
                        )
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = { value ->
                        ownUser.value = value
                        state.value = LoadingData.FINISHED
                    },
                    onError = { e ->
                        Timber.e(e, "Error during gettin info about own user")
                        state.value = LoadingData.ERROR
                        Timber.d(context.getString(R.string.error_profile_loading))
                    }
                )
        }
    }

    override fun onCleared() {
        disposable?.dispose()
    }
}
