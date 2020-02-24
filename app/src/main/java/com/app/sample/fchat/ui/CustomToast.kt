package com.app.sample.fchat.ui

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.app.sample.fchat.R

class CustomToast(val context: Context) {
    val text: TextView
    val icon: ImageView
    val layout: View

    init {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        layout = inflater.inflate(R.layout.custom_meaasge, null)
        text = layout.findViewById(R.id.text)
        icon = layout.findViewById(R.id.icon)
    }

    private fun showActualToast() {
        val toast = Toast(context)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()

        //More subtle way
//        with(Toast(context)) {
//            setGravity(Gravity.CENTER_VERTICAL, 0, 0)
//            duration = Toast.LENGTH_LONG
//            view = layout
//            show()
//        }
    }

    fun showError(msg: String) {
        text.text = msg
        icon.setImageResource(R.drawable.round_error_24)
        icon.setColorFilter(ContextCompat.getColor(context, R.color.error), android.graphics.PorterDuff.Mode.MULTIPLY)

        showActualToast()
    }

    fun showInfo(msg: String) {
        text.text = msg
        icon.setImageResource(R.drawable.round_info_24)
        icon.setColorFilter(ContextCompat.getColor(context, R.color.info), android.graphics.PorterDuff.Mode.MULTIPLY)

        showActualToast()
    }

    fun showSuccess(msg: String) {
        text.text = msg
        icon.setImageResource(R.drawable.round_success_24)
        icon.setColorFilter(ContextCompat.getColor(context, R.color.success), android.graphics.PorterDuff.Mode.MULTIPLY)

        showActualToast()
    }
}
