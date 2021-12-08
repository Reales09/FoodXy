package com.example.foodxy.data.remote.auth

import android.graphics.Bitmap
import android.net.Uri
import com.example.foodxy.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class AuthDataSource {

    suspend fun singIn(email:String, password: String) : FirebaseUser?{

        val authResult = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
        return authResult.user

    }

   suspend fun singUp(email: String, password: String, username: String): FirebaseUser? {
       val authResult = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()

       authResult.user?.uid?.let { uid ->
           FirebaseFirestore.getInstance().collection("users").document(uid).set(User(email,username,"foto_URL.png")).await()
       }

       return authResult.user
    }

    suspend fun updateUserProfile(imageBitmap: Bitmap){

        val user = FirebaseAuth.getInstance().currentUser
        val imageRef = FirebaseStorage.getInstance().reference.child("${user?.uid}/profile_picture")
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG,90, baos)
        val dowloadUrl = imageRef.putBytes(baos.toByteArray()).await().storage.downloadUrl.await().toString()
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(Uri.parse(dowloadUrl))
            .build()

        user?.updateProfile(profileUpdates)?.await()

    }

}