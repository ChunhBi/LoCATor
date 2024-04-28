package com.android.locator

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

val NOTIFICATION_CHANNEL_ID="your_channel_id"
class LoCATorApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = NOTIFICATION_CHANNEL_ID
            val channelName = "New witness"
            val channelDescription = "Send notifications when cats you like are witnessed."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}