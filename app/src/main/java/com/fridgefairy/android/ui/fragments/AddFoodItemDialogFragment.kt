// Start of file: AddFoodItemDialogFragment.kt
// This fragment displays a dialog for adding a new food item to the fridge.
// It collects user input and passes the new FoodItem to the parent activity via a callback.
package com.fridgefairy.android.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.fridgefairy.android.R
import com.fridgefairy.android.data.entities.FoodItem
import java.util.Calendar

class AddFoodItemDialogFragment : DialogFragment() {


    var onFoodItemAdded: ((FoodItem) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_food, null)

        val editTextName = view.findViewById<EditText>(R.id.edit_text_food_name)
        val editTextCategory = view.findViewById<EditText>(R.id.edit_text_category)
        val editTextQuantity = view.findViewById<EditText>(R.id.edit_text_quantity)

        builder.setView(view)
            .setTitle(R.string.add_food_item)
            .setPositiveButton(R.string.add) { dialog, id ->
                val name = editTextName.text.toString()
                val category = editTextCategory.text.toString()
                val quantity = editTextQuantity.text.toString().toIntOrNull() ?: 1

                if (name.isNotBlank() && category.isNotBlank()) {

                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DAY_OF_YEAR, 7)
                    val expirationDate = calendar.timeInMillis


                    val foodItem = FoodItem(
                        userId = "",
                        name = name,
                        category = category,
                        expirationDate = expirationDate,
                        quantity = quantity
                    )

                    onFoodItemAdded?.invoke(foodItem)
                }
            }
            .setNegativeButton(R.string.cancel) { dialog, id ->
                dialog.dismiss()
            }

        return builder.create()
    }
}
// End of file: AddFoodItemDialogFragment.kt