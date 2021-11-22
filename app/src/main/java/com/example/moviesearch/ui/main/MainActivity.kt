package com.example.moviesearch.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.moviesearch.R
import com.example.moviesearch.databinding.ActivityMainBinding
import com.example.moviesearch.utils.common.toVisibleOrGone
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var activityDataBinding: ActivityMainBinding
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var onDestinationChangeListener: NavController.OnDestinationChangedListener
    private var isShowBottomNav = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDataBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(activityDataBinding.root)
        setupViews()
    }

    override fun onDestroy() {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment)?.navController
            ?.removeOnDestinationChangedListener(onDestinationChangeListener)
        super.onDestroy()
    }

    private fun setupViews() {
        setupBottomNav()
    }

    private fun setupBottomNav() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
        onDestinationChangeListener =
            NavController.OnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.main_page,
                    R.id.favorites_page -> {
                        showActionBar()
                        showBottomNav()
                    }
                    R.id.movie_detail -> {
                        hideActionBar()
                        hideBottomNav()
                    }
                    else -> {
                        showBottomNav()
                        showBottomNav()
                    }
                }
            }
        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        navController.addOnDestinationChangedListener(onDestinationChangeListener)
        bottomNavigation.setupWithNavController(navController)
    }

    private fun showActionBar() = supportActionBar?.show()

    private fun hideActionBar() = supportActionBar?.hide()

    private fun showBottomNav() {
        if (!isShowBottomNav) {
            isShowBottomNav = true
            bottomNavigation.visibility = isShowBottomNav.toVisibleOrGone()
        }
    }

    private fun hideBottomNav() {
        if (isShowBottomNav) {
            isShowBottomNav = false
            bottomNavigation.visibility = isShowBottomNav.toVisibleOrGone()
        }
    }

}
