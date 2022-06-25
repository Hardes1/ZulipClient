package com.example.tinkoff.presentation.fragments.stream.di

import com.example.tinkoff.presentation.activities.di.MainActivityComponent
import com.example.tinkoff.presentation.applications.di.FragmentScope
import com.example.tinkoff.presentation.fragments.stream.StreamFragment
import dagger.Component

@Component(
    modules = [StreamsRepositoryModule::class, StreamsElmModule::class],
    dependencies = [MainActivityComponent::class]
)
@FragmentScope
interface StreamsComponent {
    fun inject(streamFragment: StreamFragment)

    @Component.Factory
    interface Factory {
        fun create(mainActivityComponent: MainActivityComponent): StreamsComponent
    }
}
