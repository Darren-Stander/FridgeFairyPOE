// This file defines the ShoppingListActivity, which displays the user's shopping list.
// It observes the ShoppingListViewModel to show items, and handles adding,
// deleting, checking items, and launching the receipt scanner.

package com.fridgefairy.android.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fridgefairy.android.data.FridgeFairyDatabase
import com.fridgefairy.android.data.entities.ShoppingListItem
import com.fridgefairy.android.data.repository.ShoppingListRepository
import com.fridgefairy.android.databinding.ActivityShoppingListBinding
import com.fridgefairy.android.ui.adapters.ShoppingListAdapter
import com.fridgefairy.android.ui.fragments.AddShoppingItemDialogFragment
import com.fridgefairy.android.ui.viewmodels.ShoppingListViewModel
import com.fridgefairy.android.ui.viewmodels.ShoppingListViewModelFactory
import com.google.android.material.snackbar.Snackbar

class ShoppingListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShoppingListBinding
    private lateinit var adapter: ShoppingListAdapter

    private val viewModel: ShoppingListViewModel by viewModels {
        ShoppingListViewModelFactory(
            ShoppingListRepository(
                FridgeFairyDatabase.getDatabase(this).shoppingListDao()
            )
        )
    }

    private val scannerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scannedText = result.data?.getStringExtra("SCANNED_TEXT")
            if (!scannedText.isNullOrBlank()) {
                showAddItemDialog(scannedText)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Shopping List"

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupRecyclerView()
        setupClickListeners()
        observeData()
        setupSwipeToDelete()
    }

    private fun setupRecyclerView() {
        adapter = ShoppingListAdapter(
            onItemClick = { item -> showItemDetailsDialog(item) },
            onCheckChanged = { item -> viewModel.togglePurchased(item) }
        )

        binding.recyclerViewShoppingList.apply {
            layoutManager = LinearLayoutManager(this@ShoppingListActivity)
            adapter = this@ShoppingListActivity.adapter
        }
    }

    private fun setupClickListeners() {
        binding.fabAddItem.setOnClickListener {
            showAddItemDialog()
        }

        binding.buttonClearPurchased.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Clear Purchased Items")
                .setMessage("Remove all checked items from the list?")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.clearPurchasedItems()
                    Snackbar.make(binding.root, "Purchased items cleared", Snackbar.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.buttonOpenScanner.setOnClickListener {
            val intent = Intent(this, ReceiptScannerActivity::class.java)
            scannerLauncher.launch(intent)
        }
    }

    private fun observeData() {
        viewModel.allItems.observe(this) { items ->
            if (items.isEmpty()) {
                binding.recyclerViewShoppingList.visibility = View.GONE
                binding.textEmptyState.visibility = View.VISIBLE
            } else {
                binding.recyclerViewShoppingList.visibility = View.VISIBLE
                binding.textEmptyState.visibility = View.GONE
                adapter.submitList(items)
            }
        }
    }

    private fun showAddItemDialog(prefilledName: String? = null) {
        val dialog = AddShoppingItemDialogFragment.newInstance(prefilledName).apply {
            onItemAdded = { item ->
                viewModel.insert(item)
                Snackbar.make(binding.root, "${item.name} added to list", Snackbar.LENGTH_SHORT).show()
            }
        }
        dialog.show(supportFragmentManager, "AddShoppingItemDialog")
    }

    private fun showItemDetailsDialog(item: ShoppingListItem) {
        val status = if (item.isPurchased) "Purchased" else "Not purchased"

        AlertDialog.Builder(this)
            .setTitle(item.name)
            .setMessage(
                """
                Quantity: ${item.quantity}
                Category: ${item.category}
                Status: $status
                """.trimIndent()
            )
            .setPositiveButton("OK", null)
            .setNegativeButton("Delete") { _, _ ->
                viewModel.delete(item)
                Snackbar.make(binding.root, "${item.name} deleted", Snackbar.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = adapter.currentList[position]
                viewModel.delete(item)

                Snackbar.make(binding.root, "${item.name} deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") { viewModel.insert(item) }
                    .show()
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.recyclerViewShoppingList)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}