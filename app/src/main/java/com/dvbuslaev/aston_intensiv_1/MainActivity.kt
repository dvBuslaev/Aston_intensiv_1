package com.dvbuslaev.aston_intensiv_1

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.dvbuslaev.aston_intensiv_1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var musicService: MusicService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //setup click listeners
        with(binding) {
            play.setOnClickListener {
                if (!isBound) {

                    startService(Intent(this@MainActivity, MusicService::class.java))
                    bindService(Intent(this@MainActivity, MusicService::class.java), connection, Context.BIND_AUTO_CREATE)
                    isBound = true

                }
                startMusic()

            }
            stop.setOnClickListener {
                stopMusic()
            }
            prev.setOnClickListener { prevSong() }
            next.setOnClickListener { nextSong() }
        }
    }


    fun startMusic() {
        musicService?.startMusic()
    }

    fun stopMusic() {
        musicService?.stopMusic()
    }

    fun nextSong() {
        musicService?.nextSong()

    }

    fun prevSong() {
        musicService?.prevSong()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }


}