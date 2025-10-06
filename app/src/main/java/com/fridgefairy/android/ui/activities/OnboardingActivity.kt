package com.fridgefairy.android.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.fridgefairy.android.R

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("FridgeFairyPrefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("onboarding_complete", false)) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_onboarding)

        val btn = findViewById<MaterialButton>(R.id.btnGetStarted)
        btn.setOnClickListener {
            prefs.edit().putBoolean("onboarding_complete", true).apply()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }
}