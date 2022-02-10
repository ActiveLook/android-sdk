package com.activelook.demo.kotlin

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.activelook.activelooksdk.Sdk
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class NotificationService : Service() {

    companion object {
        private const val ACTION_STOP_SERVICE = "action_stop_service"
        private const val NOTIFICATION_ID = 12
    }

//    private val notificationManager: NotificationManager? = (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)

    override fun onCreate() {
        super.onCreate()


        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val stopIntent = Intent(this, this::class.java)
        stopIntent.action = ACTION_STOP_SERVICE

//        val stopPendingIntent =
//            PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT)
//        val stopAction = NotificationCompat.Action.Builder(
//            android.R.drawable.ic_delete,
//            "disconnect",
//            stopPendingIntent
//        )
//            .build()

        //we need to create a channel ID
        // https://stackoverflow.com/questions/47531742/startforeground-fail-after-upgrade-to-android-8-1
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(
                    "Keep_connection",
                    "Keep_connection"
                )
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notification: Notification =
            NotificationCompat.Builder(
                this,
                channelId
            )
                .setContentTitle("App running")
                .setContentText(
                    "App is Running"
                )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
//                .addAction(stopAction)
                .build()

        startForeground(12, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (intent?.action == ACTION_STOP_SERVICE) {
            service.cancel(NOTIFICATION_ID)
            stopSelf()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "keep_connection_id",
                "keep_connection",
                NotificationManager.IMPORTANCE_LOW
            )
            service.createNotificationChannel(channel)
        }

        if (GlassesRepository.connectedGlasses == null) {
            stopSelf()
            return START_NOT_STICKY
        }


        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}