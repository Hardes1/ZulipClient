package com.example.tinkoff.presentation.fragments.profile.di

import com.example.tinkoff.presentation.activities.di.MainActivityComponent
import com.example.tinkoff.presentation.applications.di.FragmentScope
import com.example.tinkoff.presentation.fragments.profile.ProfileFragment
import dagger.Component

@FragmentScope
@Component(
    modules = [UserRepositoryModule::class, UserElmModule::class],
    dependencies = [MainActivityComponent::class]
)
interface UserComponent {
    fun inject(profileFragment: ProfileFragment)

    @Component.Factory
    interface Factory {
        fun create(mainActivityComponent: MainActivityComponent): UserComponent
    }
}
