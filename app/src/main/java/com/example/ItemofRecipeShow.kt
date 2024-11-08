package com.example.grocessarymanagmentapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocessarymanagmentapp.Adapters.RecipeAdapter
import com.example.grocessarymanagmentapp.databinding.ActivityItemofRecipeShowBinding
import com.example.grocessarymanagmentapp.extention.Appconstant
import com.example.grocessarymanagmentapp.models.RecipeModel
import com.example.grocessarymanagmentapp.models.categoryModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction

class ItemofRecipeShow : AppCompatActivity() {
    lateinit var binding: ActivityItemofRecipeShowBinding
    private lateinit var adapter: RecipeAdapter
    private var list = ArrayList<categoryModel>()
    lateinit var firestore: FirebaseFirestore
    private var item: ArrayList<categoryModel> = ArrayList()
    lateinit var model: categoryModel


    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemofRecipeShowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        binding.recyclerRecipe.setHasFixedSize(true)
        binding.recyclerRecipe.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = RecipeAdapter(this, list) { position, add ->


            model = list[position]

            if (add.equals("add", true)) {
                val totalQuantity = model.quantity?.toInt() ?: 0


                  if (model.itemQuanity < totalQuantity) {
                    model.itemQuanity = model.itemQuanity.plus(1)
                      list[position] = model
                      item.add(model)
                      adapter.notifyItemChanged(position)

                }



                if (::model.isInitialized) {
                    val existingItemIndex =
                        Appconstant.arrayList.indexOfFirst { it.Id == model.Id }

                    if (existingItemIndex != -1) {
                        Appconstant.arrayList[existingItemIndex].itemQuanity = model.itemQuanity


                    }else {
                            Appconstant.arrayList.add(model)
                            Log.d("Item2", "Item added: $model")
                        }
                    } else {
                        Toast.makeText(this, "Model is not initialized", Toast.LENGTH_SHORT).show()
                    }

                } else if (add.equals("sub", true)) {
                    if (model.itemQuanity > 0) {
                        model.itemQuanity = model.itemQuanity.minus(1)
                        list[position] = model
                        item.remove(model)
                        adapter.notifyItemChanged(position)
                    }

                    if (::model.isInitialized) {
                        val existingItemIndex =
                            Appconstant.arrayList.indexOfFirst { it.Id == model.Id }

                        if (existingItemIndex != -1) {
                            Appconstant.arrayList[existingItemIndex].itemQuanity = model.itemQuanity

                            Log.d(
                                "Item2",
                                "Item already exists. Updated quantity: ${Appconstant.arrayList[existingItemIndex].itemQuanity}"
                            )
                        } else {
                            Appconstant.arrayList.add(model)
                            Log.d("Item2", "Item added: $model")
                        }
                    } else {
                        Toast.makeText(this, "Model is not initialized", Toast.LENGTH_SHORT).show()
                    }
                }




                adapter.notifyDataSetChanged()
            }

        binding.recyclerRecipe.adapter = adapter
        loaddatafromfirestore()
        actions()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun actions() {
        list.clear()
        binding.materialButton.setOnClickListener {
            val recipeName = binding.addName.text.toString()
            if (recipeName.isEmpty()) {
                binding.addName.error = "Field required"
                return@setOnClickListener
            }

            if (list.isEmpty()) {
                Toast.makeText(this, "Please select at least one item", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Appconstant.arrayList.forEach { model ->
                if (!list.contains(model))
                    list.add(model)
            }
            val recipeDocument = firestore.collection("recipe").document()

            Appconstant.documentId = recipeDocument.id
            val recipeModel = RecipeModel(addname = recipeName, Id = Appconstant.documentId)
            recipeDocument.set(recipeModel).addOnCompleteListener { recipetask ->
                if (recipetask.isSuccessful) {
                    Toast.makeText(this, "Recipe name Set", Toast.LENGTH_SHORT).show()

                    binding.addName.setText("")

                    list.forEach { itemSelect ->
                        recipeDocument.collection("items").document().set(itemSelect)
                            .addOnCompleteListener { taskitems ->
                                if (taskitems.isSuccessful) {
                                    Toast.makeText(this, "items add to recipe", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    Toast.makeText(
                                        this,
                                        " failed to add ${taskitems.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }.addOnFailureListener {
                                Toast.makeText(
                                    this,
                                    "Recipe name don't add ${recipetask.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }.also {
                        list.clear()
                        adapter.notifyDataSetChanged()
                        this.finish()
                    }

                } else {
                    Toast.makeText(this, "Recipe name not set", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loaddatafromfirestore() {
        firestore.collection("images").orderBy("timestamp", Direction.DESCENDING).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result?.documents
                    documents?.forEach { document ->
                        val model = document.toObject(categoryModel::class.java)
                        model?.Id = document.id
                        if (model != null) {
                            model.itemQuanity = 0
                            list.add(model)
                        }
                    }.also {
                        adapter.notifyDataSetChanged()
                    }
                }

            }.addOnFailureListener {
                Toast.makeText(this, "failure", Toast.LENGTH_SHORT).show()
            }
    }
}
