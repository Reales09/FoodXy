package com.example.foodxy.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.example.foodxy.R
import com.example.foodxy.core.Constants
import com.example.foodxy.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)

        registerNewTokenLocal(newToken)

    }

    private fun registerNewTokenLocal(newToken: String) {

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        preferences.edit {
            putString(Constants.PROP_TOKEN, newToken)
                .apply()
        }

        Log.i("new token", newToken)

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let {
            sendNotification(it)
        }
    }

    private fun sendNotification(notification: RemoteMessage.Notification){

        val intent= Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.notification_channel_id_default)
        val defaulSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.foodxy_notify_logo)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setAutoCancel(true)
            .setSound(defaulSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,getString(R.string.notification_channel_name_default),
                NotificationManager.IMPORTANCE_DEFAULT)

            notificationManager.createNotificationChannel(channel)

        }

        notificationManager.notify(0,notificationBuilder.build())
    }

}