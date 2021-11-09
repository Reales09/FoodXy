package com.example.foodxy.domain.home

import com.example.foodxy.core.Resource
import com.example.foodxy.data.model.Post
import com.example.foodxy.data.remote.home.HomeScreenDataSource
import com.example.foodxy.domain.home.HomeScreenRepo

class HomeScreenRepoImpl(private val dataSource: HomeScreenDataSource) : HomeScreenRepo {

    override suspend fun getLatestPost(): Resource<List<Post>> = dataSource.getLatestPost()



}