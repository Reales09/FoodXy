package com.example.foodxy.ui.home.store

import com.example.foodxy.data.model.Product

interface OnProductListener {

    fun onClick(product: Product)
}