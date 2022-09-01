package com.example.autoclick.service.manager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.autoclick.R
import com.example.autoclick.view.home.HomeActivity
import kotlin.LazyThreadSafetyMode.NONE

class AppNotificationManager constructor(
    private val applicationContext: Context,
) {
    private val notificationManager by lazy(NONE) {
        NotificationManagerCompat.from(applicationContext)
    }

    fun showNotification(contentText: String) = notificationManager.notify(
        NOTIFICATION_ID,
        createNotification(contentText)
    )

    fun cancelNotification() = notificationManager.cancel(NOTIFICATION_ID)

    fun createNotification(contentText: String): Notification {
        fun createNotificationChannelIfAboveAndroidO() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(
                    applicationContext.getString(R.string.notification_tracking_channel_id),
                    applicationContext.getString(R.string.notification_tracking_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                    .apply {
                        description =
                            applicationContext.getString(R.string.notification_tracking_channel_description)
                        lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                        setSound(null, null)
                    }
                    .let { notificationManager.createNotificationChannel(it) }
            }
        }

        createNotificationChannelIfAboveAndroidO()

        val pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) it or PendingIntent.FLAG_IMMUTABLE
            else it
        }
        return NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.notification_tracking_channel_id)
        )
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(contentText)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSound(null)
            .setSilent(true)
            .setVibrate(null)
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    Intent(applicationContext, HomeActivity::class.java),
                    pendingIntentFlags
                )
            )
            .addAction(
                R.drawable.ic_baseline_stop_24,
                applicationContext.getString(R.string.home_stop),
                PendingIntent.getActivity(
                    applicationContext,
                    1,
                    Intent(applicationContext, HomeActivity::class.java),
                    pendingIntentFlags
                )
            )
            .build()
    }

    companion object {
        const val NOTIFICATION_ID = 100
    }
}
