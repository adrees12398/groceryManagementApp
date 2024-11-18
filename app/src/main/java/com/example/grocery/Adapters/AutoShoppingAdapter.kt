package com.example.Adapters
import android.view.View
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grocessarymanagmentapp.R
import com.example.models.AutoShoppingListModel

class AutoShoppingAdapter(val context: Context, val arrayList: ArrayList<AutoShoppingListModel>):RecyclerView.Adapter<ShoppingHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingHolder {
        val inflate = LayoutInflater.from(parent.context)
        val view = inflate.inflate(R.layout.shoppinglistlayout,parent,false)
        return ShoppingHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ShoppingHolder, position: Int) {
        holder.shoppingname.text = arrayList[position].Shoppingname
        holder.shoppingImage.setOnClickListener {

        }
    }
}
class ShoppingHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    val shoppingname:TextView = itemView.findViewById(R.id.shoppingname)
    val shoppingImage:ImageView = itemView.findViewById(R.id.shoppingimage)
}