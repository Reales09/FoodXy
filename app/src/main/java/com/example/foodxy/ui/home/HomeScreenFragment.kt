package com.example.foodxy.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.foodxy.R
import com.example.foodxy.core.Resource
import com.example.foodxy.data.model.Post
import com.example.foodxy.data.remote.HomeScreenDataSource
import com.example.foodxy.databinding.FragmentHomeScreenBinding
import com.example.foodxy.domain.HomeScreenRepoImpl
import com.example.foodxy.presentation.HomeScreenViewModel
import com.example.foodxy.presentation.HomeScreenViewModelFactory
import com.example.foodxy.ui.home.adapter.HomeScreenAdapter
import com.google.firebase.Timestamp

class HomeScreenFragment : Fragment(R.layout.fragment_home_screen) {

    private lateinit var binding: FragmentHomeScreenBinding
    private val viewModel by viewModels<HomeScreenViewModel>{HomeScreenViewModelFactory(HomeScreenRepoImpl(HomeScreenDataSource()))}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeScreenBinding.bind(view)

        viewModel.fetchLatestPost().observe(viewLifecycleOwner, Observer {result ->

            when(result){
                is Resource.Loading ->{
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success ->{
                    binding.progressBar.visibility = View.GONE
                    binding.rvHome.adapter = HomeScreenAdapter(result.data)
                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Ocurrio un error ${result.exception}", Toast.LENGTH_SHORT).show()
                }
            }

        })


    }

}
