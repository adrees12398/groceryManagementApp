package com.example.grocery

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.grocessarymanagmentapp.databinding.FragmentHelpLineBinding

class HelpLineFragment : Fragment() {
    lateinit var binding: FragmentHelpLineBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHelpLineBinding.inflate(layoutInflater, container, false)
        binding.whatsapplogo.setOnClickListener {
            val phoneNumberWithCountryCode = "+923184159975"
            val message = ""
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        String.format(
                            "https://api.whatsapp.com/send?phone=%s&text=%s",
                            phoneNumberWithCountryCode,
                            message
                        )
                    )
                )
            )
        }
        binding.facebooklogo.setOnClickListener {
            val url = "https://www.facebook.com/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        binding.tweeterlogo.setOnClickListener {
            val url = "https://twitter.com/?lang=en"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        return binding.root
    }


}