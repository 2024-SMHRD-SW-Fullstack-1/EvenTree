package com.example.andhack

import android.content.Context
import android.content.SharedPreferences

<<<<<<< HEAD
object ShaeredPrefManager {
=======
object SharedPrefManager {
>>>>>>> jand-working
    private const val PREF_NAME = "MyAppPrefs"
    private const val KEY_TOKEN = "ket_token"

    private fun getPreferences(context: Context): SharedPreferences? {
        return context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)
    }

<<<<<<< HEAD
    fun saveToken(context: Context,token:String){
=======
    fun saveToken(context: Context, token:String){
>>>>>>> jand-working
        val editor = getPreferences(context)?.edit()
        editor?.putString(KEY_TOKEN,token)
        editor?.apply()
    }

    fun getToken(context: Context):String?{
        return getPreferences(context)?.getString(KEY_TOKEN,null)
    }

    fun clearToken(context: Context){
        val editor = getPreferences(context)?.edit()
        editor?.remove(KEY_TOKEN)
        editor?.apply()
    }
}