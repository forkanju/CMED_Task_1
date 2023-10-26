package com.example.workmanagerdemo.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import javax.inject.Inject


class MyService : Service() {
    @Inject
    lateinit var notificationManager: NotificationManager
    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    private val binder = MyServiceBinder()


    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    inner class MyServiceBinder : Binder() {
        fun getService(): MyService = this@MyService
    }

}

enum class StopwatchState {
    Started,
    Stopped,
    Canceled
}