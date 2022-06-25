package com.example.tinkoff.presentation.applications.di

import android.content.Context
import com.example.tinkoff.model.network.services.MessagesService
import com.example.tinkoff.model.network.services.StreamsService
import com.example.tinkoff.model.network.services.UsersService
import com.example.tinkoff.model.room.dao.MessagesDao
import com.example.tinkoff.model.room.dao.StreamsDao
import com.example.tinkoff.model.room.dao.TopicsDao
import dagger.BindsInstance
import dagger.Component
import vivid.money.elmslie.core.switcher.Switcher
import javax.inject.Singleton

@Singleton
@Component(modules = [RetrofitModule::class, RoomModule::class, SwitchersModule::class])
interface AppComponent {

    fun getMessagesService(): MessagesService

    fun getMessagesDao(): MessagesDao

    fun getStreamsService(): StreamsService

    fun getStreamsDao(): StreamsDao

    fun getTopicsDao(): TopicsDao

    fun getUsersService(): UsersService

    fun getSwitcher(): Switcher

    fun getContext(): Context

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}
