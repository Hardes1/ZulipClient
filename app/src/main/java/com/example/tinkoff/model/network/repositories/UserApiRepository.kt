package com.example.tinkoff.model.network.repositories

import com.example.tinkoff.model.network.api.StatusJson
import com.example.tinkoff.presentation.classes.User
import io.reactivex.Single

interface UserApiRepository {
    fun getOwnUser(): Single<User>
    fun getOnlineUserStatus(userId: Int): Single<StatusJson>
}