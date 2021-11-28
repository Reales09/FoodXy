package com.example.foodxy.ui.home.store.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.foodxy.R
import com.example.foodxy.data.model.Product
import com.example.foodxy.databinding.FragmentDetailBinding


import com.example.foodxy.ui.home.store.MainAux

class DetailFragment: Fragment() {

    private var binding: FragmentDetailBinding? = null
    private var product: Product? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      binding =  FragmentDetailBinding.inflate(inflater, container, false)



        binding?.let {
            return it.root
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getProduct()
        setupButtons()
      
    }


    private fun getProduct() {

     product = (activity as? MainAux)?.getProductSelected()
        product?.let {product ->
            binding?.let {

                it.tvName.text = product.name
                it.tvDescription.text = product.description
                it.tvQuantity.text = getString(R.string.detail_quantity,product.quantity)

                setNewQuantity(product)

                Glide.with(this)
                    .load(product.imgUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_access_time)
                    .error(R.drawable.ic_broken_image)
                    .centerCrop()
                    .into(it.imgProduct)

            }
        }

    }

    private fun setNewQuantity(product: Product){

        binding?.let {

            it.etNewQuantity.setText(product.newQuantity.toString())

            it.tvTotalPrice.text = getString(R.string.detail_total_price, product.totalPrice(), product.newQuantity, product.price)



        }

    }

    private fun setupButtons() {
        product?.let {product ->
            binding?.let {binding ->
                binding.ibSub.setOnClickListener {
                    if (product.newQuantity  > 1){
                        product.newQuantity -= 1

                        setNewQuantity(product)
                    }
                }
                binding.ibSum.setOnClickListener {
                    if (product.newQuantity  < product.quantity){
                        product.newQuantity += 1

                        setNewQuantity(product)

                    }
                }
                binding.efab.setOnClickListener {
                    product.newQuantity = binding.etNewQuantity.text.toString().toInt()
                }

            }
        }
    }

    override fun onDestroyView() {
        (activity as? MainAux )?.showButton(true)
        super.onDestroyView()
        binding=null
    }
}