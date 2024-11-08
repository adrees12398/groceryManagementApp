package com.example.grocessarymanagmentapp.models

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.Date

class items(
    var Id: String? = null,
    var name: String? = null,
    var addname: String? = null,
    var quantity: String? = null,
    var itemQuanity: Int = 0,
    var quantityname:String?= null,
    @ServerTimestamp
    val date: Date? = null
) : Serializable

