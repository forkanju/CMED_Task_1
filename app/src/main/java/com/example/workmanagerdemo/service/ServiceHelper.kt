package com.example.workmanagerdemo.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.workmanagerdemo.MainActivity
import com.example.workmanagerdemo.util.Constants.CANCEL_REQUEST_CODE
import com.example.workmanagerdemo.util.Constants.CLICK_REQUEST_CODE
import com.example.workmanagerdemo.util.Constants.DOWNLOAD_STATE
import com.example.workmanagerdemo.util.Constants.RESUME_REQUEST_CODE
import com.example.workmanagerdemo.util.Constants.STOP_REQUEST_CODE


object ServiceHelper {

    private val flag =
        PendingIntent.FLAG_IMMUTABLE

    fun clickPendingIntent(context: Context): PendingIntent {
        val clickIntent = Intent(context, MainActivity::class.java).apply {
            putExtra(DOWNLOAD_STATE, StopwatchState.Started.name)
        }
        return PendingIntent.getActivity(
            context, CLICK_REQUEST_CODE, clickIntent, flag
        )
    }

    fun stopPendingIntent(context: Context): PendingIntent {
        val stopIntent = Intent(context, MyService::class.java).apply {
            putExtra(DOWNLOAD_STATE, StopwatchState.Stopped.name)
        }
        return PendingIntent.getService(
            context, STOP_REQUEST_CODE, stopIntent, flag
        )
    }

    fun resumePendingIntent(context: Context): PendingIntent {
        val resumeIntent = Intent(context, MyService::class.java).apply {
            putExtra(DOWNLOAD_STATE, StopwatchState.Started.name)
        }
        return PendingIntent.getService(
            context, RESUME_REQUEST_CODE, resumeIntent, flag
        )
    }

    fun cancelPendingIntent(context: Context): PendingIntent {
        val cancelIntent = Intent(context, MyService::class.java).apply {
            putExtra(DOWNLOAD_STATE, StopwatchState.Canceled.name)
        }
        return PendingIntent.getService(
            context, CANCEL_REQUEST_CODE, cancelIntent, flag
        )
    }

    fun triggerForegroundService(context: Context, action: String) {
        Intent(context, MyService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }
}