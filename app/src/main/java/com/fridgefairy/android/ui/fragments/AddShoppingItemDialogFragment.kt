package com.fridgefairy.android.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.fridgefairy.android.R
import com.fridgefairy.android.data.entities.ShoppingListItem

class AddShoppingItemDialogFragment : DialogFragment() {

    var onItemAdded: ((ShoppingListItem) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_shopping_item, null)

        val editTextName = view.findViewById<EditText>(R.id.edit_text_item_name)
        val editTextCategory = view.findViewById<EditText>(R.id.edit_text_category)
        val editTextQuantity = view.findViewById<EditText>(R.id.edit_text_quantity)

        builder.setView(view)
            .setTitle("Add Shopping Item")
            .setPositiveButton("Add") { dialog, id ->
                val name = editTextName.text.toString()
                val category = editTextCategory.text.toString()
                val quantity = editTextQuantity.text.toString().toIntOrNull() ?: 1

                if (name.isNotBlank()) {
                    val item = ShoppingListItem(
                        name = name,
                        category = category.ifBlank { "General" },
                        quantity = quantity
                    )

                    onItemAdded?.invoke(item)
                }
            }
            .setNegativeButton("Cancel") { dialog, id ->
                dialog.dismiss()
            }

        return builder.create()
    }
}