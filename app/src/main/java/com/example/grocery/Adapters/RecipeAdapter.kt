package com.example.grocessarymanagmentapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grocessarymanagmentapp.R
import com.example.grocessarymanagmentapp.models.categoryModel

class RecipeAdapter(
    val context: Context,
    private var arrayList: ArrayList<categoryModel>,
    private var click: (Int, String) -> Unit
) : RecyclerView.Adapter<RecipeHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeHolder {
        val infalte = LayoutInflater.from(parent.context)
        val view = infalte.inflate(R.layout.recipteitemview, parent, false)
        return RecipeHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: RecipeHolder, position: Int) {
        val category = arrayList[position]
        holder.name.text = category.addname
        holder.nameweight.text = category.quantityname
        holder.quantity.text = category.quantity.toString()
        holder.incrementdecrement.text = category.itemQuanity.toString()

        holder.add.setOnClickListener {
           holder.incrementdecrement.text = category.itemQuanity.toString()
            click.invoke(position, "add")
        }

        holder.remove.setOnClickListener {
            click.invoke(position, "sub")
        }

        Glide.with(context).load(category.name).placeholder(R.drawable.pic2).into(holder.image)
    }
}

class RecipeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val image: ImageView = itemView.findViewById(R.id.ivImage)
    val name: TextView = itemView.findViewById(R.id.textname)
    val quantity: TextView = itemView.findViewById(R.id.weight)
    val add: ImageView = itemView.findViewById(R.id.add)
    val remove: ImageView = itemView.findViewById(R.id.sub)
    val incrementdecrement = itemView.findViewById<TextView>(R.id.increment_decrement)
    val nameweight = itemView.findViewById<TextView>(R.id.weightname)
}