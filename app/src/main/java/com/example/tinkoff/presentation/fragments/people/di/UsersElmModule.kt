package com.example.tinkoff.presentation.fragments.people.di

import com.example.tinkoff.model.repositories.UsersRepository
import com.example.tinkoff.model.storages.UsersStorage
import com.example.tinkoff.model.storagesImplementation.UsersStorageImpl
import com.example.tinkoff.presentation.applications.di.FragmentScope
import com.example.tinkoff.presentation.fragments.people.elm.UsersActor
import com.example.tinkoff.presentation.fragments.people.elm.UsersReducer
import com.example.tinkoff.presentation.fragments.people.elm.UsersState
import dagger.Binds
import dagger.Module
import dagger.Provides
import vivid.money.elmslie.core.switcher.Switcher

@Module(includes = [UsersElmModule.BindsUsersElmModule::class])
class UsersElmModule {

    @Module
    interface BindsUsersElmModule {
        @Binds
        @FragmentScope
        fun provideUsersStorage(usersStorage: UsersStorageImpl): UsersStorage
    }

    @Provides
    @FragmentScope
    fun provideUsersReducer(): UsersReducer {
        return UsersReducer()
    }

    @Provides
    @FragmentScope
    fun provideUsersState(): UsersState {
        return UsersState()
    }

    @Provides
    @FragmentScope
    fun providesUsersActor(
        usersRepository: UsersRepository,
        filterSwitcher: Switcher,
        dataSwitcher: Switcher
    ): UsersActor {
        return UsersActor(usersRepository, filterSwitcher, dataSwitcher)
    }
}
