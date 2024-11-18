package com.example.grocery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavHost
import androidx.navigation.ui.setupWithNavController
import com.example.grocessarymanagmentapp.R
import com.example.grocessarymanagmentapp.databinding.ActivityHandlerBinding

class HandlerActivity : AppCompatActivity() {

    lateinit var binding: ActivityHandlerBinding

    private val navController by lazy {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHost
        navHost.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHandlerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavigation()
    }

    private fun bottomNavigation() {
        binding.nav.setupWithNavController(navController)
    }
}