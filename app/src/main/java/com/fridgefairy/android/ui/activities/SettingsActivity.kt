package com.fridgefairy.android.ui.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.fridgefairy.android.R
import com.fridgefairy.android.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val PREFS_NAME = "FridgeFairyPrefs"
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
        private const val KEY_THEME = "app_theme"
        const val KEY_DIET = "diet_preference"
        const val KEY_INTOLERANCES = "intolerances"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        loadSettings()
        setupListeners()
    }

    private fun loadSettings() {
        val notificationsEnabled = sharedPreferences.getBoolean(KEY_NOTIFICATIONS, true)
        binding.switchNotifications.isChecked = notificationsEnabled

        val currentTheme = sharedPreferences.getInt(KEY_THEME, 2)
        binding.spinnerTheme.setSelection(currentTheme)

        val currentDiet = sharedPreferences.getInt(KEY_DIET, 0)
        binding.spinnerDiet.setSelection(currentDiet)

        // Load intolerances checkboxes
        val intolerances = sharedPreferences.getString(KEY_INTOLERANCES, "") ?: ""
        binding.checkboxGluten.isChecked = intolerances.contains("gluten")
        binding.checkboxDairy.isChecked = intolerances.contains("dairy")
        binding.checkboxEgg.isChecked = intolerances.contains("egg")
        binding.checkboxPeanut.isChecked = intolerances.contains("peanut")
    }

    private fun setupListeners() {
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS, isChecked).apply()
        }

        binding.spinnerTheme.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val currentThemePref = sharedPreferences.getInt(KEY_THEME, 2)

                if (currentThemePref != position) {
                    sharedPreferences.edit().putInt(KEY_THEME, position).apply()
                    applyTheme(position)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerDiet.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                sharedPreferences.edit().putInt(KEY_DIET, position).apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Save intolerances when checkboxes change
        val intoleranceListener = { _: Any, _: Boolean ->
            saveIntolerances()
        }

        binding.checkboxGluten.setOnCheckedChangeListener(intoleranceListener)
        binding.checkboxDairy.setOnCheckedChangeListener(intoleranceListener)
        binding.checkboxEgg.setOnCheckedChangeListener(intoleranceListener)
        binding.checkboxPeanut.setOnCheckedChangeListener(intoleranceListener)
    }

    private fun saveIntolerances() {
        val intolerances = mutableListOf<String>()
        if (binding.checkboxGluten.isChecked) intolerances.add("gluten")
        if (binding.checkboxDairy.isChecked) intolerances.add("dairy")
        if (binding.checkboxEgg.isChecked) intolerances.add("egg")
        if (binding.checkboxPeanut.isChecked) intolerances.add("peanut")

        val intolerancesString = intolerances.joinToString(",")
        sharedPreferences.edit().putString(KEY_INTOLERANCES, intolerancesString).apply()
    }

    private fun applyTheme(themeIndex: Int) {
        val mode = when (themeIndex) {
            0 -> AppCompatDelegate.MODE_NIGHT_NO // Light mode
            1 -> AppCompatDelegate.MODE_NIGHT_YES // Dark mode
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM // System default (position 2)
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun getDietPreference(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
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
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val intolerances = prefs.getString(KEY_INTOLERANCES, "")
        return if (intolerances.isNullOrBlank()) null else intolerances
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}