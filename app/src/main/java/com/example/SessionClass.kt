package com.example.grocessarymanagmentapp

import android.content.Context
import android.content.SharedPreferences

class SessionClass(var context: Context) {
    private var pref: SharedPreferences =
        context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE)

    fun getTheme(): Boolean {
        return pref.getBoolean("theme", false)
    }
    fun setTheme(value: Boolean) {
        pref.edit().putBoolean("theme", value).apply()
    }
    fun getUser(): String? {
        return pref.getString("user", "")
    }
    fun setUser(value: String) {
        pref.edit().putString("user", value).apply()
    }

    fun setvalue(ischecked: Boolean){
         pref.edit().putBoolean("key",ischecked).apply()
    }
    fun getvalue():Boolean{
        return pref.getBoolean("key",false)
    }
    fun settext(a: String){
        pref.edit().putString("switch_text",a).apply()
    }
    fun gettext(): String? {
        return pref.getString("switch_text","")
    }
}