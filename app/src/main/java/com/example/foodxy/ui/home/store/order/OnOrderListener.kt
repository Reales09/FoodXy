package com.example.foodxy.ui.home.store.order

import com.example.foodxy.data.model.Order

interface OnOrderListener {

    fun onTrack(order: Order)
    fun onStartChat(order: Order)

}