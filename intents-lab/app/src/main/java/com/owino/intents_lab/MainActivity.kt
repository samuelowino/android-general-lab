package com.owino.intents_lab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.owino.intents_lab.services.DownloadService
import com.owino.intents_lab.services.DownloadService2

class MainActivity : AppCompatActivity() {
    private lateinit var launchActivityIntent: View
    private lateinit var launchServiceIntent: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        launchActivityIntent = findViewById(R.id.activity_transition_action_view)
        launchServiceIntent = findViewById(R.id.launch_service_action_view)
        launchActivityIntent.setOnClickListener {
            startActivity(Intent(this@MainActivity, SecondActivity::class.java))
            finish()
        }
        launchServiceIntent.setOnClickListener {
            val downloadIntent = Intent(this@MainActivity, DownloadService::class.java)
            downloadIntent.putExtra(DownloadService.URL_EXTRAS_KEY, "https://developer.android.com/develop/background-work/services")
            startService(downloadIntent)
        }
    }
}