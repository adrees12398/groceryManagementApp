package com.example.grocessarymanagmentapp.models

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.Date

data class categoryModel(
    var Id: String = "",
    var name: String? = null,
    var addname: String? = null,
    var quantity: String? = null,
    var itemQuanity: Int = 0,
    var quantityname:String?= null,
    @ServerTimestamp
    var timestamp: Date? = null
) : Serializable