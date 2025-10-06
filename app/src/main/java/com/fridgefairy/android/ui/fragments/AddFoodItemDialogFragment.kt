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
            .setTitle("Add Food Item")
            .setPositiveButton("Add") { dialog, id ->
                val name = editTextName.text.toString()
                val category = editTextCategory.text.toString()
                val quantity = editTextQuantity.text.toString().toIntOrNull() ?: 1

                if (name.isNotBlank() && category.isNotBlank()) {
                    // Set expiration to 7 days from now by default
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DAY_OF_YEAR, 7)
                    val expirationDate = calendar.timeInMillis

                    // NOTE: userId will be set by MainActivity before inserting
                    val foodItem = FoodItem(
                        userId = "", // Placeholder - will be replaced in MainActivity
                        name = name,
                        category = category,
                        expirationDate = expirationDate,
                        quantity = quantity
                    )

                    onFoodItemAdded?.invoke(foodItem)
                }
            }
            .setNegativeButton("Cancel") { dialog, id ->
                dialog.dismiss()
            }

        return builder.create()
    }
}