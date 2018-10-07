package com.wanbok.monger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.ContactsContract
import android.telephony.SmsMessage
import android.util.Log
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail
import com.github.kittinunf.fuel.Fuel
import com.github.salomonbrys.kotson.jsonArray
import com.github.salomonbrys.kotson.jsonObject

/**
 * Created by wanbok on 16. 1. 30..
 */
class SMSReceiver : BroadcastReceiver() {
    val TAG = "SMSReceiver"
    val ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"
    val EXTRA_PUDS = "pdus"

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action?.equals(ACTION_SMS_RECEIVED) ?: false) {
            val bundle = intent?.extras
            val pdus = bundle?.get(EXTRA_PUDS) as? Array<Any>
            val messages = pdus?.map {
                SmsMessage.createFromPdu(it as? ByteArray, bundle?.getString("format"))
            }

            onSmsReceived(context, messages)
        }
    }

    private fun onSmsReceived(context: Context?, messages: List<SmsMessage>?) {
        messages?.forEach {
            Log.i(TAG, it.messageBody)
            Log.i(TAG, it.originatingAddress)
            val sharedPref = context?.getSharedPreferences(
                    "com.wanbok.monger.MONGER",
                    Context.MODE_PRIVATE
            )

            val name = getDisplayName(context, it.displayOriginatingAddress)
            val sender = if (name != null)
                name + " (" + (it.originatingAddress ?: "") + ")"
            else {
                it.displayOriginatingAddress ?: ""
            }

            val json = jsonObject(
                    "attachments" to jsonArray(
                            jsonObject(
                                    "footer" to sender,
                                    "text" to (it.messageBody ?: "")
                            )
                    )
            )

            val url = sharedPref?.getString("URL", null)
            if (url != null && url.count() > 0) {
                Fuel.post(url).body(json.toString())
                        .response { request, response, result ->
                            result.fold({ d ->
                                //do something with data
                            }, { err ->
                                //do something with error
                            })
                        }
            }

            val gmailUsername = sharedPref?.getString("GMAIL_USERNAME", null) ?: context?.resources?.getString(R.string.gmail_default_username)
            val gmailPassword = sharedPref?.getString("GMAIL_PASSWORD", null) ?: context?.resources?.getString(R.string.gmail_default_password)
            val email = sharedPref?.getString("EMAIL", null)
            if (email != null && email.count() > 0
                    && gmailUsername != null && gmailUsername.count() > 0
                    && gmailPassword != null && gmailPassword.count() > 0) {
                BackgroundMail.newBuilder(context)
                        .withUsername(gmailUsername)
                        .withPassword(gmailPassword)
                        .withMailto(email)
                        .withSubject("[Monger] SMS From : " + sender)
                        .withBody(it.messageBody)
                        .withOnSuccessCallback {
                            // do something
                        }
                        .withOnFailCallback {
                            // do something
                        }
                        .send()
            }
        }
    }

    private fun getDisplayName(context: Context?, number: String): String? {
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))

        val contentResolver = context?.contentResolver
        val contactLookup = contentResolver?.query(uri, arrayOf(BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME), null, null, null)

        try {
            if (contactLookup != null && contactLookup.count > 0) {
                contactLookup.moveToNext()
                return contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME))
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close()
            }
        }

        return null
    }
}