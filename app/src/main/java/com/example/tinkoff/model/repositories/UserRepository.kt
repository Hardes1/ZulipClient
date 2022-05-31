package com.example.tinkoff.model.repositories

import com.example.tinkoff.presentation.classes.User
import io.reactivex.Single

interface UserRepository {
    fun getOwnUser(): Single<User>
}
