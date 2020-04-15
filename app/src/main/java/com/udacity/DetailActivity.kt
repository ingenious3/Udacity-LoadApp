package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.app.NotificationManager
import android.content.Context
import androidx.constraintlayout.motion.widget.MotionLayout
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent

        val fileName = intent.getStringExtra(MainActivity.EXTRA_DOWNLOADED_FILE_NAME)
        val downloadStatus = intent.getStringExtra(MainActivity.EXTRA_DOWNLOADED_FILE_STATUS)
        val notificationId = intent.getIntExtra(MainActivity.EXTRA_NOTIFICATION_ID, MainActivity.NOTIFICATION_ID)

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)

        if(fileName != null){
            textview_filename.text = fileName
        }
        if(downloadStatus != null) {
            textview_status.text = downloadStatus
        }

        detailsLayout.transitionToStart()

        end_button.setOnClickListener {
            detailsLayout.transitionToStart()
            detailsLayout.setTransitionListener(object: MotionLayout.TransitionListener {
                override fun onTransitionTrigger(
                    p0: MotionLayout?,
                    p1: Int,
                    p2: Boolean,
                    p3: Float
                ) {

                }

                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

                override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                    if (p1 == detailsLayout.endState)
                        finish()
                }
            })
        }
        detailsLayout.transitionToEnd()
    }
}
