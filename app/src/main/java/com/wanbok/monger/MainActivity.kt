package com.wanbok.monger

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onLoad(null)
    }

    fun openSlackSite(button: View?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse("https://my.slack.com/services/new/incoming-webhook/"))
        startActivity(intent)
    }

    fun onLoad(button: View?) {
        val sharedPref = getSharedPreferences("com.wanbok.monger.MONGER", Context.MODE_PRIVATE)

        val url = sharedPref?.getString("URL", "")
        val urlText = findViewById(R.id.url) as? EditText
        urlText?.setText(url)
        Log.i(TAG, "Load, URL : " + url)
    }

    fun onSave(button: View?) {
        val editor = getSharedPreferences("com.wanbok.monger.MONGER", Context.MODE_PRIVATE)?.edit()

        val urlText = findViewById(R.id.url) as? EditText
        val url = urlText?.text?.toString() ?: ""
        editor?.putString("URL", url)
        editor?.commit()

        Log.i(TAG, "Save, URL : " + url)
        Toast.makeText(this, "Saved Incoming WebHook!", Toast.LENGTH_SHORT).show()
    }
}
