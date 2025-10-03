package com.fridgefairy.android.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fridgefairy.android.R
import com.fridgefairy.android.data.entities.FoodItem
import java.text.SimpleDateFormat
import java.util.*

class FoodItemAdapter(private val onItemClick: (FoodItem) -> Unit) :
    ListAdapter<FoodItem, FoodItemAdapter.FoodItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return FoodItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        val foodItem = getItem(position)
        holder.bind(foodItem)
        holder.itemView.setOnClickListener { onItemClick(foodItem) }
    }

    class FoodItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.text_food_name)
        private val expirationTextView: TextView = itemView.findViewById(R.id.text_expiration)
        private val quantityTextView: TextView = itemView.findViewById(R.id.text_quantity)
        private val categoryTextView: TextView = itemView.findViewById(R.id.text_category)

        fun bind(foodItem: FoodItem) {
            nameTextView.text = foodItem.name
            quantityTextView.text = "Qty: ${foodItem.quantity}"
            categoryTextView.text = foodItem.category

            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val expirationDate = Date(foodItem.expirationDate)
            expirationTextView.text = "Expires: ${dateFormat.format(expirationDate)}"

            // Change color based on expiration status
            when {
                foodItem.isExpired -> {
                    expirationTextView.setTextColor(
                        ContextCompat.getColor(itemView.context, R.color.expired_red)
                    )
                }
                foodItem.isExpiringSoon -> {
                    expirationTextView.setTextColor(
                        ContextCompat.getColor(itemView.context, R.color.expiring_orange)
                    )
                }
                else -> {
                    expirationTextView.setTextColor(
                        ContextCompat.getColor(itemView.context, R.color.fresh_green)
                    )
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<FoodItem>() {
        override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem == newItem
        }
    }
}
