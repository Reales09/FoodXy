package com.example.foodxy.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foodxy.R
import com.example.foodxy.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment(R.layout.fragment_register) {


    private lateinit var binding: FragmentRegisterBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)

        signUp()
    }

    private fun signUp(){

        binding.btnRegister.setOnClickListener {
            val username = binding.editTextUsername.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            val confirmPassword = binding.editTextConfirmPassword.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()

            if (password != confirmPassword){

                binding.editTextConfirmPassword.error = "Password does not match"

                return@setOnClickListener

            }

            if (username.isEmpty()){

                binding.editTextUsername.error = "Username is empty"
            }

            if (email.isEmpty()){

                binding.editTextEmail.error = "Email is empty"
            }

            if (password.isEmpty()){

                binding.editTextPassword.error = "Password is empty"

            }
            if (confirmPassword.isEmpty()){
                binding.editTextConfirmPassword.error = "Confirm Password is empty"

            }



            Log.d("signUpData","data $username $password $confirmPassword $email")
        }


    }

}