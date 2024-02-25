package com.dvbuslaev.aston_intensiv_1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dvbuslaev.aston_intensiv_1.R

class MusicService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private val binder = MusicBinder()
    val songList = mutableListOf(R.raw.lida, R.raw.lida, R.raw.lida)
    var currentSongIndex = 0
    var isPlaying = false

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        Log.d("MusicService", "onCreate")
        mediaPlayer = MediaPlayer.create(this, songList[currentSongIndex])
        mediaPlayer.isLooping = true
    }

    fun startMusic() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            isPlaying = true
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MusicService", "onStartCommand")

        return START_STICKY
    }

    fun stopMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
        }
    }

    fun nextSong() {
        if (currentSongIndex < songList.size - 1) {
            mediaPlayer.release()
            mediaPlayer = MediaPlayer.create(this, songList[currentSongIndex])
            currentSongIndex++
            mediaPlayer.start()
            mediaPlayer.isLooping = true
        }
    }

    fun prevSong() {
        if (currentSongIndex < songList.size - 1) {
            mediaPlayer.release()
            mediaPlayer = MediaPlayer.create(this, songList[currentSongIndex])
            currentSongIndex++
            mediaPlayer.isLooping = true
            mediaPlayer.start()
            currentSongIndex--
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        Log.d("MusicService", "onDestroy")
    }


    companion object {
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "channel_name"
        private const val NOTIFICATION_ID = 5

    }

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Music Player")
        .setContentText("Music is playing")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setOngoing(true)
        .build()
}