package com.owino.intents_lab.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class DownloadService : Service() {
    private val binder: IBinder = Binder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url: String? = intent?.getStringExtra(URL_EXTRAS_KEY)
        val message: String = if (url == null) {
            "Failed to start download service, null url"
        } else {
            "Starting download service for $url"
        }
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        url?.let {
            download(url)
        }
        return START_STICKY
    }

    fun download(path: String): String {
        val url = URL(path)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.requestMethod = HttpMethod.GET.name
        val inputStream = connection.inputStream
        val byteBuffer = mutableListOf<Byte>()
        val  buffer = ByteArray(1024)
        var bytesRead = 0
        do {
            bytesRead = inputStream.read(buffer)
            if (bytesRead > 0){
                byteBuffer.addAll(buffer.asList().subList(0, bytesRead))
            }
        } while (bytesRead != -1)

        return byteBuffer.toByteArray().toString(Charset.defaultCharset())
    }

    enum class HttpMethod {
        GET, PUT, POST, DELETE
    }

    companion object {
        const val URL_EXTRAS_KEY = "service.url.extras"
    }
}