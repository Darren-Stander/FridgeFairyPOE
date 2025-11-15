// Start of file: SettingsActivity.kt
// This activity allows the user to change app settings such as notifications, theme, diet, and intolerances.
// It loads settings from shared preferences and updates them based on user interaction.
package com.fridgefairy.android.ui.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.fridgefairy.android.databinding.ActivitySettingsBinding
import com.fridgefairy.android.utils.BiometricHelper
// *** NEW: Import the helper ***
import com.fridgefairy.android.utils.SettingsHelper

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val PREFS_NAME = "FridgeFairyPrefs"
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
        private const val KEY_THEME = "app_theme"
        // *** MOVED to SettingsHelper ***
        // const val KEY_DIET = "diet_preference"
        // const val KEY_INTOLERANCES = "intolerances"
        private const val KEY_LANGUAGE = "app_language"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        loadSettings()
        setupListeners()
    }

    // Loads settings from shared preferences and updates UI
    private fun loadSettings() {
        val notificationsEnabled = sharedPreferences.getBoolean(KEY_NOTIFICATIONS, true)
        binding.switchNotifications.isChecked = notificationsEnabled

        val currentTheme = sharedPreferences.getInt(KEY_THEME, 2) // 2 = System Default
        binding.spinnerTheme.setSelection(currentTheme)

        val languageValue = sharedPreferences.all[KEY_LANGUAGE]
        var currentLanguageCode = "system" // Default

        if (languageValue is String) {
            currentLanguageCode = languageValue
        } else if (languageValue is Int) {
            currentLanguageCode = when (languageValue) {
                1 -> "en"
                2 -> "af"
                3 -> "zu"
                else -> "system"
            }
            sharedPreferences.edit().putString(KEY_LANGUAGE, currentLanguageCode).apply()
        }

        val languagePosition = when (currentLanguageCode) {
            "en" -> 1
            "af" -> 2
            "zu" -> 3
            else -> 0 // "system"
        }
        binding.spinnerLanguage.setSelection(languagePosition)

        // *** MODIFIED: Use the key from SettingsHelper ***
        val currentDiet = sharedPreferences.getInt(SettingsHelper.KEY_DIET, 0)
        binding.spinnerDiet.setSelection(currentDiet)

        // Load intolerances checkboxes
        // *** MODIFIED: Use the key from SettingsHelper ***
        val intolerances = sharedPreferences.getString(SettingsHelper.KEY_INTOLERANCES, "") ?: ""
        binding.checkboxGluten.isChecked = intolerances.contains("gluten")
        binding.checkboxDairy.isChecked = intolerances.contains("dairy")
        binding.checkboxEgg.isChecked = intolerances.contains("egg")
        binding.checkboxPeanut.isChecked = intolerances.contains("peanut")

        // Load biometric setting
        binding.switchBiometric.isChecked = BiometricHelper.isBiometricEnabled(this)

        // Disable biometric switch if not available
        if (!BiometricHelper.isBiometricAvailable(this)) {
            binding.switchBiometric.isEnabled = false
            binding.textBiometricStatus.text = BiometricHelper.getBiometricStatusMessage(this)
        } else {
            binding.textBiometricStatus.text = "Use fingerprint or face unlock to login quickly"
        }
    }

    // Sets up listeners for user interactions
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

        // Language Spinner Listener
        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val languageCode = when (position) {
                    1 -> "en" // English
                    2 -> "af" // Afrikaans
                    3 -> "zu" // isiZulu
                    else -> "system" // System Default
                }

                val currentLangPref = sharedPreferences.getString(KEY_LANGUAGE, "system")
                if (currentLangPref != languageCode) {
                    sharedPreferences.edit().putString(KEY_LANGUAGE, languageCode).apply()
                    applyLanguage(languageCode)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerDiet.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                // *** MODIFIED: Use the key from SettingsHelper ***
                sharedPreferences.edit().putInt(SettingsHelper.KEY_DIET, position).apply()
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

        // Biometric toggle listener
        binding.switchBiometric.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (BiometricHelper.isBiometricAvailable(this)) {
                    BiometricHelper.setBiometricEnabled(this, true)
                    Toast.makeText(this, "Biometric login enabled", Toast.LENGTH_SHORT).show()
                } else {
                    binding.switchBiometric.isChecked = false
                    Toast.makeText(this, BiometricHelper.getBiometricStatusMessage(this), Toast.LENGTH_LONG).show()
                }
            } else {
                BiometricHelper.setBiometricEnabled(this, false)
                BiometricHelper.clearBiometricData(this)
                Toast.makeText(this, "Biometric login disabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Saves selected intolerances to shared preferences
    private fun saveIntolerances() {
        val intolerances = mutableListOf<String>()
        if (binding.checkboxGluten.isChecked) intolerances.add("gluten")
        if (binding.checkboxDairy.isChecked) intolerances.add("dairy")
        if (binding.checkboxEgg.isChecked) intolerances.add("egg")
        if (binding.checkboxPeanut.isChecked) intolerances.add("peanut")

        val intolerancesString = intolerances.joinToString(",")
        // *** MODIFIED: Use the key from SettingsHelper ***
        sharedPreferences.edit().putString(SettingsHelper.KEY_INTOLERANCES, intolerancesString).apply()
    }

    // Applies the selected theme
    private fun applyTheme(themeIndex: Int) {
        val mode = when (themeIndex) {
            0 -> AppCompatDelegate.MODE_NIGHT_NO // Light mode
            1 -> AppCompatDelegate.MODE_NIGHT_YES // Dark mode
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM // System default (position 2)
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    // Apply Language Change
    private fun applyLanguage(languageCode: String) {
        val localeList = if (languageCode == "system") {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(languageCode)
        }
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    // *** REMOVED getDietPreference and getIntolerances functions ***
    // (They are now in SettingsHelper.kt)

    // Handles menu item selections
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
// End of file: SettingsActivity.kt