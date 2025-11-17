// This file defines the ViewModel for the AnalyticsActivity.
// It calculates analytics (e.g., waste reduced, items consumed, category breakdown)
// by fetching data from the FoodRepository and exposes the results as a StateFlow.

package com.fridgefairy.android.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fridgefairy.android.data.FridgeFairyDatabase
import com.fridgefairy.android.data.models.Analytics
import com.fridgefairy.android.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Analytics Dashboard
 * Calculates money saved, waste reduced, and usage statistics
 */
class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = FridgeFairyDatabase.getDatabase(application)
    private val foodDao = database.foodDao()
    // *** NEW: Instantiate the repository ***
    private val foodRepository = FoodRepository(foodDao)

    private val _analytics = MutableStateFlow(Analytics())
    val analytics: StateFlow<Analytics> = _analytics.asStateFlow()

    init {
        loadAnalytics()
    }

    /**
     * Load analytics data from database
     */
    fun loadAnalytics() {
        viewModelScope.launch {
            try {
                // *** REPLACED SAMPLE DATA WITH REAL CALCULATIONS ***

                // Get real data from the user's database
                val allItems = foodRepository.getAllFoodItemsList()
                val expiredItems = foodRepository.getExpired()

                // Calculate "Items Consumed" as "Total Items Currently in Fridge"
                val itemsInFridge = allItems.size

                // Calculate "Waste Reduced" as "Number of Expired Items"
                // Note: We can't calculate "kg" because weight is not tracked.
                // We will display the *count* of expired items instead.
                val wasteItemCount = expiredItems.size.toDouble()

                // Calculate "Category Breakdown"
                val categoryBreakdown = if (allItems.isNotEmpty()) {
                    allItems.groupBy { it.category }
                        .mapValues { (_, items) -> (items.size.toFloat() / allItems.size.toFloat()) * 100f }
                } else {
                    emptyMap()
                }

                // NOTE: MoneySaved, SavingsThisMonth, DailyUsage, and MonthlySavings
                // are left at 0 because the app does not track item prices or
                // daily consumption history. This would require more features.

                val realAnalytics = Analytics(
                    moneySaved = 0.0, // Cannot be calculated
                    wasteKg = wasteItemCount, // Using count of expired items
                    itemsConsumed = itemsInFridge, // Using count of items in fridge
                    savingsThisMonth = 0.0, // Cannot be calculated
                    dailyUsage = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f), // Cannot be calculated
                    categoryBreakdown = categoryBreakdown,
                    monthlySavings = listOf(0f, 0f, 0f, 0f, 0f, 0f) // Cannot be calculated
                )

                _analytics.value = realAnalytics

            } catch (e: Exception) {
                e.printStackTrace()
                // Keep default values on error
            }
        }
    }

    /**
     * Refresh analytics data
     */
    fun refreshData() {
        loadAnalytics()
    }
}