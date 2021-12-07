package com.example.foodxy.ui.home.store.products

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.foodxy.R
import com.example.foodxy.data.model.Product
import com.example.foodxy.databinding.ItemProductBinding


class ProductAdapter (val productList: MutableList<Product>,
                      private val listener: OnProductListener
) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_product,parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]

        holder.setListener(product)

        if (product.id == null){
            holder.binding.containeProduct.visibility = View.GONE
            holder.binding.btnMore.visibility = View.VISIBLE
        }else{
            holder.binding.containeProduct.visibility = View.VISIBLE
            holder.binding.btnMore.visibility = View.GONE


            holder.binding.tvName.text = product.name
            holder.binding.tvPrice.text = product.price.toString()
            holder.binding.tvQuantity.text = product.quantity.toString()

            Glide.with(context)
                .load(product.imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_access_time)
                .error(R.drawable.ic_broken_image)
                .centerCrop()
                .into(holder.binding.imgProduct)

        }


    }

    override fun getItemCount(): Int = productList.size

    fun add(product: Product){
        if (!productList.contains(product)){
            //productList.add(product)
                productList.add(productList.size -1, product)
            notifyItemInserted(productList.size - 2)
        }else{

            update(product)
        }
    }

    fun update(product: Product){
        val index = productList.indexOf(product)
        if (index != -1){
            productList.set(index, product)
            notifyItemChanged(index)
        }
    }

    fun delete(product: Product){
        val index = productList.indexOf(product)
        if (index != -1){
            productList.removeAt(index)
            notifyItemRemoved(index)
        }
    }



    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val  binding = ItemProductBinding.bind(view)

        fun setListener(product: Product){
            binding.root.setOnClickListener{
                listener.onClick(product)
            }

            binding.btnMore.setOnClickListener {
                listener.loadMore()
            }

        }

    }

}