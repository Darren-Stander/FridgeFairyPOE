// Start of file: RegisterActivity.kt
// This activity allows the user to register a new account using email and password.
// It validates input, creates a new user with FirebaseAuth, and navigates to the main screen on success.
package com.fridgefairy.android.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fridgefairy.android.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        binding.buttonBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        binding.buttonCreateAccount.setOnClickListener { doRegister() }

        binding.textGoToLogin.setOnClickListener { finish() }
    }


    private fun doRegister() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString()
        val confirm = binding.editTextConfirmPassword.text.toString()

        when {
            email.isEmpty() -> { toast("Please enter your email."); return }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> { toast("Enter a valid email."); return }
            password.length < 6 -> { toast("Password must be at least 6 characters."); return }
            password != confirm -> { toast("Passwords do not match."); return }
            !password.any { it.isLetter() } || !password.any { it.isDigit() } -> {
                toast("Use letters and numbers in your password."); return
            }
        }

        binding.buttonCreateAccount.isEnabled = false

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                toast("Registration successful!")

                val i = Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(i)
                finish()
            }
            .addOnFailureListener { e ->
                binding.buttonCreateAccount.isEnabled = true
                val msg = when (e) {
                    is com.google.firebase.auth.FirebaseAuthWeakPasswordException ->
                        "Your password is too weak."
                    is com.google.firebase.auth.FirebaseAuthUserCollisionException ->
                        "Email already in use. Try logging in."
                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ->
                        "Invalid email."
                    else -> e.message ?: "Registration failed. Please try again."
                }
                toast(msg)
            }
    }


    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
// End of file: RegisterActivity.kt
