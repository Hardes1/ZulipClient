package com.example.tinkoff.presentation.activities.handlers.destination

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.navigation.NavDestination
import com.example.tinkoff.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class ActivityDestinationHandlerImpl(
    private val navView: BottomNavigationView,
    private val context: Context,
    private val window: Window,
    private val supportActionBar: androidx.appcompat.app.ActionBar?
) : ActivityDestinationChangedHandler {
    override fun handleState(destination: NavDestination) {
        val messageInputMode: Int =
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
        val bottomNavViewInputMode: Int = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        when (destination.id) {
            R.id.navigation_other_profile -> {
                setAppBarColor(R.color.color_bg_black)
                setSoftInputMode(bottomNavViewInputMode)
                navView.visibility = View.GONE
            }
            R.id.navigation_profile -> {
                setAppBarColor(R.color.color_bg_black)
                setSoftInputMode(bottomNavViewInputMode)
                navView.visibility = View.VISIBLE
            }
            R.id.navigation_people -> {
                setAppBarColor(R.color.content_layout_bg_color)
                setSoftInputMode(bottomNavViewInputMode)
                navView.visibility = View.VISIBLE
            }
            R.id.navigation_stream_tabs -> {
                setAppBarColor(R.color.content_layout_bg_color)
                setSoftInputMode(bottomNavViewInputMode)
                navView.visibility = View.VISIBLE
            }
            R.id.navigation_message -> {
                setAppBarColor(R.color.topic_color)
                setSoftInputMode(messageInputMode)
                navView.visibility = View.GONE
            }
        }
    }

    private fun setSoftInputMode(mode: Int) {
        window.setSoftInputMode(mode)
    }

    private fun setAppBarColor(colorId: Int) {
        val color = ContextCompat.getColor(
            context,
            colorId
        )
        window.statusBarColor = color
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                color
            )
        )
    }
}
