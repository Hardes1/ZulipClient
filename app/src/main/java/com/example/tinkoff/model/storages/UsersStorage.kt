package com.example.tinkoff.model.storages

import com.example.tinkoff.presentation.classes.User
import io.reactivex.Completable
import io.reactivex.Single

interface UsersStorage {
    var usersList: List<User>
    fun needToDownload(): Single<Boolean>
    fun updateList(newList: List<User>): Completable
    fun getAllUsers(): Single<List<User>>
    fun filterListByName(word: String): Single<List<User>>
}
