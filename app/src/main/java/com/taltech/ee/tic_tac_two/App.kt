package com.taltech.ee.tic_tac_two

import android.app.Activity
import android.app.Application
import android.os.Bundle

class App : Application(), Application.ActivityLifecycleCallbacks {
    private var activityCount = 0

    override fun onCreate() {
        super.onCreate()
        MusicPlayerHelper.initialize(this)
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityStarted(activity: Activity) {
        if (activityCount == 0) {
            MusicPlayerHelper.startMusic()
        }
        activityCount++
    }

    override fun onActivityStopped(activity: Activity) {
        activityCount--
        if (activityCount == 0) {
            MusicPlayerHelper.stopMusic()
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterActivityLifecycleCallbacks(this)
        MusicPlayerHelper.release()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
    }
