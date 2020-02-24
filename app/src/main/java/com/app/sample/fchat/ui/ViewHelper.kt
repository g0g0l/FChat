package com.app.sample.fchat.ui

import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context


/**
 *Created by Bibaswann Bandyopadhyay on 20-02-2020.
 */
class ViewHelper(private val mContext: Context) {
    private var progressDialog: ProgressDialog? = null
    public fun showProgressDialog() {
        progressDialog = ProgressDialog(mContext)
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    public fun dismissProgressDialog() {
        progressDialog?.hide()
    }

    fun clearNotofication() {
        val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
}