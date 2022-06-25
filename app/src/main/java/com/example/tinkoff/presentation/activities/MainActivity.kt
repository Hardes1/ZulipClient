package com.example.tinkoff.presentation.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tinkoff.R
import com.example.tinkoff.databinding.ActivityMainBinding
import com.example.tinkoff.presentation.activities.di.DaggerMainActivityComponent
import com.example.tinkoff.presentation.activities.di.MainActivityComponent
import com.example.tinkoff.presentation.activities.handlers.destination.ActivityDestinationChangedHandler
import com.example.tinkoff.presentation.activities.handlers.destination.ActivityDestinationHandlerImpl
import com.example.tinkoff.presentation.applications.ApplicationController

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

    private val destinationChangedHandler: ActivityDestinationChangedHandler by lazy {
        ActivityDestinationHandlerImpl(binding.navView, baseContext, window, supportActionBar)
    }

    private lateinit var mainActivityComponent: MainActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        mainActivityComponent = DaggerMainActivityComponent.builder()
            .appComponent((application as ApplicationController).getAppComponent())
            .build()
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = navHostFragment.navController
        supportActionBar?.elevation = 0f
        mainActivityComponent.inject(this)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_stream_tabs, R.id.navigation_people, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            searchItem?.collapseActionView()
            destinationChangedHandler.handleState(destination)
        }
    }

    fun getMainActivityComponent(): MainActivityComponent {
        return mainActivityComponent
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions_menu, menu)
        searchItem = menu?.findItem(R.id.action_search)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
