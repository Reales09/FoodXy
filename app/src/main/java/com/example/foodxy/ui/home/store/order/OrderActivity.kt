package com.example.foodxy.ui.home.store.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodxy.R
import com.example.foodxy.core.Constants
import com.example.foodxy.data.model.Order
import com.example.foodxy.databinding.ActivityOrderBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class OrderActivity : AppCompatActivity(), OnOrderListener {

    private lateinit var binding: ActivityOrderBinding

    private lateinit var adapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupFirestore()

    }



    private fun setupRecyclerView() {
        adapter = OrderAdapter(mutableListOf(),this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrderActivity)
            adapter = this@OrderActivity.adapter
        }
    }

    private fun setupFirestore() {

        val db = FirebaseFirestore.getInstance()

        db.collection(Constants.COLL_REQUEST)
            .get()
            .addOnSuccessListener {
                for (document in it){
                    val order = document.toObject(Order::class.java)
                    order.id = document.id
                    adapter.add(order)
                }
            }
        
            .addOnFailureListener {
                Toast.makeText(this, "Errror al consultar los datos", Toast.LENGTH_SHORT).show()
            }

    }

    override fun onTrack(order: Order) {

    }

    override fun onStartChat(order: Order) {

    }
}