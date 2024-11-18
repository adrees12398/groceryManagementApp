package com.example.grocessarymanagmentapp

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.grocery.SessionClass
import com.example.grocery.LoginActivity
import com.example.grocery.ScanResultActivity
import com.example.grocessarymanagmentapp.databinding.FragmentSettingBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class Setting : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var sessionClass: SessionClass

    // Initialize the barcode launcher at the class level
    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(requireActivity(), "Cancelled", Toast.LENGTH_LONG).show()
        } else {
            result.contents?.let {
                // Pass the scanned barcode to a new activity
                val intent = Intent(requireActivity(), ScanResultActivity::class.java)
                intent.putExtra("key", it)
                requireActivity().startActivity(intent)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        sessionClass = SessionClass(this.requireContext())
        sessionClass.getvalue().let {
            binding.switcher.isChecked = it
        }
        sessionClass.gettext().let {
            binding.textscanner4.text = it
        }

        binding.switcher.setOnCheckedChangeListener { compoundButton, ischecked ->
            binding.textscanner4.setText("Switch on")
            if (ischecked) {

                binding.textscanner4.setText("Switch On")
                sessionClass.settext("Switch On")
                sessionClass.setvalue(ischecked)
            } else {

                binding.textscanner4.text = "Switch Off"
                sessionClass.settext("Switch Off")
                sessionClass.setvalue(ischecked)
            }

        }
        firebaseAuth = FirebaseAuth.getInstance()
        // Set an onClickListener for the barcode button
        binding.material1.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Launch the barcode scanner
                val options = ScanOptions()
                options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
                options.setPrompt("Scan a barcode")
                options.setCameraId(0)
                options.setBeepEnabled(true)
                options.setBarcodeImageEnabled(true)
                options.setOrientationLocked(true)
                barcodeLauncher.launch(options)
            } else {
                // Request camera permission if not granted
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            }
        }
        binding.material2.setOnClickListener {
            val url = "https://grocerapp.pk/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        binding.material3.setOnClickListener {
            Alertdailge()
        }
        return binding.root
    }

    // Handle camera permission result
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, launch barcode scanner
                val options = ScanOptions()
                options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
                options.setPrompt("Scan a barcode")
                options.setCameraId(0)
                options.setBeepEnabled(true)
                options.setBarcodeImageEnabled(true)
                options.setOrientationLocked(true)
                barcodeLauncher.launch(options)
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(requireActivity(), "Camera permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    private fun Alertdailge(){
        val dailog = AlertDialog.Builder(this.requireContext(), R.style.TransparentAlertDialog)
        val infalte = layoutInflater
        val view = infalte.inflate(R.layout.deletealertdailog,null)
        dailog.setView(view)
        val Button1:MaterialButton = view.findViewById(R.id.Cancel)
        val Button:MaterialButton = view.findViewById(R.id.btnYes)

        val alertdailog = dailog.create()
         Button1.setOnClickListener {
             alertdailog.dismiss()
         }
        Button.setOnClickListener {
                firebaseAuth.signOut()
                sessionClass.setTheme(false)
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                Toast.makeText(requireContext(), "Logout", Toast.LENGTH_SHORT).show()
                alertdailog.dismiss()
            this@Setting.activity?.finish()
        }
     alertdailog.show()
    }
}
