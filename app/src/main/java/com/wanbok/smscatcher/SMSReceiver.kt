package com.wanbok.monger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.salomonbrys.kotson.jsonObject

/**
 * Created by wanbok on 16. 1. 30..
 */
class SMSReceiver: BroadcastReceiver() {
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

            onSmsReceived(messages)
        }
    }

    private fun onSmsReceived(messages: List<SmsMessage>?) {
        messages?.forEach {
            Log.i(TAG, it.messageBody)
            Log.i(TAG, it.originatingAddress)

            val json = jsonObject(
                    "response_type" to "in_channel",
                    "text" to (it.messageBody ?: "") + " :calling: _" + (it.originatingAddress ?: "")+"_"
            )
            Fuel.post("https://hooks.slack.com/services/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").body(json.toString())
                .response { request, response, result ->
                    result.fold({ d ->
                        //do something with data
                    }, { err ->
                        //do something with error
                    })
                }
        }
    }
}