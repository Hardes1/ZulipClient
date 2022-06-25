package com.example.tinkoff.presentation.fragments.messages.di

import com.example.tinkoff.model.repositories.MessagesRepository
import com.example.tinkoff.model.storages.MessagesStorage
import com.example.tinkoff.model.storagesImplementation.MessagesStorageImpl
import com.example.tinkoff.presentation.applications.di.FragmentScope
import com.example.tinkoff.presentation.fragments.messages.elm.MessagesActor
import com.example.tinkoff.presentation.fragments.messages.elm.MessagesReducer
import com.example.tinkoff.presentation.fragments.messages.elm.MessagesState
import dagger.Binds
import dagger.Module
import dagger.Provides
import vivid.money.elmslie.core.switcher.Switcher

@Module(includes = [MessagesElmModule.BindsMessagesElmModule::class])
class MessagesElmModule {

    @Module
    interface BindsMessagesElmModule {
        @Binds
        @FragmentScope
        fun provideMessagesStorage(messagesStorageImpl: MessagesStorageImpl): MessagesStorage
    }

    @Provides
    @FragmentScope
    fun provideMessagesReducer(): MessagesReducer {
        return MessagesReducer()
    }

    @Provides
    @FragmentScope
    fun provideMessagesState(): MessagesState {
        return MessagesState()
    }

    @Provides
    @FragmentScope
    fun providesMessagesActor(
        switcher: Switcher,
        messagesRepository: MessagesRepository
    ): MessagesActor {
        return MessagesActor(switcher, messagesRepository)
    }
}
