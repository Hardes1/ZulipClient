package com.example.tinkoff.model.network.repositoriesImplementation

import com.example.tinkoff.model.network.api.StatusJson
import com.example.tinkoff.model.network.repositories.UserApiRepository
import com.example.tinkoff.model.network.services.UsersService
import com.example.tinkoff.presentation.applications.di.FragmentScope
import com.example.tinkoff.presentation.classes.User
import io.reactivex.Single
import javax.inject.Inject
@FragmentScope
class UserApiRepositoryImpl @Inject constructor() : UserApiRepository {
    @Inject
    lateinit var usersService: UsersService

    override fun getOwnUser(): Single<User> {
        return usersService.getOwnUser()
    }

    override fun getOnlineUserStatus(userId: Int): Single<StatusJson> {
        return usersService.getUserOnlineStatus(userId)
    }
}
