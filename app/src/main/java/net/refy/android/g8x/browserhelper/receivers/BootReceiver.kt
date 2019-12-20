package net.refy.android.g8x.browserhelper.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import net.refy.android.g8x.browserhelper.services.CoverEventService

class BootReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, p1: Intent?) {
        val syncState = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("syncstate", false)
        if(syncState){
            context?.startService(Intent(context.applicationContext, CoverEventService::class.java))
        }
    }
}