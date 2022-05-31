package com.example.tinkoff.model.repositories

import com.example.tinkoff.model.storages.UsersStorage
import com.example.tinkoff.presentation.classes.User
import io.reactivex.Single

interface UsersRepository {
    val storage: UsersStorage
    fun init(): Single<List<User>>
    fun getUsersFromCache(): Single<List<User>>
    fun getUsersFromInternet(): Single<List<User>>
    fun filterUsersByString(word: String): Single<List<User>>
}
