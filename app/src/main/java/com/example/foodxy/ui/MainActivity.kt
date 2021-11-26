package com.example.foodxy.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.foodxy.R
import com.example.foodxy.core.hide
import com.example.foodxy.core.show
import com.example.foodxy.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController



    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_FoodXy)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)


        observeDestinationChange()

    }



    private fun observeDestinationChange(){

        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            when(destination.id){

                R.id.loginFragment -> {

                    binding.bottomNavigationView.hide()
                }

                R.id.registerFragment -> {
                    binding.bottomNavigationView.hide()
                }

                R.id.setupProfileFragment -> {
                    binding.bottomNavigationView.hide()
                }

                R.id.mainActivity2 -> {
                    binding.bottomNavigationView.hide()
                }


                else ->{
                    binding.bottomNavigationView.show()
                }
            }
        }

    }


}
