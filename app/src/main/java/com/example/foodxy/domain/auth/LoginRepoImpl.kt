package com.example.foodxy.domain.auth

import com.example.foodxy.data.remote.auth.LoginDataSource
import com.google.firebase.auth.FirebaseUser

class LoginRepoImpl(private val datasource: LoginDataSource) : LoginRepo {

    override suspend fun signIn(email: String, password: String): FirebaseUser? = datasource.singIn(email, password)

}