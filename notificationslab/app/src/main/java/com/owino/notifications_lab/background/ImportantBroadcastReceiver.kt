package com.owino.notifications_lab.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class ImportantBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("TAG", "ImportantBroadcastReceiver onReceive")
        Log.e("TAG", "ImportantBroadcastReceiver action ${intent?.getStringExtra("ACTION_TYPE")}")
        if (intent?.getStringExtra("ACTION_TYPE").equals("CANCEL_ACTION")) {
            Toast.makeText(context, "Important broadcast received", Toast.LENGTH_LONG).show()
        }
    }
}