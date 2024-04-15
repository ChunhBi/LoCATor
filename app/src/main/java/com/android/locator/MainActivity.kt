package com.android.locator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.locator.databinding.HomeBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: HomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

//////////////////// Works only for Fragment, FragmentContainerView will handle itself /////////////
//        val navView: BottomNavigationView = binding.bottomNavigationView
//
//        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_home, R.id.navigation_home
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
//
////////////////////////////////////////////////////////////////////////////////////////////////////
    }
}