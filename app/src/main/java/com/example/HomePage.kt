package com.example.grocessarymanagmentapp


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.grocessarymanagmentapp.databinding.ActivityHomePageBinding


class HomePage : AppCompatActivity() {
    lateinit var binding: ActivityHomePageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}

