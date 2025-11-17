// This file defines the RecyclerView adapter for displaying ShoppingListItem objects.
// It uses ListAdapter for efficient updates, binds data to the item_shopping_list.xml layout,
// and handles checkbox state changes and applying strikethrough text.

package com.fridgefairy.android.ui.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fridgefairy.android.R
import com.fridgefairy.android.data.entities.ShoppingListItem

class ShoppingListAdapter(
    private val onItemClick: (ShoppingListItem) -> Unit,
    private val onCheckChanged: (ShoppingListItem) -> Unit
) : ListAdapter<ShoppingListItem, ShoppingListAdapter.ShoppingListViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shopping_list, parent, false)
        return ShoppingListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onItemClick, onCheckChanged)
    }

    class ShoppingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_purchased)
        private val nameTextView: TextView = itemView.findViewById(R.id.text_item_name)
        private val quantityTextView: TextView = itemView.findViewById(R.id.text_quantity)
        private val categoryTextView: TextView = itemView.findViewById(R.id.text_category)

        fun bind(
            item: ShoppingListItem,
            onItemClick: (ShoppingListItem) -> Unit,
            onCheckChanged: (ShoppingListItem) -> Unit
        ) {
            nameTextView.text = item.name
            quantityTextView.text = "Qty: ${item.quantity}"
            categoryTextView.text = item.category
            checkBox.isChecked = item.isPurchased


            if (item.isPurchased) {
                nameTextView.paintFlags = nameTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                quantityTextView.paintFlags = quantityTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                nameTextView.paintFlags = nameTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                quantityTextView.paintFlags = quantityTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            checkBox.setOnCheckedChangeListener { _, _ ->
                onCheckChanged(item)
            }

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ShoppingListItem>() {
        override fun areItemsTheSame(oldItem: ShoppingListItem, newItem: ShoppingListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShoppingListItem, newItem: ShoppingListItem): Boolean {
            return oldItem == newItem
        }
    }
}