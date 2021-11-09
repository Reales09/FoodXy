package com.example.foodxy.domain.home

import com.example.foodxy.core.Result
import com.example.foodxy.data.model.Post

interface HomeScreenRepo {

    suspend fun getLatestPost(): Result<List<Post>>
}