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
import com.example.tinkoff.presentation.activities.handlers.destination.ActivityDestinationHandler
import com.example.tinkoff.presentation.activities.handlers.destination.DestinationChangedHandlerInterface

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
    private val destinationChangedHandler: DestinationChangedHandlerInterface by lazy {
        ActivityDestinationHandler(binding.navView, baseContext, window, supportActionBar)
    }

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
            destinationChangedHandler.handleState(destination)
        }
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
