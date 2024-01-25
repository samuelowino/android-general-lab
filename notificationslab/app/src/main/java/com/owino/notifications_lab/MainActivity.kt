package com.owino.notifications_lab

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
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
    private lateinit var mediaNotificationChip: Chip
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        normalNotificationChip = findViewById(R.id.normal_notification)
        actionNotificationChip = findViewById(R.id.normal_notification_with_action)
        mediaNotificationChip = findViewById(R.id.media_notification_with_action)
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
        mediaNotificationChip.setOnClickListener {
            if (notificationPermissionCheck()) {
                createNotificationChannel()
                showMediaNotification()
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
    private fun showMediaNotification() {
        val title = resources.getString(R.string.marketing_banner)
        val description = resources.getString(R.string._50_discount_available_now)
        NotificationManagerCompat.from(applicationContext)
            .notify(6665522, mediaNotification(title, description))
    }

    private fun mediaNotification(title: String, description: String): Notification {
        val intent = Intent(applicationContext, MainActivity::class.java)
        val notificationRequestCode = 111222
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationRequestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.ic_action_media)
            .setColorized(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(notificationMediaStyle())
            .setContentIntent(pendingIntent)
            .build()

    }

    private fun notificationMediaStyle(): NotificationCompat.Style {
        return androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession())
    }

    private fun mediaSession(): MediaSessionCompat.Token? {
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Noisy Crowd AMR")
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Unknown Artist")
            .putBitmap(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                BitmapFactory.decodeResource(resources, R.drawable.graphic_large)
            )
            .build()

        val playbackState = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
            .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
            .build()


        val player = MediaPlayer.create(applicationContext, R.raw.crowd_talking)
        val sessionToken = "secret-session-token"
        val session = MediaSessionCompat(applicationContext, sessionToken)
        session.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                if (!player.isPlaying) {
                    player.start()
                }
            }

            override fun onPause() {
                if (!player.isPlaying) {
                    player.pause()
                }
            }

            override fun onFastForward() {
                if (!player.isPlaying) {
                    player.start()
                }
            }

            override fun onRewind() {
                if (!player.isPlaying) {
                    player.start()
                }
            }

            override fun onStop() {
                if (!player.isPlaying) {
                    player.stop()
                }
            }
        })

        session.setMetadata(metadata)
        session.setPlaybackState(playbackState)

        return session.sessionToken
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
        val launchPendingIntent = PendingIntent.getActivity(
            applicationContext, 0, launchIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val intent = Intent(applicationContext, ImportantBroadcastReceiver::class.java)
        intent.putExtra("ACTION_TYPE", "CANCEL_ACTION")
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