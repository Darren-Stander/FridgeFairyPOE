// Start of file: AuthActivity.kt
// activity handles user authentication, including email/password and Google sign-in.
// It uses FirebaseAuth and GoogleSignInClient for authentication and navigates to the main app on success.
package com.fridgefairy.android.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fridgefairy.android.R
import com.fridgefairy.android.databinding.ActivityAuthBinding
import com.fridgefairy.android.utils.BiometricHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("AuthActivity", "Google sign in failed", e)
                Toast.makeText(this, "Google Sign In failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setupBiometricButton()
        setupListeners()
    }

    override fun onStart() {
        super.onStart()
        if (::firebaseAuth.isInitialized && firebaseAuth.currentUser != null) {
            navigateToMain()
        } else {

            validateBiometricData()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh button visibility when activity resumes (e.g., after returning from Settings)
        if (firebaseAuth.currentUser == null) {
            setupBiometricButton()
        }
    }

    private fun validateBiometricData() {

        val savedEmail = BiometricHelper.getSavedUserEmail(this)
        val currentUser = firebaseAuth.currentUser

        if (savedEmail != null && currentUser?.email != null && savedEmail != currentUser.email) {

            Log.d("AuthActivity", "Different user detected: saved=$savedEmail, current=${currentUser.email}")
            Log.d("AuthActivity", "Clearing biometric data for different user")
            BiometricHelper.clearBiometricData(this)

            setupBiometricButton()
        } else if (savedEmail != null && currentUser == null) {

            Log.d("AuthActivity", "User logged out, keeping biometric data for: $savedEmail")
        }
    }

    private fun setupBiometricButton() {

        val biometricAvailable = BiometricHelper.isBiometricAvailable(this)
        val biometricEnabled = BiometricHelper.isBiometricEnabled(this)
        val savedEmail = BiometricHelper.getSavedUserEmail(this)
        val hasToken = BiometricHelper.hasAutoLoginToken(this)

        Log.d("AuthActivity", "Biometric available: $biometricAvailable")
        Log.d("AuthActivity", "Biometric enabled: $biometricEnabled")
        Log.d("AuthActivity", "Saved email: $savedEmail")
        Log.d("AuthActivity", "Has auto-login token: $hasToken")


        if (biometricAvailable && biometricEnabled && !savedEmail.isNullOrEmpty()) {
            binding.buttonBiometricLogin.visibility = android.view.View.VISIBLE
            binding.buttonBiometricLogin.setOnClickListener {
                loginWithBiometric()
            }
            Log.d("AuthActivity", "Biometric button shown (hasToken: $hasToken)")
        } else {
            binding.buttonBiometricLogin.visibility = android.view.View.GONE


            if (!biometricAvailable) {
                Log.d("AuthActivity", "Biometric not available: ${BiometricHelper.getBiometricStatusMessage(this)}")
            } else if (!biometricEnabled) {
                Log.d("AuthActivity", "Biometric not enabled by user")
            } else if (savedEmail.isNullOrEmpty()) {
                Log.d("AuthActivity", "No saved email for biometric login")
            }
        }
    }

    private fun loginWithBiometric() {
        val savedEmail = BiometricHelper.getSavedUserEmail(this)
        val savedToken = BiometricHelper.getAutoLoginToken(this)


        if (savedEmail.isNullOrEmpty()) {
            Log.e("AuthActivity", "loginWithBiometric called but no saved email")
            BiometricHelper.clearBiometricData(this)
            setupBiometricButton()
            Toast.makeText(this, "Biometric login unavailable. Please log in with your credentials first.", Toast.LENGTH_SHORT).show()
            return
        }

        BiometricHelper.showBiometricPrompt(
            activity = this,
            title = "Login to FridgeFairy",
            subtitle = "Use your biometric credential to log in",
            negativeButtonText = "Use password",
            onSuccess = {
                Log.d("AuthActivity", "Biometric authentication successful")
                Toast.makeText(this, "Biometric authentication successful!", Toast.LENGTH_SHORT).show()

                if (!savedToken.isNullOrEmpty()) {
                    // Token exists - perform automatic login
                    Log.d("AuthActivity", "Auto-login token found, logging in automatically")
                    performBiometricLogin(savedEmail, savedToken)
                } else {
                    // No token yet - auto-fill email and let user enter password once
                    Log.d("AuthActivity", "No token yet, auto-filling email for first-time setup")
                    binding.editTextEmail.setText(savedEmail)
                    binding.editTextPassword.requestFocus()
                    Toast.makeText(this, "Please enter your password to complete setup", Toast.LENGTH_LONG).show()
                }
            },
            onError = { errorCode, errorMessage ->
                Log.e("AuthActivity", "Biometric authentication error: $errorCode - $errorMessage")
                Toast.makeText(this, "Authentication error: $errorMessage", Toast.LENGTH_LONG).show()
            },
            onFailed = {
                Toast.makeText(this, "Biometric authentication failed. Try again.", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun performBiometricLogin(email: String, password: String) {
        // Show loading state
        binding.buttonBiometricLogin.isEnabled = false

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("AuthActivity", "Automatic biometric login successful")
                Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
                navigateToMain()
            }
            .addOnFailureListener { e ->
                Log.e("AuthActivity", "Automatic biometric login failed: ${e.message}")
                binding.buttonBiometricLogin.isEnabled = true


                BiometricHelper.clearBiometricData(this)
                setupBiometricButton()

                Toast.makeText(this, "Biometric login failed. Please log in with your credentials.", Toast.LENGTH_LONG).show()
            }
    }

    private fun setupListeners() = with(binding) {

        buttonRegister.setOnClickListener {
            startActivity(Intent(this@AuthActivity, RegisterActivity::class.java))
        }
        buttonLogin.setOnClickListener { loginUser() }
        buttonGoogleSignIn.setOnClickListener { signInWithGoogle() }
    }

    private fun loginUser() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val savedEmail = BiometricHelper.getSavedUserEmail(this)
                val biometricEnabled = BiometricHelper.isBiometricEnabled(this)

                if (biometricEnabled && (savedEmail == null || savedEmail == email)) {

                    BiometricHelper.saveUserEmail(this, email)
                    BiometricHelper.saveAutoLoginToken(this, password)
                    Log.d("AuthActivity", "Saved credentials for biometric auto-login: $email")
                } else if (biometricEnabled && savedEmail != email) {

                    Log.d("AuthActivity", "Different user detected, clearing biometric data")
                    BiometricHelper.clearBiometricData(this)
                }

                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                navigateToMain()
            }
            .addOnFailureListener { e ->
                val msg = when (e) {
                    is com.google.firebase.auth.FirebaseAuthInvalidUserException ->
                        "No account found for this email. Try registering first."
                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ->
                        "Incorrect email or password."
                    else -> e.message ?: "Login failed. Please try again."
                }
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
    }

    private fun signInWithGoogle() {
        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }

    // Handles Firebase authentication with Google account
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val email = firebaseAuth.currentUser?.email
                    val savedEmail = BiometricHelper.getSavedUserEmail(this)
                    val biometricEnabled = BiometricHelper.isBiometricEnabled(this)

                    if (email != null && biometricEnabled && (savedEmail == null || savedEmail == email)) {

                        BiometricHelper.saveUserEmail(this, email)
                        BiometricHelper.saveAutoLoginToken(this, idToken)
                        Log.d("AuthActivity", "Saved Google credentials for biometric auto-login: $email")
                    } else if (email != null && biometricEnabled && savedEmail != email) {

                        Log.d("AuthActivity", "Different user detected, clearing biometric data")
                        BiometricHelper.clearBiometricData(this)
                    }

                    Toast.makeText(this, "Google Sign In successful!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}
// End of file: AuthActivity.kt
