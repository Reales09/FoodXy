package com.example.foodxy.domain.home

import com.example.foodxy.core.Result
import com.example.foodxy.data.model.Post
import kotlinx.coroutines.flow.Flow

interface HomeScreenRepo {

    suspend fun getLatestPost(): Result<List<Post>>

    suspend fun registerLikeButtonState(postId: String, liked: Boolean)

}