package com.example.foodxy.ui.home.store.chat

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foodxy.R
import com.example.foodxy.data.model.Message
import com.example.foodxy.databinding.ItemChatBinding

class ChatAdapter(private val messageList: MutableList<Message>, val listener: OnChatListener): RecyclerView.Adapter<ChatAdapter.ViewHolder>(){

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_chat,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]

        holder.setListener(message)

        var gravity = Gravity.END
        var background = ContextCompat.getDrawable(context, R.drawable.background_chat_client)
        var textColor = ContextCompat.getColor(context,R.color.orange_200)

        if (message.isSentByMe()){
            gravity =Gravity.END
            background = ContextCompat.getDrawable(context, R.drawable.background_chat_support)
            textColor = ContextCompat.getColor(context,R.color.design_default_color_on_primary)
        }

        holder.binding.root.gravity = gravity

        holder.binding.tvMessage.setBackground(background)
        holder.binding.tvMessage.setTextColor(textColor)
        holder.binding.tvMessage.text = message.message
    }

    override fun getItemCount(): Int = messageList.size

    fun add(message: Message){
        if(!messageList.contains(message)){
           messageList.add(message)
            notifyItemInserted(messageList.size -1)

        }
    }

    fun update(message: Message){

        val index = messageList.indexOf(message)
        if(index != -1){

            messageList.set(index, message)
            notifyItemChanged(index)

        }

    }

    fun delete(message: Message){

        val index = messageList.indexOf(message)
        if(index != -1){

            messageList.removeAt(index)
            notifyItemRemoved(index)

        }

    }



    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val binding  = ItemChatBinding.bind(view)

        fun setListener(message: Message){
            binding.tvMessage.setOnLongClickListener {
                listener.deleteMessage(message)
                true
            }
        }
    }
}