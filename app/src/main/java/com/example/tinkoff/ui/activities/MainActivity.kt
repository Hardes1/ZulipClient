package com.example.tinkoff.ui.activities

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tinkoff.R
import com.example.tinkoff.databinding.ActivityMainBinding
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private var searchItem: MenuItem? = null
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!
    private val navHostFragment: NavHostFragment by lazy {
        supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = navHostFragment.navController
        supportActionBar?.elevation = 0f
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_stream_tabs, R.id.navigation_people, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            searchItem?.collapseActionView()
            val messageInputMode: Int =
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
            val bottomNavViewInputMode: Int = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
            when (destination.id) {
                R.id.navigation_other_profile -> {
                    setAppBarColor(R.color.color_bg_black)
                    setSoftInputMode(bottomNavViewInputMode)
                    binding.navView.visibility = View.GONE
                }
                R.id.navigation_profile -> {
                    setAppBarColor(R.color.color_bg_black)
                    setSoftInputMode(bottomNavViewInputMode)
                    binding.navView.visibility = View.VISIBLE
                }
                R.id.navigation_people -> {
                    setAppBarColor(R.color.content_layout_bg_color)
                    setSoftInputMode(bottomNavViewInputMode)
                    binding.navView.visibility = View.VISIBLE
                }
                R.id.navigation_stream_tabs -> {
                    setAppBarColor(R.color.content_layout_bg_color)
                    setSoftInputMode(bottomNavViewInputMode)
                    binding.navView.visibility = View.VISIBLE
                }
                R.id.navigation_message -> {
                    setAppBarColor(R.color.topic_color)
                    setSoftInputMode(messageInputMode)
                    binding.navView.visibility = View.GONE
                }
            }
        }
    }


    private fun setSoftInputMode(mode: Int) {
        window.setSoftInputMode(mode)
    }

    private fun setAppBarColor(colorId: Int) {
        val color = ContextCompat.getColor(
            baseContext,
            colorId
        )
        window.statusBarColor = color
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                color
            )
        )
    }


    override fun onSupportNavigateUp(): Boolean {
        Timber.d("navigated up")
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_action_menu, menu)
        searchItem = menu?.findItem(R.id.action_search)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}
