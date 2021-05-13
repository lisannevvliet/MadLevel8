package com.example.madlevel8.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.madlevel8.R
import com.example.madlevel8.databinding.ItemProductBinding
import com.example.madlevel8.model.Product

class ProductAdapter(private val products: List<Product>, private val onClick: (Product) -> Unit) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener { onClick(products[adapterPosition]) }
        }

        private val binding = ItemProductBinding.bind(itemView)

        // Fill the RecyclerView with the name ans vegan status of the product.
        fun databind(product: Product) {
            binding.tvName.text = product.name
            binding.tvVegan.text = product.vegan.toString()
        }
    }

    // Create and return a ViewHolder object, inflate a standard layout called simple_list_item_1.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false))
    }

    // Return the size of the list.
    override fun getItemCount(): Int {
        return products.size
    }

    // Display the data at the specified position, called by RecyclerView.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.databind(products[position])
    }
}