// Start of file: MainActivity.kt
// This activity displays the main fridge view, showing all food items for the user.
// It uses a RecyclerView and FoodItemAdapter, and interacts with FridgeViewModel for data operations.
package com.fridgefairy.android.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fridgefairy.android.R
import com.fridgefairy.android.data.FridgeFairyDatabase
import com.fridgefairy.android.data.entities.FoodItem
import com.fridgefairy.android.data.repository.FoodRepository
import com.fridgefairy.android.databinding.ActivityMainBinding
import com.fridgefairy.android.ui.adapters.FoodItemAdapter
import com.fridgefairy.android.ui.fragments.AddFoodItemDialogFragment
import com.fridgefairy.android.ui.viewmodels.FridgeViewModel
import com.fridgefairy.android.ui.viewmodels.FridgeViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: FoodItemAdapter
    private lateinit var auth: FirebaseAuth
    private var currentUserId: String? = null

    // ViewModel for fridge data
    private val viewModel: FridgeViewModel by viewModels {
        FridgeViewModelFactory(
            FoodRepository(
                FridgeFairyDatabase.getDatabase(this).foodDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        auth = FirebaseAuth.getInstance()

        // Check if user is logged in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        // Set the current user ID
        currentUserId = currentUser.uid
        viewModel.setUserId(currentUserId!!)

        setupRecyclerView()
        setupClickListeners()
        observeData()
        setupSwipeToDelete()
    }

    private fun setupRecyclerView() {
        adapter = FoodItemAdapter { foodItem ->
            showFoodItemDetailsDialog(foodItem)
        }

        binding.content.recyclerViewFoodItems.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupClickListeners() {
        binding.fabAddFoodItem.setOnClickListener {
            showAddFoodItemDialog()
        }
    }

    private fun observeData() {
        viewModel.allFoodItems.observe(this) { foodItems ->
            if (foodItems.isEmpty()) {
                binding.content.recyclerViewFoodItems.visibility = View.GONE
                binding.content.textEmptyState?.visibility = View.VISIBLE
            } else {
                binding.content.recyclerViewFoodItems.visibility = View.VISIBLE
                binding.content.textEmptyState?.visibility = View.GONE
                adapter.submitList(foodItems)
            }
        }
    }

    private fun showAddFoodItemDialog() {
        val dialog = AddFoodItemDialogFragment().apply {
            onFoodItemAdded = { foodItem ->
                // Add userId to the food item
                val foodItemWithUser = foodItem.copy(userId = currentUserId!!)
                viewModel.insert(foodItemWithUser)
                Snackbar.make(
                    binding.root,
                    "${foodItem.name} added to fridge",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        dialog.show(supportFragmentManager, "AddFoodItemDialog")
    }

    private fun showFoodItemDetailsDialog(foodItem: FoodItem) {
        val daysUntil = foodItem.daysUntilExpiration
        val expiryStatus = when {
            foodItem.isExpired -> "EXPIRED"
            foodItem.isExpiringSoon -> "Expiring in $daysUntil days"
            else -> "Fresh ($daysUntil days left)"
        }

        AlertDialog.Builder(this)
            .setTitle(foodItem.name)
            .setMessage(
                """
                Category: ${foodItem.category}
                Quantity: ${foodItem.quantity}
                Storage: ${foodItem.storageLocation}
                Status: $expiryStatus
            """.trimIndent()
            )
            .setPositiveButton("OK", null)
            .setNegativeButton("Delete") { _, _ ->
                viewModel.delete(foodItem)
                Snackbar.make(binding.root, "${foodItem.name} deleted", Snackbar.LENGTH_SHORT)
                    .show()
            }
            .show()
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val foodItem = adapter.currentList[position]

                viewModel.delete(foodItem)

                Snackbar.make(binding.root, "${foodItem.name} deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        viewModel.insert(foodItem)
                    }
                    .show()
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.content.recyclerViewFoodItems)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }

            R.id.action_recipe_search -> {
                startActivity(Intent(this, RecipeSearchActivity::class.java))
                true
            }

            R.id.action_find_recipes_by_ingredients -> {
                startActivity(Intent(this, RecipesByIngredientsActivity::class.java))
                true
            }

            R.id.action_shopping_list -> {
                startActivity(Intent(this, ShoppingListActivity::class.java))
                true
            }

            R.id.action_logout -> {
                auth.signOut()
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
// End of file: MainActivity.kt
