package com.example.tinkoff.presentation.activities.handlers.destination

import androidx.navigation.NavDestination

interface ActivityDestinationChangedHandler {
    fun handleState(destination: NavDestination)
}
