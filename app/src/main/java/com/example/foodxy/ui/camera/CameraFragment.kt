package com.example.foodxy.ui.camera

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.foodxy.R
import com.example.foodxy.core.Result
import com.example.foodxy.data.remote.camera.CameraDataSource
import com.example.foodxy.data.remote.home.HomeScreenDataSource
import com.example.foodxy.databinding.FragmentCameraBinding
import com.example.foodxy.domain.camera.CameraRepoImpl
import com.example.foodxy.domain.home.HomeScreenRepoImpl
import com.example.foodxy.presentation.HomeScreenViewModel
import com.example.foodxy.presentation.HomeScreenViewModelFactory
import com.example.foodxy.presentation.camera.CameraViewModel
import com.example.foodxy.presentation.camera.CameraViewModelFactory

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var binding: FragmentCameraBinding
    private var bitmap: Bitmap? = null

    private val viewModel by viewModels<CameraViewModel> {
        CameraViewModelFactory(
            CameraRepoImpl(CameraDataSource())
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "No se encontro ninguna app para abrir la camara",
                Toast.LENGTH_SHORT
            ).show()

        }

        binding.btnUploadPhoto.setOnClickListener {

            bitmap?.let {
                viewModel.uploadPhoto(it, binding.editTextDescription.text.toString().trim())
                    .observe(viewLifecycleOwner, { result ->
                        when (result) {
                            is Result.Loading -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Uploading photo...",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is Result.Success -> {
                                findNavController().navigate(R.id.action_cameraFragment_to_homeScreenFragment)
                            }
                            is Result.Failure -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Error ${result.exception}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    })
            }
        }

    }


override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

        val imageBitmap = data?.extras?.get("data") as Bitmap
        binding.postImage.setImageBitmap(imageBitmap)
        bitmap = imageBitmap

    }
}

}