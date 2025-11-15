package com.fridgefairy.android.data.models

/**
 * Data class for Analytics Dashboard statistics
 */
data class Analytics(
    val moneySaved: Double = 0.0,
    val wasteKg: Double = 0.0,
    val itemsConsumed: Int = 0,
    val savingsThisMonth: Double = 0.0,
    val dailyUsage: List<Float> = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f),
    val categoryBreakdown: Map<String, Float> = emptyMap(),
    val monthlySavings: List<Float> = listOf(0f, 0f, 0f, 0f, 0f, 0f)
)