package com.taltech.ee.tic_tac_two

import android.app.Application
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner

class App : Application(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        startService(Intent(this, MusicService::class.java)) // Start the music service when the app launches
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        // Pause music when app goes to background
        val pauseIntent = Intent(this, MusicService::class.java).apply {
            action = "PAUSE_MUSIC"
        }
        startService(pauseIntent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        // Resume music when app comes to foreground
        val resumeIntent = Intent(this, MusicService::class.java).apply {
            action = "RESUME_MUSIC"
        }
        startService(resumeIntent)
    }
}