package com.example.foodxy.ui.home

import com.example.foodxy.data.model.Product

interface OnProductListener {

    fun onClick(product: Product)
    fun onLongClick(product: Product)
}