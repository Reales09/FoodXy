package com.example.foodxy.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.foodxy.R
import com.example.foodxy.core.Constants
import com.example.foodxy.core.hide
import com.example.foodxy.core.show
import com.example.foodxy.data.model.Product
import com.example.foodxy.databinding.ActivityMainBinding
import com.example.foodxy.databinding.FragmentStoreBinding
import com.example.foodxy.ui.home.OnProductListener
import com.example.foodxy.ui.home.ProductAdapter
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


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
