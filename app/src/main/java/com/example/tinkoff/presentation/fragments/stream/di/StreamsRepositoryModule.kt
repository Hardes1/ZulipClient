package com.example.tinkoff.presentation.fragments.stream.di

import com.example.tinkoff.model.network.repositories.StreamsApiRepository
import com.example.tinkoff.model.network.repositoriesImplementation.StreamsApiRepositoryImpl
import com.example.tinkoff.model.repositories.StreamsRepository
import com.example.tinkoff.model.repositoriesImplementation.StreamsRepositoryImpl
import com.example.tinkoff.model.room.repositories.StreamsRoomRepository
import com.example.tinkoff.model.room.repositoriesImplementation.StreamsRoomRepositoryImpl
import com.example.tinkoff.presentation.applications.di.FragmentScope
import dagger.Binds
import dagger.Module

@Module
interface StreamsRepositoryModule {
    @Binds
    @FragmentScope
    fun provideStreamsRepository(streamsRepositoryImpl: StreamsRepositoryImpl): StreamsRepository

    @Binds
    @FragmentScope
    fun provideStreamsApiRepository(streamsApiRepository: StreamsApiRepositoryImpl): StreamsApiRepository

    @Binds
    @FragmentScope
    fun provideStreamsRoomRepository(streamsRoomRepository: StreamsRoomRepositoryImpl): StreamsRoomRepository
}
