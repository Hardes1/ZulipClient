package com.example.tinkoff.model.network.repositoriesImplementation

import com.example.tinkoff.model.network.api.StatusJson
import com.example.tinkoff.model.network.api.UsersJson
import com.example.tinkoff.model.network.repositories.UsersApiRepository
import com.example.tinkoff.model.network.services.UsersService
import com.example.tinkoff.presentation.applications.di.FragmentScope
import io.reactivex.Single
import javax.inject.Inject

@FragmentScope
class UsersApiRepositoryImpl @Inject constructor() : UsersApiRepository {
    @Inject
    lateinit var usersService: UsersService

    override fun getAllUsers(): Single<UsersJson> {
        return usersService.getAllUsers()
    }

    override fun getOnlineUserStatus(userId: Int): Single<StatusJson> {
        return usersService.getUserOnlineStatus(userId)
    }
}
