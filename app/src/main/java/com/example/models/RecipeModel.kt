package com.example.grocessarymanagmentapp.models


import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.Date

class RecipeModel(
    var Id:String? = null,
    var addname: String? = null,
    @ServerTimestamp
    val date: Date? = null
) : Serializable