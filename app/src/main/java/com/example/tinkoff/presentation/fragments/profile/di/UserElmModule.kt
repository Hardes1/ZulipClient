package com.example.tinkoff.presentation.fragments.profile.di

import com.example.tinkoff.model.repositories.UserRepository
import com.example.tinkoff.presentation.applications.di.FragmentScope
import com.example.tinkoff.presentation.fragments.profile.elm.UserActor
import com.example.tinkoff.presentation.fragments.profile.elm.UserReducer
import com.example.tinkoff.presentation.fragments.profile.elm.UserState
import dagger.Module
import dagger.Provides

@Module
class UserElmModule {
    @Provides
    @FragmentScope
    fun provideUserReducer(): UserReducer {
        return UserReducer()
    }

    @Provides
    @FragmentScope
    fun provideUserState(): UserState {
        return UserState()
    }

    @Provides
    @FragmentScope
    fun provideUserActor(
        userRepository: UserRepository
    ): UserActor {
        return UserActor(userRepository)
    }
}
