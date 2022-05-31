package com.example.tinkoff.presentation.activities.handlers.destination

import androidx.navigation.NavDestination

interface DestinationChangedHandlerInterface {
    fun handleState(destination: NavDestination)
}
