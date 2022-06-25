package com.example.tinkoff.presentation.fragments.people.di

import com.example.tinkoff.model.repositories.UsersRepository
import com.example.tinkoff.model.repositoriesImplementation.UsersRepositoryImpl
import com.example.tinkoff.presentation.applications.di.FragmentScope
import dagger.Binds
import dagger.Module

@Module
interface UsersRepositoryModule {
    @Binds
    @FragmentScope
    fun provideUsersRepository(usersRepositoryImpl: UsersRepositoryImpl): UsersRepository
}
