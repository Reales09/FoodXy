package com.example.foodxy.ui.home.store.products

import com.example.foodxy.data.model.Product

interface OnProductListener {

    fun onClick(product: Product)
    fun loadMore()
}