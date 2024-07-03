package com.example.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class AlarmService : Service() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()

        // Initialize the media player
        mediaPlayer = MediaPlayer.create(this, R.raw.s1).apply {
            isLooping = true
        }

        // Start playing the alarm sound
        mediaPlayer.start()

        // Create the notification channel for the foreground service
        createNotificationChannel()

        // Create an intent to launch the Show activity
        val showIntent = Intent(this, Show::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(showIntent)
        val showPendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, showIntent, PendingIntent.FLAG_IMMUTABLE
        )

        // Create and start the foreground notification
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Alarm")
            .setContentText("Alarm is ringing. Tap to stop.")
            .setContentIntent(showPendingIntent)
            .setAutoCancel(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AlarmService", "Service started")
        // Handle service restart here if necessary
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for Alarm Service"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "AlarmServiceChannel"
        const val NOTIFICATION_ID = 1
    }
}