package com.example.grocessarymanagmentapp.Adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grocessarymanagmentapp.R
import com.example.grocessarymanagmentapp.models.RecipeModel
import com.example.grocessarymanagmentapp.models.RecipeShowModel
import com.example.grocessarymanagmentapp.models.categoryModel
import com.google.android.material.card.MaterialCardView

class ShowRecipeAdapter(val context: Context, var arrayList: ArrayList<RecipeModel>,var click:(Int, String) -> Unit):RecyclerView.Adapter<ShowRecipeHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowRecipeHolder {
        val infalte = LayoutInflater.from(parent.context)
        val view = infalte.inflate(R.layout.recipelayout,parent,false)
        return ShowRecipeHolder(view)
    }

    override fun getItemCount(): Int {
       return arrayList.size
    }

    override fun onBindViewHolder(holder: ShowRecipeHolder, position: Int) {
        val Recipe = arrayList[position]
        holder.RecipeName.text = Recipe.addname
        holder.delete.setOnClickListener {
              click.invoke(position,"delete")
        }
        holder.recipeview.setOnClickListener {
            click.invoke(position,"open")
        }
    }
}
class ShowRecipeHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    val RecipeName:TextView = itemView.findViewById(R.id.recipename)
    var delete :ImageView = itemView.findViewById(R.id.delete)
    val recipeview:MaterialCardView =itemView.findViewById(R.id.recipeview)
}