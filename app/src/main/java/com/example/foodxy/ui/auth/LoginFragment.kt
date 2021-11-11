package com.example.foodxy.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.foodxy.R
import com.example.foodxy.data.remote.auth.AuthDataSource
import com.example.foodxy.databinding.FragmentLoginBinding
import com.example.foodxy.domain.auth.AuthRepoImpl
import com.google.firebase.auth.FirebaseAuth
import com.example.foodxy.core.Result
import com.example.foodxy.presentation.auth.AuthViewModel
import com.example.foodxy.presentation.auth.AuthViewModelFactory


class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val firebaseAuth by lazy {FirebaseAuth.getInstance()}
    private val viewModel by viewModels<AuthViewModel> { AuthViewModelFactory(AuthRepoImpl(
        AuthDataSource()
    ))  }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        isUserLoggedIn()
        doLogin()
        gotoSignUpPage()

    }

    private fun isUserLoggedIn(){
        firebaseAuth.currentUser?.let { user ->
            if(user.displayName.isNullOrEmpty()) {
                findNavController().navigate(R.id.action_loginFragment_to_setupProfileFragment)

                Toast.makeText(requireContext(), "Debes agregar una imagen y nombre de usuario", Toast.LENGTH_LONG).show()
            }else{
                findNavController().navigate(R.id.action_loginFragment_to_homeScreenFragment)
            }
        }
    }

    private fun doLogin(){

        binding.btnSignin.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            validateCredentials(email, password)
            signIn(email, password)

        }

    }

    private fun validateCredentials (email: String, password: String){

        if (email.isEmpty()){
            binding.editTextEmail.error = "E-mail is empty"
        }
        if (password.isEmpty()){
            binding.editTextPassword.error = "Password is empty"

        }

    }

    private fun gotoSignUpPage(){

        binding.txtSignup.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }


    }

    private fun signIn(email: String, password: String){

        viewModel.signIn(email, password).observe(viewLifecycleOwner, Observer { result ->
            when(result){
                is Result.Loading ->{

                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSignin.isEnabled = false
                }
                is Result.Success ->{

                    binding.progressBar.visibility = View.GONE

                    if(result.data?.displayName.isNullOrEmpty()) {
                        findNavController().navigate(R.id.action_loginFragment_to_setupProfileFragment)
                        Toast.makeText(requireContext(), "Debes agregar una imagen y nombre de usuario", Toast.LENGTH_LONG).show()
                    }else{
                        findNavController().navigate(R.id.action_loginFragment_to_homeScreenFragment)
                        Toast.makeText(requireContext(), "Bienvenido ${result.data?.email}", Toast.LENGTH_SHORT).show()
                    }

                }
                is  Result.Failure -> {

                    binding.progressBar.visibility = View.GONE
                    binding.btnSignin.isEnabled = true
                    Toast.makeText(requireContext(), "Error ${result.exception}", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

}