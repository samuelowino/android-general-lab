package com.owino.notifications_lab

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.chip.Chip
import com.owino.notifications_lab.background.ImportantBroadcastReceiver

class MainActivity : AppCompatActivity() {
    private lateinit var normalNotificationChip: Chip
    private lateinit var actionNotificationChip: Chip
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        normalNotificationChip = findViewById(R.id.normal_notification)
        actionNotificationChip = findViewById(R.id.normal_notification_with_action)
        normalNotificationChip.setOnClickListener {
            if (notificationPermissionCheck()) {
                createNotificationChannel()
                showNormalNotification()
            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.missing_notification_permissions),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        actionNotificationChip.setOnClickListener {
            if (notificationPermissionCheck()) {
                createNotificationChannel()
                showNotificationWithAction()
            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.missing_notification_permissions),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotificationWithAction() {
        val notificationId = 778811
        NotificationManagerCompat.from(applicationContext)
            .notify(notificationId, actionNotification())
    }

    private fun actionNotification(): Notification {
        val title = resources.getString(R.string.wake_up_alarm)
        val message = resources.getString(R.string.it_s_8am_wake_up_now)
        val launchIntent = Intent(applicationContext, MainActivity::class.java)
        val launchPendingIntent = PendingIntent.getActivity(applicationContext, 0, launchIntent,
            PendingIntent.FLAG_IMMUTABLE)
        val intent = Intent(applicationContext, ImportantBroadcastReceiver::class.java)
        intent.putExtra("ACTION_TYPE","CANCEL_ACTION")
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext, ACTION_BROADCAST_RECEIVER_ID, intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_action_action)
            .addAction(
                R.drawable.ic_action_cancel,
                resources.getString(R.string.general_stop),
                pendingIntent
            )
            .setContentIntent(launchPendingIntent)
            .build()
    }

    private fun notificationPermissionCheck(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
            false
        }
    }

    private fun createNotificationChannel() {
        val manager = NotificationManagerCompat.from(applicationContext)
        /**
         * A notification channel is only required post Android 8
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(notificationChanel())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun notificationChanel(): NotificationChannel {
        val channelId = "af4ed4e3-6a00-4ddf-9fbd-bb5bc2497722"
        val importance = NotificationManager.IMPORTANCE_HIGH
        return NotificationChannel(channelId, NOTIFICATION_CHANNEL_NAME, importance)
    }

    @SuppressLint("MissingPermission")
    private fun showNormalNotification() {
        val notificationId = 51
        val title = resources.getString(R.string.happy_happy)
        val content = resources.getString(R.string.remind_to_be_happy)
        val notification = notification(title, content)
        NotificationManagerCompat.from(applicationContext).notify(notificationId, notification)
    }

    private fun notification(
        notificationContentTitle: String,
        notificationContent: String
    ): Notification {
        return NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(notificationContentTitle)
            .setContentText(notificationContent)
            .setSmallIcon(R.drawable.ic_action_name)
            .build()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            normalNotificationChip.callOnClick()
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "af4ed4e3-6a00-4ddf-9fbd-bb5bc2497722"
        const val NOTIFICATION_CHANNEL_NAME = "Normal Notifications Channel"
        const val NOTIFICATION_PERMISSION_REQUEST_CODE = 55544411
        const val ACTION_BROADCAST_RECEIVER_ID = 66773311
    }
}