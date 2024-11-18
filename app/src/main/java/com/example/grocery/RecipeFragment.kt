package com.example.grocery

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocessarymanagmentapp.Adapters.RecipeAdapter
import com.example.grocessarymanagmentapp.Adapters.ShowRecipeAdapter
import com.example.grocessarymanagmentapp.R
import com.example.grocessarymanagmentapp.databinding.FragmentRecipeBinding
import com.example.grocessarymanagmentapp.models.RecipeModel
import com.example.grocessarymanagmentapp.models.categoryModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction
import com.google.firebase.firestore.SetOptions

class RecipeFragment : Fragment() {
    lateinit var binding: FragmentRecipeBinding
    private lateinit var sessionClass: SessionClass
    var list = ArrayList<RecipeModel>()
    var list2 = ArrayList<categoryModel>()
    lateinit var firestore: FirebaseFirestore
    lateinit var adapter: ShowRecipeAdapter
    lateinit var adapter2: RecipeAdapter

    var id: String? = null

    private var item: ArrayList<categoryModel> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeBinding.inflate(layoutInflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        sessionClass = SessionClass(requireContext())
        binding.materialButton.setOnClickListener {
            startActivity(Intent(requireContext(), ItemofRecipeShow::class.java))
        }

        binding.recipeRecycler.setHasFixedSize(true)
        binding.recipeRecycler.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)


        adapter = ShowRecipeAdapter(requireContext(), list) { position, delete ->
            this@RecipeFragment.id = list[position].Id



            if (delete.equals("delete", true)) {
                deletedailog(position)

            } else if (delete.equals("open", true)) {
                openDialog()

            }
        }
        binding.recipeRecycler.adapter = adapter
        loadRecipeNameOnFireStore()
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    @Synchronized
    private fun click() {
        sessionClass.getUId()?.let {
            firestore.collection("images").document(it).collection("items").orderBy("timestamp", Direction.ASCENDING).get()
                .addOnCompleteListener { taskimages ->
                    if (taskimages.isSuccessful) {
                        val documents1 = taskimages.result.documents
                        documents1.forEach { doc ->
                            val mod = doc.toObject(categoryModel::class.java)
                            if (mod != null) {
                                list2.add(mod)
                                val quanity1 = mod.quantity?.toInt() ?: 0
                                if (quanity1 > 0) {
                                    firestore.collection("recipe").document(this.id!!).collection("items").get()
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                                val documents = task.result.documents
                                                documents.forEach { document ->
                                                    val model = document.toObject(categoryModel::class.java)
                                                    if (model != null) {
                                                        list2.add(model)

                                                        val quantity = model.quantity?.toInt() ?: 0
                                                        if (quantity > 0) {

                                                            if (quantity >= model.itemQuanity) {
                                                                firestore.collection("images").document(
                                                                    sessionClass.getUId()!!
                                                                ).collection("items").document(model.Id) .get()
                                                                    .addOnCompleteListener { doc ->
                                                                        if (doc.isSuccessful) {

                                                                            val proModel = doc.result.toObject(categoryModel::class.java)

                                                                            proModel?.quantity = model.itemQuanity.let {
                                                                                proModel?.quantity?.toInt()?.minus(it)?.toString()
                                                                            }
                                                                            proModel?.let {
                                                                                firestore.collection("images").document(
                                                                                    sessionClass.getUId()!!
                                                                                ).collection("items").document(model.Id)
                                                                                    .set(it, SetOptions.merge())
                                                                            }

                                                                        }
                                                                    }
                                                            } else {
                                                                Toast.makeText(
                                                                    requireContext(), "Insufficient quantity", Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        } else {
                                                            val dialog = AlertDialog.Builder(this.requireContext())
                                                            dialog.setTitle("Are you want to add the data")
                                                            val names = mutableListOf<String>()
                                                            list2.forEach { mod ->
                                                                mod.addname?.let { names.add(it) }
                                                            }
                                                            dialog.setMessage(
                                                                "Please enter the items values ${names.joinToString(", ")}"
                                                            )
                                                            dialog.setPositiveButton("Ok") { delete, _ ->
                                                                delete.dismiss()
                                                            }
                                                            dialog.show()
                                                        }
                                                    } else {
                                                        Toast.makeText(
                                                            this.requireContext(),
                                                            "Firstly edit your quantities",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }.also {
                                                    this.activity?.runOnUiThread {
                                                        list.clear()
                                                        Toast.makeText(this.activity, "Finish", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        }

                                }else{
                                    val dialog = AlertDialog.Builder(this.requireContext())
                                    dialog.setTitle(" AlertDialog ")
                                    val names = mutableListOf<String>()
                                    list2.forEach { mod1 ->
                                        mod1.addname?.let { names.add(it) }
                                    }
                                    dialog.setMessage(
                                        "Please enter the items values ${names.joinToString(", ")}"
                                    )
                                    dialog.setPositiveButton("Ok") { delete, _ ->
                                        delete.dismiss()
                                    }
                                    dialog.show()
                                }
                            }
                        }
                    }
                }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun loadRecipeNameOnFireStore() {
        list.clear()
        firestore.collection("recipe").orderBy("date", Direction.ASCENDING).get()
            .addOnCompleteListener { taskloadRecipe ->
                if (taskloadRecipe.isSuccessful) {
                    val documents = taskloadRecipe.result?.documents
                    documents?.forEach { document ->
                        val model = document.toObject(RecipeModel::class.java)
                        model?.Id = document.id
                        if (model != null) {
                            list.add(model)
                            adapter.notifyDataSetChanged()
                        }
                    }

                } else {
                    Log.e("TAG", "Error loading data: ${taskloadRecipe.exception?.message}")
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to load recipes: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun delclick(position: Int) {
        val recipeId = list[position].Id
        if (recipeId != null) {
            firestore.collection("recipe").document(recipeId).delete()
                .addOnCompleteListener { deletetask ->
                    if (deletetask.isSuccessful) {
                        Toast.makeText(
                            requireContext(), "Deleted Recipe", Toast.LENGTH_SHORT
                        ).show()
                        list.removeAt(position)
                        adapter.notifyItemRemoved(position)
                    }
                }
        }
    }

    private fun openDialog() {

        val alertDialog = AlertDialog.Builder(this.requireContext(), R.style.TransparentAlertDialog)
        val view = layoutInflater.inflate(R.layout.recipedailogue, null)
        alertDialog.setView(view)
        val dialog = alertDialog.create()

        val cancel: TextView = view.findViewById(R.id.btnCancel)
        val yes: TextView = view.findViewById(R.id.yes)

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        yes.setOnClickListener {
            click()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deletedailog(position: Int) {
        val dailog = AlertDialog.Builder(this.requireContext(), R.style.TransparentAlertDialog)
        val inflate = layoutInflater
        val view = inflate.inflate(R.layout.removeitemalertdailog, null)
        dailog.setView(view)
        val alertDialog = dailog.create()
        val cancelbtn: MaterialButton = view.findViewById(R.id.delcancel)
        val yesbtn: MaterialButton = view.findViewById(R.id.delYes)
        yesbtn.setOnClickListener {
            delclick(position)
            alertDialog.dismiss()
        }
        cancelbtn.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }
}


