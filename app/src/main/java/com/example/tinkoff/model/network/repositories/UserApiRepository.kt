package com.example.tinkoff.model.network.repositories

import com.example.tinkoff.model.network.api.StatusJson
import com.example.tinkoff.model.network.services.UsersService
import com.example.tinkoff.presentation.applications.di.FragmentScope
import com.example.tinkoff.presentation.classes.User
import io.reactivex.Single
import javax.inject.Inject
@FragmentScope
class UserApiRepository @Inject constructor() {
    @Inject
    lateinit var usersService: UsersService

    fun getOwnUser(): Single<User> {
        return usersService.getOwnUser()
    }

    fun getOnlineUserStatus(userId: Int): Single<StatusJson> {
        return usersService.getUserOnlineStatus(userId)
    }
}
