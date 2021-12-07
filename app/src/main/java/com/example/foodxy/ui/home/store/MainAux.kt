package com.example.foodxy.ui.home.store

import com.example.foodxy.data.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface MainAux {

    fun getProductCart(): MutableList<Product>
    fun updateTotal()
    fun getProductSelected(): Product?
    fun showButton(isVisible: Boolean)
    fun addProductToCart(product: Product)
    fun clearCart()

    fun updateTitle(user: FirebaseUser)


}