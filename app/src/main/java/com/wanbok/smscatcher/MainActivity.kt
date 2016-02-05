package com.wanbok.monger

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = baseContext?.getSharedPreferences("com.wanbok.monger.MONGER", Context.MODE_PRIVATE)

        val url = sharedPref?.getString("URL", "")
        val channel = sharedPref?.getString("CHANNEL", "")

        val urlText = findViewById(R.id.url) as? EditText
        val channelText = findViewById(R.id.channel) as? EditText

        urlText?.setText(url)
        channelText?.setText(channel)
    }

    fun onSave(button: View) {
        val editor = baseContext?.getSharedPreferences("com.wanbok.monger.MONGER", Context.MODE_PRIVATE)?.edit()

        val channelText = findViewById(R.id.channel) as? EditText
        val urlText = findViewById(R.id.url) as? EditText

        editor?.putString("URL", urlText?.text?.toString() ?: "")
        editor?.putString("CHANNEL", channelText?.text?.toString() ?: "")
    }
}
