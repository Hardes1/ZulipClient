package com.example.tinkoff.presentation.applications.di

import dagger.Module
import dagger.Provides
import vivid.money.elmslie.core.switcher.Switcher

@Module
class SwitchersModule {
    @Provides
    fun provideSwitcher(): Switcher {
        return Switcher()
    }
}
