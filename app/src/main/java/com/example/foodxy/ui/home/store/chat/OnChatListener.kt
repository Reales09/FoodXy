package com.example.foodxy.ui.home.store.chat

import com.example.foodxy.data.model.Message

interface OnChatListener {

    fun deleteMessage(message: Message)

}