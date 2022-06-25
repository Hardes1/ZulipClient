package com.example.tinkoff.presentation.fragments.stream.di

import com.example.tinkoff.model.repositories.StreamsRepository
import com.example.tinkoff.model.storages.StreamsStorage
import com.example.tinkoff.model.storagesImplementation.StreamsStorageImpl
import com.example.tinkoff.presentation.applications.di.FragmentScope
import com.example.tinkoff.presentation.fragments.stream.elm.StreamsActor
import com.example.tinkoff.presentation.fragments.stream.elm.StreamsReducer
import com.example.tinkoff.presentation.fragments.stream.elm.StreamsState
import dagger.Binds
import dagger.Module
import dagger.Provides
import vivid.money.elmslie.core.switcher.Switcher

@Module(includes = [StreamsElmModule.BindsStreamsElmModule::class])
class StreamsElmModule {
    @Module
    interface BindsStreamsElmModule {
        @Binds
        @FragmentScope
        fun provideStreamsStorage(streamsStorageImpl: StreamsStorageImpl): StreamsStorage
    }

    @Provides
    @FragmentScope
    fun provideStreamsReducer(): StreamsReducer {
        return StreamsReducer()
    }

    @Provides
    @FragmentScope
    fun provideStreamsState(): StreamsState {
        return StreamsState()
    }

    @Provides
    @FragmentScope
    fun provideStreamsActor(
        filterSwitcher: Switcher,
        repository: StreamsRepository
    ): StreamsActor {
        return StreamsActor(filterSwitcher, repository)
    }
}
