package com.example.workmanagerdemo.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.workmanagerdemo.MainActivity.Companion.CANCEL_DOWNLOAD
import com.example.workmanagerdemo.MainActivity.Companion.KEY_INPUT_URL
import com.example.workmanagerdemo.MainActivity.Companion.KEY_OUTPUT_FILE_NAME
import com.example.workmanagerdemo.MainActivity.Companion.NOTIFICATION_CHANNEL_ID
import com.example.workmanagerdemo.MainActivity.Companion.NOTIFICATION_CHANNEL_NAME
import com.example.workmanagerdemo.MainActivity.Companion.NOTIFICATION_ID
import com.example.workmanagerdemo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.net.URL
import java.util.UUID
import javax.inject.Inject

class DownloadWorker @Inject constructor(
    context: Context,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {



    override suspend fun doWork(): Result {
        Log.d("DownloadWorker", "doWork() -> called")
        val inputUrl = inputData.getString(KEY_INPUT_URL) ?: return Result.failure()
        val outputFile = inputData.getString(KEY_OUTPUT_FILE_NAME) ?: return Result.failure()
        //start download
        val result = downloadFile(inputUrl, outputFile)
        return if (result) {
            Log.d("DownloadWorker", "doWork() -> success")
            Result.success()
        } else {
            Log.d("DownloadWorker", "doWork() -> failure")
            Result.failure()
        }
    }

    private suspend fun downloadFile(inputUrl: String, outputFile: String): Boolean {
        try {
            val url = URL(inputUrl)
            val connection = withContext(Dispatchers.IO) { url.openConnection() }
            val contentLength = connection.contentLength
            val inputStream = BufferedInputStream(withContext(Dispatchers.IO) { url.openStream() })
            val outputStream = withContext(Dispatchers.IO) { FileOutputStream(outputFile) }
            val data = ByteArray(1024)
            var totalBytesRead = 0
            var bytesRead: Int

            while (withContext(Dispatchers.IO) { inputStream.read(data) }.also {
                    bytesRead = it
                } != -1) {
                withContext(Dispatchers.IO) { outputStream.write(data, 0, bytesRead) }
                totalBytesRead += bytesRead

                // Calculate download progress as a percentage
                val progress = (totalBytesRead * 100 / contentLength)

                // Update the notification progress
                updateNotificationProgress(progress)
            }

            withContext(Dispatchers.IO) { inputStream.close() }
            withContext(Dispatchers.IO) { outputStream.close() }
            return true
        } catch (e: Exception) {
            Log.d("DownloadWorker", "downloadFile() -> $e")
            return false
        }
    }

    private suspend fun updateNotificationProgress(progress: Int) {
        // Update the notification with the download progress
        val notification = createForegroundInfo("Download Progress: $progress%").toString()
        setForeground(createForegroundInfo(notification))
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val id = NOTIFICATION_CHANNEL_ID
        val uuid = UUID.randomUUID()
        val title = NOTIFICATION_CHANNEL_NAME
        val cancel = CANCEL_DOWNLOAD
        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(uuid)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.outline_file_download_24)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


}






