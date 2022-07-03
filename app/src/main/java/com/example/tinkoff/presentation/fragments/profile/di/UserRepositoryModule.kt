package com.example.tinkoff.presentation.fragments.profile.di

import com.example.tinkoff.model.network.repositories.UserApiRepository
import com.example.tinkoff.model.network.repositoriesImplementation.UserApiRepositoryImpl
import com.example.tinkoff.model.repositories.UserRepository
import com.example.tinkoff.model.repositoriesImplementation.UserRepositoryImpl
import com.example.tinkoff.presentation.applications.di.FragmentScope
import dagger.Binds
import dagger.Module

@Module
interface UserRepositoryModule {
    @Binds
    @FragmentScope
    fun provideUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @FragmentScope
    fun provideUserApiRepository(
        userRepositoryImpl: UserApiRepositoryImpl
    ): UserApiRepository
}
