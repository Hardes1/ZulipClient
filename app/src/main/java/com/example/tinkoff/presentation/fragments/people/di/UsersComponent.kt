package com.example.tinkoff.presentation.fragments.people.di

import com.example.tinkoff.presentation.activities.di.MainActivityComponent
import com.example.tinkoff.presentation.applications.di.FragmentScope
import com.example.tinkoff.presentation.fragments.people.PeopleFragment
import dagger.Component

@FragmentScope
@Component(
    modules = [UsersRepositoryModule::class, UsersElmModule::class],
    dependencies = [MainActivityComponent::class]
)
interface UsersComponent {
    fun inject(peopleFragment: PeopleFragment)

    @Component.Factory
    interface Factory {
        fun create(mainActivityComponent: MainActivityComponent): UsersComponent
    }
}
