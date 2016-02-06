package com.wanbok.monger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by Wanbok on 2016. 2. 7..
 */
class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val intent = Intent(context, MainService::class.java)
        context?.startService(intent)
    }
}