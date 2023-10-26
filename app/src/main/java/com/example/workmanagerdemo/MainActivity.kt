package com.example.workmanagerdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.workmanagerdemo.databinding.ActivityMainBinding
import com.example.workmanagerdemo.worker.DownloadWorker

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDownld.setOnClickListener {
            startWorker()
        }

    }

    private fun startWorker() {
        val inputData = workDataOf(
            KEY_INPUT_URL to "https://file-examples.com/storage/feaade38c1651bd01984236/2017/04/file_example_MP4_192\n" +
                    "0_18MG.mp4",
            KEY_OUTPUT_FILE_NAME to "sample.mp4"
        )

        val downloadRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(this).enqueue(downloadRequest)

    }

    companion object {
        const val KEY_INPUT_URL = "KEY_INPUT_URL"
        const val KEY_OUTPUT_FILE_NAME = "KEY_OUTPUT_FILE_NAME"
        const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
        const val NOTIFICATION_CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME"
        const val CANCEL_DOWNLOAD = "CANCEL_DOWNLOAD"
        const val NOTIFICATION_ID = 10
    }


}

