package com.example.madlevel8.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.madlevel8.R
import com.example.madlevel8.databinding.ItemProductBinding
import com.example.madlevel8.model.Product

class ProductAdapter(private val products: List<Product>) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemProductBinding.bind(itemView)

        // Fill the CardView with the name and vegan status of the product.
        fun bind(product: Product) {
            binding.tvName.text = product.name

            // Show whether the product is vegan by text and color (green for vegan products and red for non-vegan products).
            if (product.vegan) {
                binding.tvName.setTextColor(Color.parseColor("#669900"))
                binding.tvVegan.setTextColor(Color.parseColor("#669900"))
                binding.tvVegan.text = " is vegan."
            } else {
                binding.tvName.setTextColor(Color.parseColor("#CC0000"))
                binding.tvVegan.setTextColor(Color.parseColor("#CC0000"))
                binding.tvVegan.text = " is not vegan."
            }
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
        holder.bind(products[position])
    }
}