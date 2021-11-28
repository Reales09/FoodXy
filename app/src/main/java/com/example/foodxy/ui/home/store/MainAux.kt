package com.example.foodxy.ui.home.store

import com.example.foodxy.data.model.Product

interface MainAux {

    fun getProductCart(): MutableList<Product>

    fun getProductSelected(): Product?
    fun showButton(isVisible: Boolean)

}