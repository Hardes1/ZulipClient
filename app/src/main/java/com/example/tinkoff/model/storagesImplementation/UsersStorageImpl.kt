package com.example.tinkoff.model.storagesImplementation

import com.example.tinkoff.model.storages.UsersStorage
import com.example.tinkoff.presentation.classes.User
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class UsersStorageImpl @Inject constructor() : UsersStorage {
    override var usersList: List<User> = emptyList()

    override fun needToDownload(): Single<Boolean> {
        return Single.just(usersList.isEmpty())
    }

    override fun updateList(newList: List<User>): Completable {
        return Completable.fromAction {
            usersList = newList
        }
    }

    override fun getAllUsers(): Single<List<User>> {
        return Single.just(usersList)
    }

    override fun filterListByName(word: String): Single<List<User>> {
        return Single.just(usersList).map { list ->
            if (word.isBlank() || word.isEmpty()) {
                list
            } else {
                list.filter { user -> user.name.contains(word, ignoreCase = true) }
            }
        }
    }
}
