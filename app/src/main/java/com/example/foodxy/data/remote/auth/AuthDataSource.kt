package com.example.foodxy.data.remote.auth

import com.example.foodxy.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

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
}