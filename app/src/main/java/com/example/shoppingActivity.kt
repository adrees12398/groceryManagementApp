package com.example

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Adapters.AutoShoppingAdapter
import com.example.grocessarymanagmentapp.databinding.ActivityShoppingBinding
import com.example.grocessarymanagmentapp.models.categoryModel
import com.example.models.AutoShoppingListModel
import com.google.firebase.firestore.FirebaseFirestore

class shoppingActivity : AppCompatActivity() {
    lateinit var binding: ActivityShoppingBinding
    private lateinit var firestore: FirebaseFirestore
    private var list = ArrayList<AutoShoppingListModel>()
    private lateinit var adapter: AutoShoppingAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestore = FirebaseFirestore.getInstance()

        adapter = AutoShoppingAdapter(this, list)
        binding.recyclerViewShopping.setHasFixedSize(true)
        binding.recyclerViewShopping.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.recyclerViewShopping.adapter = adapter

        autoCreatingShoppinglist()

        adapter.notifyDataSetChanged()
    }

    private fun autoCreatingShoppinglist() {
        firestore.collection("images").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result.documents
                    documents.forEach { document ->
                        val model = document.toObject(categoryModel::class.java)


                        if (model != null && (model.quantity?.toInt() == 0 || model.quantity?.toInt()!! < 5)) {

                            val shoppingListModel = AutoShoppingListModel(
                                Id = model.Id, // Assuming you have an `id` field in categoryModel
                                Shoppingname = model.addname // Set addname to Shoppingname
                            )

                            // Add the new shopping item to the list
                            list.add(shoppingListModel)
                        }
                    }

                    // Notify adapter after data is updated
                    adapter.notifyDataSetChanged()
                }
            }
    }
}