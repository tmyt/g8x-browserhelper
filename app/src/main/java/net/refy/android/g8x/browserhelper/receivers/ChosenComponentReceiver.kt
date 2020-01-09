package net.refy.android.g8x.browserhelper.receivers

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager

class ChosenComponentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras
        val componentName = bundle?.get(Intent.EXTRA_CHOSEN_COMPONENT)
        if (componentName is ComponentName) {
            putPreference(context!!, componentName.packageName)
        }
    }

    private fun putPreference(context: Context, packageName: String) {
        val preference = PreferenceManager.getDefaultSharedPreferences(context)
        preference.edit().putString("choose_browser", packageName).apply()
    }
}