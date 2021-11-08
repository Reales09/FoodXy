package com.example.foodxy.domain

import com.example.foodxy.core.Resource
import com.example.foodxy.data.model.Post

interface HomeScreenRepo {

    suspend fun getLatestPost(): Resource<List<Post>>
}