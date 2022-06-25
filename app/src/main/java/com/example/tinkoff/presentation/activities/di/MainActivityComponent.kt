package com.example.tinkoff.presentation.activities.di

import android.content.Context
import com.example.tinkoff.model.network.services.MessagesService
import com.example.tinkoff.model.network.services.StreamsService
import com.example.tinkoff.model.network.services.UsersService
import com.example.tinkoff.model.room.dao.MessagesDao
import com.example.tinkoff.model.room.dao.StreamsDao
import com.example.tinkoff.model.room.dao.TopicsDao
import com.example.tinkoff.presentation.activities.MainActivity
import com.example.tinkoff.presentation.applications.di.ActivityScope
import com.example.tinkoff.presentation.applications.di.AppComponent
import dagger.Component
import vivid.money.elmslie.core.switcher.Switcher

@ActivityScope
@Component(dependencies = [AppComponent::class])
interface MainActivityComponent {
    fun getMessagesService(): MessagesService

    fun getMessagesDao(): MessagesDao

    fun getStreamsService(): StreamsService

    fun getStreamsDao(): StreamsDao

    fun getTopicsDao(): TopicsDao

    fun getUsersService(): UsersService

    fun getSwitcher(): Switcher

    fun getContext(): Context

    fun inject(mainActivity: MainActivity)
}
