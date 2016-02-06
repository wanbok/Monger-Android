package com.wanbok.monger

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast

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

        Log.i("MainService", "Starting the Monger service.")
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(smsReceive)

        val intent = Intent("com.android.wanbok.permanent")
        sendBroadcast(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException()
    }
}