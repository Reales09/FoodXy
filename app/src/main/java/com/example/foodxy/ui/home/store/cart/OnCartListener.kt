package com.example.foodxy.ui.home.store.cart

import com.example.foodxy.data.model.Product

interface OnCartListener {

    fun setQuantity(product: Product)
    fun showTotal(total: Double)

}