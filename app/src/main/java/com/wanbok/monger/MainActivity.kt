package com.wanbok.monger

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
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

        val intent = Intent(applicationContext, MainService::class.java)
        applicationContext?.startService(intent)

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

        val email = sharedPref?.getString("EMAIL", "")
        val emailText = findViewById(R.id.email) as? EditText
        emailText?.setText(email)

        Log.i(TAG, "Load, URL : " + url + ", EMAIL : " + email)
    }

    fun onSave(button: View?) {
        val editor = getSharedPreferences("com.wanbok.monger.MONGER", Context.MODE_PRIVATE)?.edit()

        // url
        val urlText = findViewById(R.id.url) as? EditText
        val url = urlText?.text?.toString()
        val isValidURL = validateURL(url)
        if (!isValidURL) { urlText?.setError("Invalid Incoming Webhook URL") }
        editor?.putString("URL", if (isValidURL) url else { null })

        // email
        val emailText = findViewById(R.id.email) as? EditText
        val email = emailText?.text?.toString()
        val isValidEmail = validateEmail(email)
        if (!isValidEmail) { emailText?.setError("Invalid Email") }
        editor?.putString("EMAIL", if (isValidEmail) email else { null })

        // commit SharedPreferences
        editor?.commit()

        // Notice message
        var message = if (validateURL(url)) " 'Incomming WebHook URL'" else { "" }
        message += if (validateEmail(email)) " 'Email'" else { "" }
        message = if (TextUtils.isEmpty(message)) "No data." else { "Save" + message }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.i(TAG, message)
    }

    fun isExist(string: String?): Boolean {
        return string != null && !TextUtils.isEmpty(string)
    }

    fun validateURL(url: String?): Boolean {
        return isExist(url) && android.util.Patterns.WEB_URL.matcher(url).matches()
    }

    fun validateEmail(email: String?): Boolean {
        if (email?.contains(",") == true) {
            return isExist(email)
                    && (
                    email?.
                    split(",")?.
                    map { it.replace(" ", "") }?.
                    map { android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches() }?.
                    reduce { a, b -> a && b } == true
                    )
        } else {
            return isExist(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

    fun getToastMessage(hasIncomingWebhook: Boolean, hasEmail: Boolean) {

    }
}
