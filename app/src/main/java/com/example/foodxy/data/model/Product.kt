package com.example.foodxy.data.model

import com.google.firebase.firestore.Exclude

data class Product (@get:Exclude var id: String?=null,
                    var name:String?=null,
                    var description: String?=null,
                    var imgUrl: String?=null,
                    var quantity: Int =0,
                    @get:Exclude var newQuantity: Int = 1,
                    var price: Double=0.0){

    fun totalPrice(): Double = newQuantity * price

   override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Product

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }


}