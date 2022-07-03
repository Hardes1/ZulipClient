package com.example.tinkoff.presentation.fragments.messages.di

import com.example.tinkoff.model.network.repositories.MessagesApiRepository
import com.example.tinkoff.model.network.repositoriesImplementation.MessagesApiRepositoryImpl
import com.example.tinkoff.model.repositories.MessagesRepository
import com.example.tinkoff.model.repositoriesImplementation.MessagesRepositoryImpl
import com.example.tinkoff.model.room.repositories.MessagesRoomRepository
import com.example.tinkoff.model.room.repositoriesImplementation.MessagesRoomRepositoryImpl
import com.example.tinkoff.presentation.applications.di.FragmentScope
import dagger.Binds
import dagger.Module

@Module
interface MessagesRepositoryModule {
    @Binds
    @FragmentScope
    fun provideMessagesRepository(messagesRepositoryImpl: MessagesRepositoryImpl): MessagesRepository

    @Binds
    @FragmentScope
    fun provideMessagesApiRepository(messagesApiRepositoryImpl: MessagesApiRepositoryImpl): MessagesApiRepository

    @Binds
    @FragmentScope
    fun provideMessagesRoomRepository(messagesRoomRepositoryImpl: MessagesRoomRepositoryImpl) : MessagesRoomRepository
}
