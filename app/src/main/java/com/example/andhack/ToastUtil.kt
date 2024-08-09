package com.example.andhack

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast

object ToastUtil {

    fun showSuccessToast(context: Context, message: String) {
        showToast(context, message, R.layout.custom_toast_success)
    }

    fun showFailureToast(context: Context, message: String) {
        showToast(context, message, R.layout.custom_toast_failure)
    }

    fun showInfoToast(context: Context, message: String){
        showToast(context, message, R.layout.custom_toast_info)
    }

    private fun showToast(context: Context, message: String, layoutId: Int) {
        val inflater = LayoutInflater.from(context)
        val layout: View = inflater.inflate(layoutId, null)

        val toastText = layout.findViewById<TextView>(R.id.toast_text)
        toastText.text = message

        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}