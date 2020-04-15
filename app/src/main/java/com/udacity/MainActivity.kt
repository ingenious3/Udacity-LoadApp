package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.app.NotificationChannel
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var downloadManager: DownloadManager
    private val query = DownloadManager.Query()

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent

    private var fileName: String? = null


    companion object {

        private const val CHANNEL_ID = "channelId"

        private const val UDACITY_PROJECT_NAME = "Udacity Project - Load App"
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"

        private const val GLIDE_NAME = "Glide"
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"

        private const val RETROFIT_NAME = "Retrofit"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/master.zip"

        const val EXTRA_DOWNLOADED_FILE_NAME = "extra_downloaded_file_name"
        const val EXTRA_DOWNLOADED_FILE_STATUS =  "extra_downloaded_file_status"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"

        const val NOTIFICATION_ID =  1
        private const val REQUEST_CODE = 100

        const val DEFAULT_CIRCLE_RADIUS = 32f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        custom_button.setOnClickListener {
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if(id == downloadID) {
                custom_button.setDownloadButtonState(ButtonState.Completed)
                createNotification()
            }
        }
    }

    private fun download() {
        val url = getURL()
        if (url != null) {
            custom_button.setDownloadButtonState(ButtonState.Clicked)
            val request =
                DownloadManager.Request(Uri.parse(url))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
            notificationManager.cancel(NOTIFICATION_ID)
            custom_button.setDownloadButtonState(ButtonState.Loading)
            query.setFilterById(downloadID)
        }
    }

    private fun getURL(): String? {
        var url:String? = null
        fileName = null

        when(download_radio_group.checkedRadioButtonId){
            glideRadioButton.id->{
                url = GLIDE_URL
                fileName = GLIDE_NAME
            }
            loadAppRadioButton.id-> {
                url = LOAD_APP_URL
                fileName = UDACITY_PROJECT_NAME
            }
            retrofitRadioButton.id-> {
                url = RETROFIT_URL
                fileName = RETROFIT_NAME
            }
            else -> {
                Toast.makeText(this, getString(R.string.select_file_prompt), Toast.LENGTH_SHORT).show()
            }
        }
        return url
    }

    private fun createNotification(){

        val openDetailsIntent = Intent(this, DetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_DOWNLOADED_FILE_NAME, fileName)
            putExtra(EXTRA_DOWNLOADED_FILE_STATUS, getString(R.string.file_downloaded))
            putExtra(EXTRA_NOTIFICATION_ID, NOTIFICATION_ID)
        }

        pendingIntent = PendingIntent.getActivity(
            this, REQUEST_CODE, openDetailsIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_cloud_download)
            .setContentTitle(resources.getString(R.string.notification_title))
            .setContentText(getString(R.string.file_downloaded))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.ic_cloud_download, getString(R.string.notification_button), pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.notification_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register notification channel
            notificationManager.createNotificationChannel(channel)
        }

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

}
