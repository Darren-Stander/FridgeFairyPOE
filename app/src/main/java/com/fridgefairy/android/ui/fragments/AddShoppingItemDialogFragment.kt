// This file defines a DialogFragment for adding a new shopping list item.
// It displays a dialog with input fields, supports pre-filling the item name
// (from the scanner), and uses a callback (onItemAdded) to pass the new item back.

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

    companion object {
        private const val ARG_ITEM_NAME = "ARG_ITEM_NAME"

        fun newInstance(itemName: String? = null): AddShoppingItemDialogFragment {
            val fragment = AddShoppingItemDialogFragment()
            val args = Bundle()
            args.putString(ARG_ITEM_NAME, itemName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_shopping_item, null)

        val editTextName = view.findViewById<EditText>(R.id.edit_text_item_name)
        val editTextCategory = view.findViewById<EditText>(R.id.edit_text_category)
        val editTextQuantity = view.findViewById<EditText>(R.id.edit_text_quantity)

        val prefilledName = arguments?.getString(ARG_ITEM_NAME)
        if (prefilledName != null) {
            editTextName.setText(prefilledName)
            editTextName.setSelection(prefilledName.length)
            editTextQuantity.requestFocus()
        }

        builder.setView(view)
            .setTitle(R.string.add_item)
            .setPositiveButton(R.string.add) { dialog, id ->
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
            .setNegativeButton(R.string.cancel) { dialog, id ->
                dialog.dismiss()
            }

        return builder.create()
    }
}