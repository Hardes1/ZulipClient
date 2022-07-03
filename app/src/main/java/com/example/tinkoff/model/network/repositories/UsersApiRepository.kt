package com.example.tinkoff.model.network.repositories

import com.example.tinkoff.model.network.api.StatusJson
import com.example.tinkoff.model.network.api.UsersJson
import io.reactivex.Single

interface UsersApiRepository {
    fun getAllUsers(): Single<UsersJson>
    fun getOnlineUserStatus(userId: Int): Single<StatusJson>
}