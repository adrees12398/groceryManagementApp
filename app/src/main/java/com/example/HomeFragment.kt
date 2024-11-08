package com.example

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.grocessarymanagmentapp.Adapters.SuggestionRecipe
import com.example.grocessarymanagmentapp.Adapters.categoreyAdapter
import com.example.grocessarymanagmentapp.R
import com.example.grocessarymanagmentapp.ScanResultActivity
import com.example.grocessarymanagmentapp.databinding.FragmentHomeBinding
import com.example.grocessarymanagmentapp.models.RecipeModel
import com.example.grocessarymanagmentapp.models.categoryModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.util.UUID


class HomeFragment : Fragment() {

    //    private val liveData:MutableLiveData<ArrayList<categoryModel>> = MutableLiveData()
    private val liveData: MutableLiveData<ArrayList<categoryModel>> = MutableLiveData()
    lateinit var binding: FragmentHomeBinding
    private lateinit var storageRef: StorageReference
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: categoreyAdapter
    private lateinit var adapter1: SuggestionRecipe
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var image: Bitmap? = null
    private var imageUri: Uri? = null
    private var TAG = "Hello"
    lateinit var inputtext: EditText
    lateinit var quantityname: EditText
    lateinit var inputtext2: EditText
    lateinit var fulltextlit: String
    lateinit var fulltextkg: String
    private var code = 200
    private var radioButton: RadioButton? = null
    private var selectedImage: ImageView? = null
    private var selectedImage1: ImageView? = null
    private var seletedImagefromcameraupdate: Image? = null
    private val list = ArrayList<categoryModel>()
    private var whichChecked: String? = null
    private lateinit var fulltext: String
    private var imagescan: ImageView? = null

    var list2 = ArrayList<RecipeModel>()
    val recipenames = ArrayList<RecipeModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        firestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        storageRef = firebaseStorage.reference.child("Images")


        binding.recyclerView2.setHasFixedSize(true)
        binding.recyclerView2.layoutManager =
            GridLayoutManager(requireContext(), 2, RecyclerView.HORIZONTAL, false)
        adapter1 = SuggestionRecipe(requireContext(), list2)

        binding.recyclerView2.adapter = adapter1
        loadRecipeNameOnFireStore()


        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        adapter = categoreyAdapter(requireContext(), list) { position, dial ->
            if (dial.equals("Scan", true)) {
                list[position].quantity?.let { barcodealertdailog(it) }
            } else if (dial.equals("update", true)) {
                Toast.makeText(requireContext(), "update", Toast.LENGTH_SHORT).show()
                dailgueUpdate(list[position].Id)

            }
        }
        binding.recyclerView.adapter = adapter

        binding.gallery.setOnClickListener {
            alertDailoggallery()
        }
        binding.camera1.setOnClickListener {
            AlertDialog()
        }
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data ?: return@registerForActivityResult
                    if (data.data != null) {
                        imageUri = data.data
                        selectedImage1?.setImageURI(imageUri)
                    } else if (data.extras?.get("data") is Bitmap) {
                        image = data.extras?.get("data") as? Bitmap
                        selectedImage?.setImageBitmap(image)
                        Log.d(TAG, "$image")
                    }
                }
            }
        binding.shoppingbtn.setOnClickListener {
            Log.d("shopping", "onCreateView: ")
            startActivity(Intent(requireContext(), shoppingActivity::class.java))
        }
        slider()
        loadImagesFromFirestore()
        attachViewmodel()

        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                // Scan was canceled
                Toast.makeText(requireContext(), "Scan canceled", Toast.LENGTH_SHORT).show()
            } else {
                // Successful scan, result contains the barcode
                val scannedBarcode = result.contents
                Toast.makeText(requireContext(), "Scanned: $scannedBarcode", Toast.LENGTH_SHORT)
                    .show()

                // Optionally, you can use the scanned barcode to look up item details in Firestore or your inventory
                findItemByBarcode(scannedBarcode)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun findItemByBarcode(barcode: String) {
        // Query Firestore or your inventory database with the scanned barcode and fetch item details
        firestore.collection("items").whereEqualTo("timestamp", barcode).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Item found, handle accordingly (e.g., display item details or add to list)
                    val item = querySnapshot.documents[0].toObject(categoryModel::class.java)
                    Toast.makeText(
                        requireContext(),
                        "Item found: ${item?.addname}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "No item found with barcode: $barcode",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun bitmapToFileUri(context: Context, bitmap: Bitmap): Uri? {
        val file = File(context.cacheDir, "image_${System.currentTimeMillis()}.jpg")

        return try {
            // Write the bitmap to the file
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos) // Compress the image
            fos.flush()
            fos.close()

            // Return the Uri from the file
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun uploadImageFromCamera(image: Bitmap) {

        val imageUri = bitmapToFileUri(requireContext(), image)


        if (imageUri != null) {

            val storageRef =
                FirebaseStorage.getInstance().reference.child("Images/${UUID.randomUUID()}.jpg")

            storageRef.putFile(imageUri)
                .addOnSuccessListener {

                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        Log.d(TAG, "Image successfully uploaded. Download URL: $downloadUri")

                        saveDownloadUrlToFirestore(downloadUri.toString())
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Failed to upload image", exception)
                    Toast.makeText(
                        requireContext(),
                        "Failed to upload image: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(requireContext(), "Failed to get image URI", Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveDownloadUrlToFirestore(downloadUrl: String) {
        // Create a new document reference to ensure a custom ID is used
        val documentReference = firestore.collection("images").document()

        // Set the custom Id using the document ID from Firestore or a predefined value
        val model = categoryModel(
            name = downloadUrl,
            Id = documentReference.id, // Assigning the Firestore document ID to the model
            addname = inputtext.text.toString(),
            quantity = fulltext,
            quantityname = fulltextlit
        )


        documentReference.set(model)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Successfully Added")
                    loadImagesFromFirestore() // Refresh the data
                }
            }.addOnFailureListener { error ->
                Toast.makeText(requireContext(), "${error.message}", Toast.LENGTH_SHORT).show()
            }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun loadImagesFromFirestore() {
        list.clear()
        firestore.collection("images").orderBy("timestamp", Direction.DESCENDING).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result?.documents
                    liveData.value?.clear()
                    documents?.forEach { document ->
                        val model = document.toObject(categoryModel::class.java)
                        model?.Id = document.id
                        if (model != null) {
                            list.add(model).also {
                                liveData.postValue(list)
                            }
                        }
                    }.also {
                        adapter.notifyDataSetChanged()
                    }
                }
            }.addOnFailureListener { error ->
                Toast.makeText(
                    requireContext(),
                    "Failed to load images: ${error.message}",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val fileRef = storageRef.child("${UUID.randomUUID()}.*")
        fileRef.putFile(imageUri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    Log.d(TAG, "downloadUri: $downloadUri")
                    saveDownloadUrlToFirestore(downloadUri.toString())
                    Toast.makeText(
                        requireContext(),
                        "Image uploaded successfully in storage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                Log.d(TAG, "Failed to upload image")
                Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun AlertDialog() {
        val dialog = AlertDialog.Builder(requireContext())
        val inflate = layoutInflater
        val view = inflate.inflate(R.layout.alertdailge, null)
        dialog.setView(view)
        val alertDialog = dialog.create()
        val add = view.findViewById<Button>(R.id.add_btn)
        val fab = view.findViewById<FloatingActionButton>(R.id.floating_btn)
        val kg = view.findViewById<RadioButton>(R.id.kg)
        val liter = view.findViewById<RadioButton>(R.id.liter)
        inputtext = view.findViewById(R.id.add_name)
        inputtext2 = view.findViewById(R.id.quantity)
        selectedImage = view.findViewById(R.id.alert_post_image)
        fab.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                activityResultLauncher.launch(intent)

            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), code)
            }
        }


        kg.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                inputtext2.hint = "Enter KG"
                whichChecked = "kg"

            } else {
                inputtext2.hint = "Enter Liter"
            }

        }

        liter.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                inputtext2.hint = "Enter Liter"
                whichChecked = "Liter"

            } else {
                inputtext2.hint = "Enter KG"
            }
        }

        add.setOnClickListener {
            if (image != null) {
                inputtext.error = "Required!!"
                inputtext2.error = "Required!!"
                uploadImageFromCamera(image!!)
                inputtext.text.toString().trim()
                fulltextlit = whichChecked.toString()

                fulltext = inputtext2.text.toString().trim()
            } else {
                Toast.makeText(requireContext(), "Required", Toast.LENGTH_SHORT).show()
            }
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == code) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                activityResultLauncher.launch(intent)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permission required to take pic",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun alertDailoggallery() {
        val dailog = AlertDialog.Builder(requireContext())
        val inflate = layoutInflater
        val view = inflate.inflate(R.layout.alertdailgegallery, null)
        dailog.setView(view)
        val alertdailgue = dailog.create()
        val fab = view.findViewById<FloatingActionButton>(R.id.floating_btn)
        val add = view.findViewById<Button>(R.id.add_btn)

        val kg = view.findViewById<RadioButton>(R.id.kg)
        val liter = view.findViewById<RadioButton>(R.id.liter)
        inputtext2 = view.findViewById(R.id.quantity)
        inputtext = view.findViewById(R.id.add_name)
        selectedImage1 = view.findViewById(R.id.alert_post_image)
        fab.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            activityResultLauncher.launch(intent)

        }
        kg.setOnCheckedChangeListener { _, ischecked ->
            if (ischecked) {
                inputtext2.hint = "Enter kg"
                whichChecked = "Kg"

            } else {
                inputtext2.hint = "Enter Liter"
            }
        }
        liter.setOnCheckedChangeListener { _, ischecked ->
            if (ischecked) {
                inputtext2.hint = "Enter Liter"
                whichChecked = "Liter"
            } else {
                inputtext2.hint = "Enter Kg"
            }
        }
        add.setOnClickListener {
            if (imageUri != null) {
                inputtext.error = "Required!!"
                inputtext2.error = "Required!!"
                imageUri?.let {
                    fulltextlit = whichChecked.toString()
                    inputtext.text.toString().trim()
                    fulltext = this.inputtext2.text.toString().trim()
                    uploadImageToFirebaseStorage(it)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "without item pic can't upload ",
                    Toast.LENGTH_SHORT
                ).show()
            }


            alertdailgue.dismiss()
        }
        alertdailgue.show()

    }

    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun updateImage(decumentId: String, imageUri: Uri, text: String) {
        val storageref = firebaseStorage.reference.child("images/${UUID.randomUUID()}.*")
        storageref.putFile(imageUri)
            .addOnSuccessListener {
                storageref.downloadUrl.addOnSuccessListener { downloadUri ->
                    Toast.makeText(
                        requireContext(),
                        "Image uploaded sccessfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    UpdateImageUrlFirestore(decumentId, downloadUri.toString(), text)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "image upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    fun UpdateImageUrlFirestore(decumentId: String, downloadurl: String, text: String) {
        firestore.collection("images").document(decumentId)
            .update("name", downloadurl, "addname", text).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("image update successfully")
                    loadImagesFromFirestore()
                }
            }.addOnFailureListener {
                showToast("images updated failed")
            }
    }

    fun dailgueUpdate(decumentId: String) {
        val dailgue = AlertDialog.Builder(requireContext())
        val infalte = layoutInflater
        val view = infalte.inflate(R.layout.update, null)
        dailgue.setView(view)
        val alertdailgue = dailgue.create()
        val camera = view.findViewById<MaterialButton>(R.id.cameraUpdate)
        val gallery = view.findViewById<MaterialButton>(R.id.galleryupdate)
        val kg = view.findViewById<RadioButton>(R.id.kg)
        val liter = view.findViewById<RadioButton>(R.id.liter)
        val add = view.findViewById<Button>(R.id.add_btn)
        inputtext = view.findViewById(R.id.add_name)
        inputtext2 = view.findViewById(R.id.quantity)
        selectedImage1 = view.findViewById(R.id.alert_post_image)
        selectedImage = view.findViewById(R.id.alert_post_image)
        kg.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId) {
                inputtext2.hint = "Enter kg"
                inputtext2.setError("Must Required")
                whichChecked = "Kg"
            } else {
                inputtext2.hint = "Enter Liter"
            }
        }
        liter.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId) {
                inputtext2.hint = "Enter Liter"
                inputtext2.setError("Must Required")
                whichChecked = "Liter"
            } else {
                inputtext2.hint = "Enter Kg"
            }
        }

        camera.setOnClickListener {
//            Dexter.withContext(requireContext())
//                .withPermission(Manifest.permission.CAMERA)
//                .withListener(listener)
//                .check()
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                activityResultLauncher.launch(intent)

            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), code)
            }

        }
        gallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            activityResultLauncher.launch(intent)
        }

        add.setOnClickListener {
            if (imageUri != null) {
                imageUri?.let {
                    fulltextlit = whichChecked.toString()
                    updateImage(decumentId, it, inputtext.text.toString().trim())
                    fulltext = this.inputtext2.text.toString().trim()
                }
            } else if (image != null) {
                val uri = bitmapToFileUri(requireContext(), image!!)
                fulltextlit = whichChecked.toString()
                updateImage(decumentId, uri!!, inputtext.text.toString().trim())
                fulltext = this.inputtext2.text.toString().trim()
            }
            alertdailgue.dismiss()
        }
        alertdailgue.show()
    }

    private val listener = object : PermissionListener {
        override fun onPermissionGranted(response: PermissionGrantedResponse) {

        }

        override fun onPermissionDenied(response: PermissionDeniedResponse) {}

        override fun onPermissionRationaleShouldBeShown(
            p0: PermissionRequest?,
            p1: PermissionToken?
        ) {
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadRecipeNameOnFireStore() {
        list2.clear()
        firestore.collection("recipe").orderBy("date", Direction.ASCENDING)
            .get()
            .addOnCompleteListener { taskloadRecipe ->
                if (taskloadRecipe.isSuccessful) {
                    val documents = taskloadRecipe.result?.documents
                    documents?.forEach { document ->
                        val model = document.toObject(RecipeModel::class.java)
                        model?.Id = document.id
                        if (model != null) {
                            list2.add(model)
                        }
                    }
                    adapter1.notifyDataSetChanged()

                } else {
                    Log.e("TAG", "Error loading data: ${taskloadRecipe.exception?.message}")
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to load recipes: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun suggestionList() {
        firestore.collection("recipe").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result.documents
                    documents.forEach { document ->
                        val model = document.toObject(RecipeModel::class.java)
                        recipenames.add(model!!)
                    }
                }
            }
    }

    private fun slider() {
        val imageList = ArrayList<SlideModel>()

        imageList.add(SlideModel(R.drawable.burger, "Yummy Burger", ScaleTypes.CENTER_INSIDE))
        imageList.add(SlideModel(R.drawable.qarooma, "delicious Qorma", ScaleTypes.CENTER_INSIDE))
        imageList.add(SlideModel(R.drawable.biryani, "Spicy Biryani", ScaleTypes.CENTER_INSIDE))
        imageList.add(SlideModel(R.drawable.paya, "Yummy plate", ScaleTypes.CENTER_INSIDE))
        imageList.add(SlideModel(R.drawable.pizza, "Pizza", ScaleTypes.CENTER_INSIDE))

        binding.imageSlider.setImageList(imageList)
    }

    private fun notification(name: String?, quantity: String?) {
        val channelId = "Grocery_Management_App"
        val notificationId = System.currentTimeMillis().toInt()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "GroceryManagementApp"
            val descriptiveText = "Notification For grocery item"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptiveText
            }

            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }


        val notificationBuilder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.projecticon)
            .setContentTitle("GroceryManagementApp")
            .setContentText("Remaining  Your item Quanity $name is $quantity ")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(requireContext())) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED

            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(notificationId, notificationBuilder.build())
        }
    }

    fun notificationCondition() {
        firestore.collection("images").get()
            .addOnCompleteListener { task ->
                val document = task.result.documents
                document.forEach { documents ->
                    val model = documents.toObject(categoryModel::class.java)


                }

            }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Synchronized
    private fun attachViewmodel() {
        liveData.observe(viewLifecycleOwner) { data ->
            data.forEach { model ->
                if (model.quantity?.toInt() == 0 || model.quantity?.toInt()!! < 5) {
                    notification(model.addname, model.quantity)
                }
            }.also {
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun barcodealertdailog(quantity: String) {
        val dailog = AlertDialog.Builder(requireContext(), R.style.TransparentAlertDialog)
        val inflate = layoutInflater
        val view = inflate.inflate(R.layout.barcodescan, null)
        dailog.setView(view)
        val Alertdailog = dailog.create()

        val imagescan = view.findViewById<ImageView>(R.id.imagebarcode)
        val cancelbtn = view.findViewById<MaterialButton>(R.id.exit)
        imagescan.viewTreeObserver.addOnPreDrawListener {
            genBarcode(quantity, imagescan)
            true
        }

        cancelbtn.setOnClickListener {
            Alertdailog.dismiss()
        }

        Alertdailog.show()
    }

    private fun genBarcode(quantity: String?, imageView: ImageView) {
        if (quantity.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Invalid quantity", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val writer = MultiFormatWriter()
            val matrix = writer.encode(
                quantity,
                BarcodeFormat.CODE_128,
                imageView.width, // Width of the ImageView
                imageView.height // Height of the ImageView
            )

            val bitmap =
                Bitmap.createBitmap(imageView.width, imageView.height, Bitmap.Config.RGB_565)

            for (x in 0 until imageView.width) {
                for (y in 0 until imageView.height) {
                    bitmap.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }

            imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Log.d("Hello", "genBarcode: $e")
            Toast.makeText(requireContext(), "Error generating barcode: $e", Toast.LENGTH_SHORT)
                .show()
        }
    }
}