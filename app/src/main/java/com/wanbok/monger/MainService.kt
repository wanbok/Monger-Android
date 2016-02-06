package com.wanbok.monger

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder

/**
 * Created by Wanbok on 2016. 2. 6..
 */
class MainService: Service() {
    val smsReceive = SMSReceiver()

    override fun onCreate() {
        super.onCreate()

        //SMS event receiver
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(smsReceive, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(smsReceive)
    }

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException()
    }
}