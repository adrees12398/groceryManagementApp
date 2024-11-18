package com.example.grocery

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.grocessarymanagmentapp.databinding.ActivitySignUpBinding
import com.example.models.namemodel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    lateinit var firestore: FirebaseFirestore
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var sessionClass: SessionClass
    lateinit var listname : ArrayList<namemodel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionClass = SessionClass(this)
        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        binding.SignUp.setOnClickListener {

            UploadData()
        }
    }

    private fun UploadData() {
        val name = binding.name1.text.toString()
        val email = binding.email1.text.toString()
        val mobile = binding.mobile1.text.toString()
        val password = binding.password1.text.toString()
        if (TextUtils.isEmpty(name)) {
            showToast("name is required")
        } else if (TextUtils.isEmpty(email)) {
            showToast("email is required")
        } else if (TextUtils.isEmpty(mobile)) {
            showToast("Mobile no is required")
        } else if (TextUtils.isEmpty(password)) {
            showToast("password is required")
        } else if (TextUtils.isEmpty(name) && TextUtils.isEmpty(email) && TextUtils.isEmpty(mobile) && TextUtils.isEmpty(
                password
            )
        ) {
            showToast("All filed are required ")
        } else {
            if (email.isNotEmpty() && password.isNotEmpty() && mobile.isNotEmpty() && name.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val model = namemodel(name = name)
                            firestore.collection("User").add(model).addOnCompleteListener {
                                    if(it.isSuccessful){
                                        sessionClass.setName(name)
                                        sessionClass.setUId(task.result.user!!.uid)
                                        sessionClass.setUser("User")
                                        sessionClass.setTheme(true)
                                        showToast("Sucessfully Data transfer")
                                        Toast.makeText(this, "NameSaved", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this, HandlerActivity::class.java))
                                        finish()
                                    }
                                }.addOnFailureListener {e->
                                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                            }
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}