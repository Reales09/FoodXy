package com.example.foodxy.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodxy.R
import com.example.foodxy.core.BaseViewHolder
import com.example.foodxy.core.TimeUtils
import com.example.foodxy.core.hide
import com.example.foodxy.data.model.Post
import com.example.foodxy.databinding.PostItemViewBinding

class HomeScreenAdapter(private val postList: List<Post>, private val onPostClickerListener: OnPostClickerListener) :
    RecyclerView.Adapter<BaseViewHolder<*>>() {

    private var postClickerListener: OnPostClickerListener? = null

    init {
        postClickerListener = onPostClickerListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {

        val itemBinding =
            PostItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeScreenViewHolder(itemBinding, parent.context)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is HomeScreenViewHolder -> holder.bind(postList[position])
        }
    }

    override fun getItemCount(): Int = postList.size


    private inner class HomeScreenViewHolder(
        val binding: PostItemViewBinding,
        val context: Context
    ) : BaseViewHolder<Post>(binding.root) {
        override fun bind(item: Post) {


            setupProfileInfo(item)
            addPostTimeStamp(item)
            setupPostImage(item)
            setupPostDescription(item)
            tinHeartIcon(item)
            setupLikeCount(item)
            setLikeClickAction(item)

        }


        private fun setupProfileInfo(post: Post) {
            Glide.with(context).load(post.poster?.profile_pictures).centerCrop()
                .into(binding.profilePicture)
            binding.profileName.text = post.poster?.username
        }

        private fun addPostTimeStamp(post: Post) {
            val createdAt = (post.created_at?.time?.div(1000L))?.let {

                TimeUtils.getTimeAgo(it.toInt())
            }


            binding.postTimestamp.text = createdAt
        }

        private fun setupPostImage(post: Post) {
            Glide.with(context).load(post.post_image).centerCrop().into(binding.postImage)
        }

        private fun setupPostDescription(post: Post) {
            if (post.post_description.isEmpty()) {
                binding.postDescription.hide()
            } else {
                binding.postDescription.text = post.post_description
            }
        }

        private fun tinHeartIcon(post: Post) {
            if (!post.liked) {
                binding.likeBtn.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_baseline_favorite_border_24
                    )
                )
                binding.likeBtn.setColorFilter(ContextCompat.getColor(context, R.color.black))

            } else {
                binding.likeBtn.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_baseline_favorite_24
                    )
                )
                binding.likeBtn.setColorFilter(ContextCompat.getColor(context, R.color.red_like))
            }
        }

        private fun setupLikeCount(post: Post) {
            if (post.likes > 0) {
                binding.likeCount.visibility = View.VISIBLE
                binding.likeCount.text = "${post.likes} likes"

            } else {
                binding.likeCount.visibility = View.GONE

            }
        }

        private fun setLikeClickAction(post: Post) {
            binding.likeBtn.setOnClickListener {
                if (post.liked) post.apply { liked = false } else post.apply {
                    liked = true
                    tinHeartIcon(post)
                    postClickerListener?.onLikeButtonClick(post, post.liked)
                }

            }


        }

    }

    interface OnPostClickerListener {
        fun onLikeButtonClick(post: Post, liked: Boolean)
    }
}