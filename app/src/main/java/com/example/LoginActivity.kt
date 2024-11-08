package com.example.grocessarymanagmentapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.grocessarymanagmentapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var sessionClass: SessionClass
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)
        sessionClass = SessionClass(this)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.createaccount.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        }
        binding.forgetpassword.setOnClickListener {
            startActivity(Intent(this,ForgetPasswordActivity::class.java))
            finish()
        }
        binding.loginbtn.setOnClickListener {
            match()
        }
    }
   private fun match(){
        val email = binding.emaillogin.text.toString()
        val password = binding.passwordlogin.text.toString()
        if (TextUtils.isEmpty(email)){
            showToast("name is required")
        }
        else if (TextUtils.isEmpty(password)){
            showToast("password is required")
        }
        else if (TextUtils.isEmpty(email)&& TextUtils.isEmpty(password)){
            showToast("Both field is required")
        }
        else{
            if (email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener {task->
                        if (task.isSuccessful){
                            startActivity(Intent(this,HandlerActivity::class.java))
                            sessionClass.setUser("User")
                            sessionClass.setTheme(true)
                            showToast("Login Successfully")
                          finish()
                        }
                    }
                    .addOnFailureListener {
                       showToast("Fail")
                    }
            }
        }
        binding.emaillogin.setText("")
        binding.passwordlogin.setText("")
    }
    fun showToast(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}