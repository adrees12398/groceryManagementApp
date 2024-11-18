package com.example.grocery

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.grocessarymanagmentapp.databinding.ActivityForgetPasswrdBinding
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityForgetPasswrdBinding
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswrdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.arrowBack.setOnClickListener {
            finish()
        }
        binding.resetbtn.setOnClickListener {
            val Repassword = binding.resetbtn.text.toString()
            firebaseAuth.sendPasswordResetEmail(Repassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "please check your email", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnCompleteListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                }
           }
       }
    }