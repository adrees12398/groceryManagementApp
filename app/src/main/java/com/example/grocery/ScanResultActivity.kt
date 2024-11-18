package com.example.grocery

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.grocessarymanagmentapp.R
import com.example.grocessarymanagmentapp.databinding.ActivityScanResultBinding

class ScanResultActivity : AppCompatActivity() {
    lateinit var binding: ActivityScanResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanResultBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val scannedResult = intent.getStringExtra("key")

        val resultTextView: TextView = findViewById(R.id.resultTextView)
        resultTextView.text = scannedResult ?: "No result found"

        val returnButton: Button = findViewById(R.id.returnButton)
        returnButton.setOnClickListener {
            finish()
        }
    }
}