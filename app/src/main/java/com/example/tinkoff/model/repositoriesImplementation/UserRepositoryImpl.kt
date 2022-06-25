package com.example.tinkoff.model.repositoriesImplementation

import com.example.tinkoff.model.network.repositories.UserApiRepository
import com.example.tinkoff.model.repositories.UserRepository
import com.example.tinkoff.presentation.classes.User
import io.reactivex.Single
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor() : UserRepository {
    @Inject
    lateinit var api: UserApiRepository
    override fun getOwnUser(): Single<User> {
        return api.getOwnUser().flatMap { user ->
            api.getOnlineUserStatus(user.id).map { status ->
                user.copy(status = status.presence.aggregated.status)
            }
        }
    }
}
