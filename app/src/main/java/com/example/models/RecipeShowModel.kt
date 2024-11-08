package com.example.grocessarymanagmentapp.models

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.Date

data class RecipeShowModel(
    var name:String? = null,
    @ServerTimestamp
    val date: Date? = null
):Serializable {

}
