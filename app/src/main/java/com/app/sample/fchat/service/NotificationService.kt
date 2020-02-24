package com.app.sample.fchat.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.app.sample.fchat.R
import com.app.sample.fchat.activity.MainActivity
import com.app.sample.fchat.data.ParseFirebaseData
import com.app.sample.fchat.model.ChatMessage
import com.app.sample.fchat.util.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
/**
Created by bibaswann on 26/09/18.
 */


class NotificationService : JobService() {

    var mNotificationManager: NotificationManager? = null

    //Todo: do something about multiple notification.
    override fun onStartJob(params: JobParameters?): Boolean {
        val ref = FirebaseDatabase.getInstance().getReference(Constants.MESSAGE_CHILD)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(Constants.LOG_TAG, "Data changed from service")
                for (oneChat: ChatMessage in ParseFirebaseData(this@NotificationService).getAllUnreadReceivedMessages(dataSnapshot)) {
//                    Log.e(Constants.LOG_TAG, oneChat.text + "\n")
                    showNotification(oneChat.senderName.toString(), oneChat.text.toString())
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    fun showNotification(title: String, content: String) {
        //Todo: notification grouping
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("default", "Fchat-Message", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "New message notification for fchat"
            mNotificationManager!!.createNotificationChannel(channel)
        }
        val mBuilder = NotificationCompat.Builder(applicationContext, "default")
                .setSmallIcon(R.drawable.ic_logo_white) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(content)// message for notification
                .setAutoCancel(true) // clear notification after click
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(pi)
        mNotificationManager!!.notify(0, mBuilder.build())
    }

    fun clearNotification() {
        mNotificationManager!!.cancelAll()
    }

}