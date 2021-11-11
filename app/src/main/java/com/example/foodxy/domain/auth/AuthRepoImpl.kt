package com.example.foodxy.domain.auth

import android.graphics.Bitmap
import com.example.foodxy.data.remote.auth.AuthDataSource
import com.google.firebase.auth.FirebaseUser

class AuthRepoImpl(private val datasource: AuthDataSource) : AuthRepo {

    override suspend fun signIn(email: String, password: String): FirebaseUser? = datasource.singIn(email, password)
    override suspend fun signUp(email: String, password: String, username: String): FirebaseUser? = datasource.singUp(email,password,username)
    override suspend fun updateProfile(imageBitmap: Bitmap, username: String) = datasource.updateUserProfile(imageBitmap,username)

}