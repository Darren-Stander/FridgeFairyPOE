

// This file provides a helper object for retrieving user settings from SharedPreferences.
// It offers simple functions to get the currently selected diet and intolerance
// preferences for use in API calls.

package com.fridgefairy.android.utils

import android.content.Context
import android.content.SharedPreferences

object SettingsHelper {

    private const val PREFS_NAME = "FridgeFairyPrefs"


    const val KEY_DIET = "diet_preference"
    const val KEY_INTOLERANCES = "intolerances"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }


    fun getDietPreference(context: Context): String? {
        val prefs = getSharedPreferences(context)
        val dietIndex = prefs.getInt(KEY_DIET, 0)
        return when (dietIndex) {
            1 -> "vegetarian"
            2 -> "vegan"
            3 -> "ketogenic"
            4 -> "paleo"
            else -> null
        }
    }


    fun getIntolerances(context: Context): String? {
        val prefs = getSharedPreferences(context)
        val intolerances = prefs.getString(KEY_INTOLERANCES, "")
        return if (intolerances.isNullOrBlank()) null else intolerances
    }
}