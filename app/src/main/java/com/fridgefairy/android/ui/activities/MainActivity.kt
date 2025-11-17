// defines the MainActivity, the main screen of the app after login.
// displays the user's list of food items from the fridge, handles adding/deleting items,
// initialides the background data sync worker.

package com.fridgefairy.android.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.fridgefairy.android.utils.BiometricHelper
import com.fridgefairy.android.workers.SyncWorker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: FoodItemAdapter
    private lateinit var auth: FirebaseAuth
    private var currentUserId: String? = null

    private val viewModel: FridgeViewModel by viewModels {
        FridgeViewModelFactory(
            FoodRepository(
                FridgeFairyDatabase.getDatabase(this).foodDao()
            )
        )
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted.")
        } else {
            Log.w("MainActivity", "Notification permission denied.")
            // You could show a snackbar here explaining why notifications are useful
            Snackbar.make(
                binding.root,
                "Please enable notifications in settings to get expiry alerts.",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        auth = FirebaseAuth.getInstance()


        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        currentUserId = currentUser.uid
        viewModel.setUserId(currentUserId!!)


        initializeWorkManager()

        setupRecyclerView()
        setupClickListeners()
        observeData()
        setupSwipeToDelete()


        askForNotificationPermission()
        handleNotificationIntent(intent)
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        setIntent(intent)
        handleNotificationIntent(intent)
    }


    private fun initializeWorkManager() {
        SyncWorker.scheduleSync(this)
        Snackbar.make(
            binding.root,
            getString(R.string.data_will_sync),
            Snackbar.LENGTH_SHORT
        ).show()
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
                val foodItemWithUser = foodItem.copy(userId = currentUserId!!)
                viewModel.insert(foodItemWithUser)
                Snackbar.make(
                    binding.root,
                    "${foodItem.name} added to fridge",
                    Snackbar.LENGTH_SHORT
                ).show()


                SyncWorker.syncNow(this@MainActivity)
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


    private fun askForNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {

                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


    private fun handleNotificationIntent(intent: Intent?) {
        if (intent?.action == "ACTION_CONSUME_ITEM") {
            val itemId = intent.getStringExtra("itemId")
            if (itemId != null) {

                viewModel.allFoodItems.observe(this) { foodItems ->
                    if (foodItems.isNullOrEmpty()) return@observe

                    val itemToConsume = foodItems.find { it.id == itemId }

                    if (itemToConsume != null) {

                        viewModel.delete(itemToConsume)
                        Snackbar.make(binding.root, "${itemToConsume.name} marked as used.", Snackbar.LENGTH_LONG).show()


                        getIntent().action = ""
                    } else {
                        Log.w("MainActivity", "Could not find item $itemId to consume.")
                    }
                }
            }
        }
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
            R.id.action_analytics -> {

                startActivity(Intent(this, AnalyticsActivity::class.java))
                true
            }
            R.id.action_refresh -> {

                SyncWorker.syncNow(this)
                Snackbar.make(
                    binding.root,
                    getString(R.string.syncing),
                    Snackbar.LENGTH_SHORT
                ).show()
                true
            }
            R.id.action_logout -> {
                BiometricHelper.clearBiometricData(this)
                SyncWorker.cancelSync(this)
                auth.signOut()
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}