package com.example.foodxy.data.remote.home

import com.example.foodxy.core.Resource
import com.example.foodxy.data.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class HomeScreenDataSource {

suspend fun getLatestPost(): Resource<List<Post>>{

        val postList = mutableListOf<Post>()
        val querySnapshot = FirebaseFirestore.getInstance().collection("post").get().await()
        for (post in querySnapshot.documents) {
            post.toObject(Post::class.java) ?. let {fbPost ->
                postList.add(fbPost)
            }
        }
      return Resource.Success(postList)
    }

}
