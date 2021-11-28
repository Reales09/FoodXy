package com.example.foodxy.ui.home.store.cart

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodxy.data.model.Product
import com.example.foodxy.databinding.FragmentCartBinding
import com.example.foodxy.ui.home.store.MainAux
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CartFragment : BottomSheetDialogFragment (), OnCartListener {

    private var binding: FragmentCartBinding?=null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    private lateinit var adapter: ProductCartAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = FragmentCartBinding.inflate(LayoutInflater.from(activity))

        binding?.let {

            val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
            bottomSheetDialog.setContentView(it.root)

            bottomSheetBehavior = BottomSheetBehavior.from(it.root.parent as View)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            setupRecyclerView()
            setupButtons()
            getProducts()

            return bottomSheetDialog

        }

        return super.onCreateDialog(savedInstanceState)
    }

    private fun setupRecyclerView() {
        binding?.let {
            adapter = ProductCartAdapter(mutableListOf(), this)

            it.recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = this@CartFragment.adapter
            }
/*
            (1..6).forEach{
                val product = Product(it.toString(),"Producto $it","this product is $it","",it, 2.0*it)
                adapter.add(product)
            }

 */

        }
    }

    private fun setupButtons(){

        binding?.let {
            it.ibCancel.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

    }

    private fun getProducts(){


        (activity as? MainAux)?.getProductCart()?.forEach {
            adapter.add(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null

    }

    override fun setQuantity(product: Product) {

    }

    override fun showTotal(total: Double) {

    }
}