package com.owino.intents_lab

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class SecondActivity: AppCompatActivity() {
    private lateinit var exitActionView: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_activity_layout)
        exitActionView = findViewById(R.id.exit_hatch_view)

        exitActionView.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@SecondActivity, MainActivity::class.java))
        finish()
    }
}