package com.example.tinkoff.model.network.repositories

import com.example.tinkoff.model.network.api.StatusJson
import com.example.tinkoff.model.network.api.UsersJson
import com.example.tinkoff.model.network.services.UsersService
import com.example.tinkoff.presentation.applications.di.FragmentScope
import io.reactivex.Single
import javax.inject.Inject

@FragmentScope
class UsersApiRepository @Inject constructor() {
    @Inject
    lateinit var usersService: UsersService

    fun getAllUsers(): Single<UsersJson> {
        return usersService.getAllUsers()
    }

    fun getOnlineUserStatus(userId: Int): Single<StatusJson> {
        return usersService.getUserOnlineStatus(userId)
    }
}
