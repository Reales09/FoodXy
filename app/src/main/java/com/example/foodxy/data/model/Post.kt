package com.example.foodxy.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Post (val profile_pictures: String ="",
                 val profile_name: String = "",
                 @ServerTimestamp
                 var created_at: Date?=null,
                 val post_image: String = "",
                 val post_description: String="",
                 val uid: String = ""
)