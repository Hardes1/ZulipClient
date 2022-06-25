package com.example.tinkoff.presentation.fragments.messages.di

import com.example.tinkoff.presentation.activities.di.MainActivityComponent
import com.example.tinkoff.presentation.applications.di.FragmentScope
import com.example.tinkoff.presentation.fragments.messages.MessagesFragment
import dagger.Component

@FragmentScope
@Component(
    modules = [MessagesRepositoryModule::class, MessagesElmModule::class],
    dependencies = [MainActivityComponent::class]
)
interface MessagesComponent {
    fun inject(messagesFragment: MessagesFragment)

    @Component.Factory
    interface Factory {
        fun create(mainActivityComponent: MainActivityComponent): MessagesComponent
    }
}
