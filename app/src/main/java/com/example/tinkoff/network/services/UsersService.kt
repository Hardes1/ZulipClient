package com.example.tinkoff.network.services

import com.example.tinkoff.data.api.StatusJson
import com.example.tinkoff.data.api.UsersJson
import com.example.tinkoff.data.classes.User
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface UsersService {
    @GET("users/me")
    fun getOwnUser(): Single<User>

    @GET("users/{userId}/presence")
    fun getUserOnlineStatus(@Path("userId") userId: Int): Single<StatusJson>

    @GET("users")
    fun getAllUsers(): Single<UsersJson>
}