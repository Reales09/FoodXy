package com.example.foodxy.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.foodxy.R
import com.example.foodxy.core.Result
import com.example.foodxy.data.remote.auth.AuthDataSource
import com.example.foodxy.databinding.FragmentProfileBinding
import com.example.foodxy.domain.auth.AuthRepoImpl
import com.example.foodxy.presentation.auth.AuthViewModel
import com.example.foodxy.presentation.auth.AuthViewModelFactory
import com.example.foodxy.ui.home.store.MainAux
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null

    private val REQUEST_IMAGE_CAPTURE = 1
    private var bitmap: Bitmap? = null

    private val viewModel by viewModels<AuthViewModel> { AuthViewModelFactory(
        AuthRepoImpl(
        AuthDataSource()
    )
    ) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding?.let {
            return it.root
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        getUser()
        configButtons()
        takePhoto()

    }


    private fun getUser() {
        binding?.let { binding ->

            FirebaseAuth.getInstance().currentUser?.let { user ->
                binding.etFullName.setText(user.displayName)
                binding.etPhotoUrl.setText(user.photoUrl.toString())
                Glide.with(this)
                    .load(user.photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_access_time)
                    .error(R.drawable.ic_broken_image)
                    .centerCrop()
                    .circleCrop()
                    .into(binding.ibProfile)

                setupActionBar()


            }

        }
    }

    private fun configButtons() {

        binding?.let { binding ->

            binding.btnUpdate.setOnClickListener {

                binding.etFullName.clearFocus()
                binding.etPhotoUrl.clearFocus()
                updateUserProfile(binding)
            }

        }

    }
    private fun setupActionBar(){

        (activity as? AppCompatActivity)?.let {
            it.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            it.supportActionBar?.title = getString(R.string.profile_title)
            setHasOptionsMenu(false)
        }

    }

private fun takePhoto(){

    binding?.let { binding ->

        binding.tvTakePhoto.setOnClickListener {

            findNavController().navigate(R.id.action_profileFragment_to_setupProfileFragment)
        }

    }
}



    private fun updateUserProfile(binding: FragmentProfileBinding) {
        FirebaseAuth.getInstance().currentUser?.let {user ->
            val profileUpdated = UserProfileChangeRequest.Builder()
                .setDisplayName(binding.etFullName.text.toString().trim())
                .setPhotoUri(Uri.parse(binding.etPhotoUrl.text.toString().trim()))
                .build()

            user.updateProfile(profileUpdated)

                .addOnSuccessListener {

                    Toast.makeText(activity, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                    (activity as? MainAux)?.updateTitle(user)
                    activity?.onBackPressed()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Error al actualizar usuario", Toast.LENGTH_SHORT).show()
                }
        }

    }



    override fun onDestroyView() {
        (activity as? MainAux )?.showButton(true)
        super.onDestroyView()
        binding = null
    }

    override fun onDestroy() {

        (activity as? AppCompatActivity)?.let {
            it.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            setHasOptionsMenu(false)
        }

        super.onDestroy()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding?.ibProfile?.setImageBitmap(imageBitmap)
            bitmap = imageBitmap
        }
    }



}