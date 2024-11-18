package com.example.grocessarymanagmentapp.Adapters

import android.annotation.SuppressLint
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
import com.google.android.material.button.MaterialButton

class categoreyAdapter(
    var context: Context,
    var arrayList: ArrayList<categoryModel>,
    private var click: (Int, String) -> Unit
) : RecyclerView.Adapter<viewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val inflate = LayoutInflater.from(context)
        val view = inflate.inflate(R.layout.recyclerviewcat, parent, false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int = arrayList.size

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val category = arrayList[position]
        holder.name.text = category.addname
        holder.quantity.text = category.quantity.toString()
        holder.weightname.text = category.quantityname
        Glide.with(context).load(category.name).placeholder(R.drawable.pic2).into(holder.image)

        holder.barcodeBtn.setOnClickListener {
            click.invoke(position, "Scan")
        }

        holder.UpdateBtn.setOnClickListener {
            click.invoke(position, "update")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(list: ArrayList<categoryModel>) {
        this.arrayList = list
        notifyDataSetChanged()
    }
}

class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var name: TextView = itemView.findViewById(R.id.textname)
    var image: ImageView = itemView.findViewById(R.id.ivImage)
    var quantity: TextView = itemView.findViewById(R.id.weight)
    var UpdateBtn: MaterialButton = itemView.findViewById(R.id.Update)
    var barcodeBtn: ImageView = itemView.findViewById(R.id.barcdodescan)
    var weightname: TextView = itemView.findViewById(R.id.weightname)
}