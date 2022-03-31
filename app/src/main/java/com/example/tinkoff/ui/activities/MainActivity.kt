package com.example.tinkoff.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
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
            when (destination.id) {
                R.id.navigation_other_profile -> {
                    binding.navView.visibility = View.INVISIBLE
                }
                else -> {
                    binding.navView.visibility = View.VISIBLE
                }
            }
        }
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
