package com.example.foodxy.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.foodxy.R
import com.example.foodxy.core.Constants
import com.example.foodxy.core.Result
import com.example.foodxy.data.remote.auth.AuthDataSource
import com.example.foodxy.databinding.FragmentProfileBinding
import com.example.foodxy.domain.auth.AuthRepoImpl
import com.example.foodxy.presentation.auth.AuthViewModel
import com.example.foodxy.presentation.auth.AuthViewModelFactory
import com.example.foodxy.ui.home.store.MainAux
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null

    private var photoSelectedUri: Uri? = null

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                photoSelectedUri = it.data?.data

                binding?.let {

                    Glide.with(this)
                        .load(photoSelectedUri)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_access_time)
                        .error(R.drawable.ic_broken_image)
                        .centerCrop()
                        .circleCrop()
                        .into(it.ibProfile)

                }

            }

        }


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
                //binding.etPhotoUrl.setText(user.photoUrl.toString())
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

            binding.ibProfile.setOnClickListener {
                openGallery()
            }

            binding.btnUpdate.setOnClickListener {

                binding.etFullName.clearFocus()
                //binding.etPhotoUrl.clearFocus()
                FirebaseAuth.getInstance().currentUser?.let { user ->

                    if (photoSelectedUri == null) {

                        updateUserProfile(binding,user,Uri.parse(""))
                    } else {
                        uploadReducedImage(user)
                    }

                }

            }

        }

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }


    private fun setupActionBar() {

        (activity as? AppCompatActivity)?.let {
            it.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            //it.supportActionBar?.title = getString(R.string.profile_title)
            setHasOptionsMenu(false)
        }

    }

    private fun takePhoto() {

        binding?.let { binding ->

            binding.tvTakePhoto.setOnClickListener {

                findNavController().navigate(R.id.action_profileFragment_to_setupProfileFragment)
            }

        }
    }


    private fun updateUserProfile(binding: FragmentProfileBinding, user: FirebaseUser, uri: Uri) {

        val profileUpdated = UserProfileChangeRequest.Builder()
            .setDisplayName(binding.etFullName.text.toString().trim())
            .setPhotoUri(uri)
            .build()

        user.updateProfile(profileUpdated)

            .addOnSuccessListener {

                Toast.makeText(activity, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                (activity as? MainAux)?.updateTitle(user)
                //activity?.onBackPressed()
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Error al actualizar usuario", Toast.LENGTH_SHORT)
                    .show()
            }

    }

    private fun uploadReducedImage(user: FirebaseUser) {

        val profileRef = FirebaseStorage.getInstance().reference.child(user.uid)
            .child(Constants.MY_PHOTO)

        photoSelectedUri?.let { uri ->
            binding?.let { binding ->

                getBitmapFromUri(uri)?.let { bitmap ->
                    binding.progressBar.visibility = View.VISIBLE

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)


                    profileRef.putBytes(baos.toByteArray())

                        .addOnProgressListener {
                            val progress = (100 * it.bytesTransferred / it.totalByteCount).toInt()
                            it.run {
                                binding.progressBar.progress = progress
                                binding.tvProgress.text = String.format("%s%%", progress)

                            }
                        }

                        .addOnCompleteListener{
                            binding.progressBar.visibility = View.INVISIBLE
                            binding.tvProgress.text = ""
                        }

                        .addOnSuccessListener {

                            it.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                                updateUserProfile(binding, user, downloadUrl)
                            }
                        }

                        .addOnFailureListener {

                            Toast.makeText(activity, "Error al subir imagen", Toast.LENGTH_SHORT)
                                .show()

                        }
                }
            }
        }

    }


private fun getBitmapFromUri(uri: Uri): Bitmap? {

    activity?.let {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            val source = ImageDecoder.createSource(it.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)

        } else {
            MediaStore.Images.Media.getBitmap(it.contentResolver, uri)
        }
        return getResizedImage(bitmap, 720)
    }
    return null

}

private fun getResizedImage(image: Bitmap, maxSize: Int): Bitmap {

    var width = image.width
    var height = image.height

    if (width <= maxSize && height <= maxSize) return image

    val bitmapRatio = width.toFloat() / height.toFloat()
    if (bitmapRatio > 1) {

        width = maxSize
        height = (width / bitmapRatio).toInt()


    } else {
        height = maxSize
        width = (height / bitmapRatio).toInt()
    }

    return Bitmap.createScaledBitmap(image, width, height, true)

}


override fun onDestroyView() {
    (activity as? MainAux)?.showButton(true)
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

}