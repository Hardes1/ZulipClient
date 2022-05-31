package com.example.tinkoff.model.repositoriesImplementation

import com.example.tinkoff.model.repositories.UserRepository
import com.example.tinkoff.presentation.classes.User
import io.reactivex.Single

object UserRepositoryImpl : UserRepository {
    override fun getOwnUser(): Single<User> {
        return DataRepositoriesImpl.api.getOwnUser().flatMap { user ->
            DataRepositoriesImpl.api.getOnlineUserStatus(user.id).map { status ->
                user.copy(status = status.presence.aggregated.status)
            }
        }
    }
}
