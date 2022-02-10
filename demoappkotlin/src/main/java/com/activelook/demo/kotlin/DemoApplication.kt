package com.activelook.demo.kotlin

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import timber.log.Timber

class DemoApplication :Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleListener())
    }

    inner class AppLifecycleListener : LifecycleObserver {

        val controllerServiceIntent: Intent by lazy {
            Intent(applicationContext, NotificationService::class.java)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onMoveToForeground() {
            stopService(controllerServiceIntent)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onMoveToBackground() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(controllerServiceIntent)
            } else {
                startService(controllerServiceIntent)
            }
        }
    }
}