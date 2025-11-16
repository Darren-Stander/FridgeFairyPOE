package com.fridgefairy.android.ui.activities

import android.content.Context
import android.content.Intent
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
import com.fridgefairy.android.utils.SettingsHelper
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mAuth: FirebaseAuth

    companion object {
        private const val PREFS_NAME = "FridgeFairyPrefs"
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
        private const val KEY_THEME = "app_theme"
        private const val KEY_LANGUAGE = "app_language"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

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

        val currentDiet = sharedPreferences.getInt(SettingsHelper.KEY_DIET, 0)
        binding.spinnerDiet.setSelection(currentDiet)

        val intolerances = sharedPreferences.getString(SettingsHelper.KEY_INTOLERANCES, "") ?: ""
        binding.checkboxGluten.isChecked = intolerances.contains("gluten")
        binding.checkboxDairy.isChecked = intolerances.contains("dairy")
        binding.checkboxEgg.isChecked = intolerances.contains("egg")
        binding.checkboxPeanut.isChecked = intolerances.contains("peanut")

        binding.switchBiometric.isChecked = BiometricHelper.isBiometricEnabled(this)

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
                sharedPreferences.edit().putInt(SettingsHelper.KEY_DIET, position).apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val intoleranceListener = { _: Any, _: Boolean ->
            saveIntolerances()
        }

        binding.checkboxGluten.setOnCheckedChangeListener(intoleranceListener)
        binding.checkboxDairy.setOnCheckedChangeListener(intoleranceListener)
        binding.checkboxEgg.setOnCheckedChangeListener(intoleranceListener)
        binding.checkboxPeanut.setOnCheckedChangeListener(intoleranceListener)

        binding.switchBiometric.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (BiometricHelper.isBiometricAvailable(this)) {
                    val email = mAuth.currentUser?.email
                    if (email != null) {
                        BiometricHelper.saveUserEmail(this, email)
                        BiometricHelper.setBiometricEnabled(this, true)
                        android.util.Log.d("SettingsActivity", "Biometric ENABLED - Email saved: $email")
                        android.util.Log.d("SettingsActivity", "Verify - Enabled: ${BiometricHelper.isBiometricEnabled(this)}, Email: ${BiometricHelper.getSavedUserEmail(this)}")
                        Toast.makeText(this, "Biometric login enabled! You can use it when you log in next time.", Toast.LENGTH_LONG).show()
                    } else {
                        binding.switchBiometric.isChecked = false
                        Toast.makeText(this, "Error: User email not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    binding.switchBiometric.isChecked = false
                    Toast.makeText(this, BiometricHelper.getBiometricStatusMessage(this), Toast.LENGTH_LONG).show()
                }
            } else {
                // Just disable the flag, keep email and token so user can re-enable easily
                BiometricHelper.setBiometricEnabled(this, false)
                android.util.Log.d("SettingsActivity", "Biometric DISABLED - Flag set to false")
                Toast.makeText(this, "Biometric login disabled", Toast.LENGTH_SHORT).show()
            }
        }

        // --- SIGN OUT BUTTON LISTENER ---
        binding.buttonLogout.setOnClickListener {
            mAuth.signOut()
            // DO NOT clear biometric data - user should be able to use biometric to login again!
            // Biometric data will only be cleared if a DIFFERENT user logs in
            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
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