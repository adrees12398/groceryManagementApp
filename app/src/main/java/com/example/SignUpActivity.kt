package com.example.grocessarymanagmentapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.text.TextUtils
import android.widget.Toast
import com.example.grocessarymanagmentapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var sessionClass: SessionClass
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
       sessionClass = SessionClass(this)
        firebaseAuth = FirebaseAuth.getInstance()
       binding.SignUp.setOnClickListener {

           UploadData()
       }
    }
   private fun  UploadData(){
        val name = binding.name1.text.toString()
        val email = binding.email1.text.toString()
        val mobile = binding.mobile1.text.toString()
       val password = binding.password1.text.toString()
       if (TextUtils.isEmpty(name)){
           showToast("name is required")
       } else if(TextUtils.isEmpty(email)){
           showToast("email is required")
       }
       else if(TextUtils.isEmpty(mobile)){
           showToast("Mobile no is required")
       }
       else if (TextUtils.isEmpty(password)){
           showToast("password is required")
       }
       else if(TextUtils.isEmpty(name) && TextUtils.isEmpty(email) && TextUtils.isEmpty(mobile) && TextUtils.isEmpty(password)) {
           showToast("All filed are required ")
       }
       else{
           if (email.isNotEmpty() && password.isNotEmpty() && mobile.isNotEmpty() && name.isNotEmpty()){
           firebaseAuth.createUserWithEmailAndPassword(email,password)
               .addOnCompleteListener(this) {task ->
                if (task.isSuccessful){
                    startActivity(Intent(this,LoginActivity::class.java))
                    sessionClass.setUser("User")
                    sessionClass.setTheme(true)
                  showToast("Sucessfully Data transfer")
                finish()

                }
               }
               .addOnFailureListener {

               }
           }

       }
       binding.name1.setText("")
       binding.email1.setText("")
       binding.mobile1.setText("")
       binding.password1.setText("")
    }
  private  fun showToast(message: String){
      Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }
}