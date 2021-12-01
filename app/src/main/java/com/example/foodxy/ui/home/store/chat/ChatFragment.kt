package com.example.foodxy.ui.home.store.chat

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.foodxy.data.model.Message
import com.example.foodxy.data.model.Order
import com.example.foodxy.databinding.FragmentChatBinding
import com.example.foodxy.ui.home.store.order.OrderAux

class ChatFragment: Fragment(), OnChatListener {


    private var binding: FragmentChatBinding? = null

    private lateinit var  adapter: ChatAdapter

    private var order: Order? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentChatBinding.inflate(inflater, container,false)
        binding?.let {
            return it.root
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getOrder()

    }

    private fun getOrder() {
        order=(activity as? OrderAux)?.getOrderSelected()
        order?.let {
            setupRealtimeDatabase()
        }
    }

    private fun setupRealtimeDatabase() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun deleteMessage(message: Message) {

    }
}