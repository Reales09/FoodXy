package com.example.foodxy.domain

import com.example.foodxy.core.Resource
import com.example.foodxy.data.model.Post
import com.example.foodxy.data.remote.HomeScreenDataSource

class HomeScreenRepoImpl(private val dataSource: HomeScreenDataSource) : HomeScreenRepo {

    override suspend fun getLatestPost(): Resource<List<Post>> = dataSource.getLatestPost()



}