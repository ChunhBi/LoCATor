package com.android.locator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val auth = FirebaseAuth.getInstance()

// Email address of the user who wants to reset their password
        val emailAddress = "ydhe@bu.edu"

// Send the password reset email
        auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Password reset email sent successfully
                    // You can handle the success case here
                    println("Password reset email sent successfully")
                    Log.d("EMAIL","success")
                } else {
                    // Password reset email sending failed
                    // You can handle the failure case here
                    val exception = task.exception
                    println("Password reset email sending failed: ${exception?.message}")
                    Log.d("EMAIL","fail")
                }
            }

    }
}