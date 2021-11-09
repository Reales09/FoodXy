package com.example.foodxy.domain.home

import com.example.foodxy.core.Result
import com.example.foodxy.data.model.Post
import com.example.foodxy.data.remote.home.HomeScreenDataSource

class HomeScreenRepoImpl(private val dataSource: HomeScreenDataSource) : HomeScreenRepo {

    override suspend fun getLatestPost(): Result<List<Post>> = dataSource.getLatestPost()



}