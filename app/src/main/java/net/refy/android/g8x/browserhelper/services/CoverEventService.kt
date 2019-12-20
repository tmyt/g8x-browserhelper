package net.refy.android.g8x.browserhelper.services

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.ICoverDisplayEnabledCallback
import android.os.IBinder
import net.refy.android.g8x.browserhelper.activities.UrlHandlerActivity
import net.refy.android.g8x.browserhelper.utils.DisplayManagerExUtils

class CoverEventService : Service() {
    companion object {
        val TAG = "g8x.browserhelper"
    }

    private val mCallback = object : ICoverDisplayEnabledCallback.Stub() {
        override fun onCoverDisplayEnabledChangedCallback(i: Int) = handleEvent(i)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val utils = DisplayManagerExUtils()
        utils.registerCoverDisplayEnabledCallback(TAG, mCallback)
        handleEvent(utils.getCoverDisplayState())
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        val utils = DisplayManagerExUtils()
        utils.unregisterCoverDisplayEnabledCallback(TAG)
    }

    private fun handleEvent(i: Int) {
        val cn = ComponentName(this, UrlHandlerActivity::class.java)
        val state = when (i) {
            ICoverDisplayEnabledCallback.STATE_ENABLED -> PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            else -> PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }
        packageManager.setComponentEnabledSetting(cn, state, PackageManager.DONT_KILL_APP)
    }
}