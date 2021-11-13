package com.example.foodxy.data.remote.home

import com.example.foodxy.core.Result
import com.example.foodxy.data.model.Post
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.lang.Exception


class HomeScreenDataSource {

    suspend fun getLatestPost(): Flow<Result<List<Post>>> = callbackFlow {

        val postList = mutableListOf<Post>()

        var postReference: Query? = null

        try {
            postReference = FirebaseFirestore.getInstance().collection("post")
                .orderBy("created_at", Query.Direction.DESCENDING)

        } catch (e: Throwable) {

            close(e)
        }

        val suscription = postReference?.addSnapshotListener { value, error ->

            if (value == null) return@addSnapshotListener

            try {
                postList.clear()
                for (post in value?.documents) {

                    post.toObject(Post::class.java)?.let { fbPost ->
                        fbPost.apply {
                            created_at = post.getTimestamp(
                                "created_at",
                                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                            )?.toDate()
                        }
                        postList.add(fbPost)
                    }
                }

            }catch (e: Exception){
                close(e)

            }


            offer(Result.Success(postList))

        }

        awaitClose { suscription?.remove() }

    }

}
