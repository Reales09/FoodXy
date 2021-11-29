package com.example.foodxy.ui.home.store.cart

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodxy.R
import com.example.foodxy.core.Constants
import com.example.foodxy.data.model.Order
import com.example.foodxy.data.model.Product
import com.example.foodxy.data.model.ProductOrder
import com.example.foodxy.databinding.FragmentCartBinding
import com.example.foodxy.ui.home.store.MainAux
import com.example.foodxy.ui.home.store.order.OrderActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartFragment : BottomSheetDialogFragment (), OnCartListener {

    private var binding: FragmentCartBinding?=null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    private lateinit var adapter: ProductCartAdapter

    protected var totalPrice = 0.0

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
            it.efab.setOnClickListener {
                requesOrder()
            }
        }

    }

    private fun getProducts(){


        (activity as? MainAux)?.getProductCart()?.forEach {
            adapter.add(it)
        }
    }

    private fun requesOrder(){

        val user = FirebaseAuth.getInstance().currentUser
        user?.let {myUser ->

            enableUi(false)

            val products = hashMapOf<String, ProductOrder>()
            adapter.getProducts().forEach {product ->

                products.put(product.id!!, ProductOrder(product.id!!, product.name!!, product.newQuantity))

            }

            val order = Order(clientId = myUser.uid, products = products, totalPrice = totalPrice, status = 1)

            val db = FirebaseFirestore.getInstance()
            db.collection(Constants.COLL_REQUEST)
                .add(order)
                .addOnSuccessListener {
                    dismiss()
                    (activity as? MainAux)?.clearCart()
                    startActivity(Intent(context, OrderActivity::class.java))

                    Toast.makeText(activity, "Compra realizada", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Error al comprar", Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener {
                    enableUi(true)
                }
        }



    }

    private fun enableUi(enable: Boolean){

        binding?.let {

            it.ibCancel.isEnabled = enable
            it.efab.isEnabled = enable

        }

    }

    override fun onDestroy() {

        (activity as? MainAux)?.updateTotal()
        super.onDestroy()

        binding = null

    }

    override fun setQuantity(product: Product) {

        adapter.update(product)

    }

    override fun showTotal(total: Double) {

        totalPrice = total
        binding?.let {

            it.tvTotal.text = getString(R.string.product_full_cart, total)

        }

    }
}