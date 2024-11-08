package com.example.grocessarymanagmentapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.grocessarymanagmentapp.R
import com.example.grocessarymanagmentapp.models.RecipeModel
import com.example.grocessarymanagmentapp.models.RecipeShowModel

class SuggestionRecipe(val context:Context, var arrayList: ArrayList<RecipeModel>):RecyclerView.Adapter<SuggestionHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionHolder {
        val infalte = LayoutInflater.from(parent.context)
        val view = infalte.inflate(R.layout.suggestionrecipe,parent,false)
        return SuggestionHolder(view)
    }

    override fun getItemCount(): Int {
       return arrayList.size

    }

    override fun onBindViewHolder(holder: SuggestionHolder, position: Int) {
        holder.Recipename.text = arrayList[position].addname
    }
}
class SuggestionHolder(itemView:View):RecyclerView.ViewHolder(itemView){
    val Recipename = itemView.findViewById<TextView>(R.id.suggestionrecipe)

}