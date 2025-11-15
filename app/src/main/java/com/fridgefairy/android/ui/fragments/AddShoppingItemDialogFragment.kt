// Start of file: AddShoppingItemDialogFragment.kt
// This fragment displays a dialog for adding a new shopping list item.
// It collects user input and passes the new ShoppingListItem to the parent activity via a callback.
package com.fridgefairy.android.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.fridgefairy.android.R
import com.fridgefairy.android.data.entities.ShoppingListItem

class AddShoppingItemDialogFragment : DialogFragment() {

    // Callback for when a shopping item is added
    var onItemAdded: ((ShoppingListItem) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_shopping_item, null)

        val editTextName = view.findViewById<EditText>(R.id.edit_text_item_name)
        val editTextCategory = view.findViewById<EditText>(R.id.edit_text_category)
        val editTextQuantity = view.findViewById<EditText>(R.id.edit_text_quantity)

        builder.setView(view)
            .setTitle(R.string.add_item) // <-- UPDATED (uses "Add Item" string)
            .setPositiveButton(R.string.add) { dialog, id -> // <-- UPDATED
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
            .setNegativeButton(R.string.cancel) { dialog, id -> // <-- UPDATED
                dialog.dismiss()
            }

        return builder.create()
    }
}
// End of file: AddShoppingItemDialogFragment.kt