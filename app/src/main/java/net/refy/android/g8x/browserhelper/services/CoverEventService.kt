package net.refy.android.g8x.browserhelper.services

import android.accessibilityservice.AccessibilityService
import android.hardware.display.ICoverDisplayEnabledCallback
import android.view.accessibility.AccessibilityEvent
import net.refy.android.g8x.browserhelper.utils.DisplayManagerExUtils
import net.refy.android.g8x.browserhelper.utils.setActivityEnabled

class CoverEventService : AccessibilityService() {
    companion object {
        val TAG = "g8x.browserhelper"
    }

    private val mCallback = object : ICoverDisplayEnabledCallback.Stub() {
        override fun onCoverDisplayEnabledChangedCallback(i: Int) = handleEvent(i)
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
    }

    override fun onInterrupt() {
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        // register handler
        val utils = DisplayManagerExUtils()
        utils.registerCoverDisplayEnabledCallback(TAG, mCallback)
        // sync current display state
        handleEvent(utils.getCoverDisplayState())
    }

    override fun onDestroy() {
        super.onDestroy()
        // unregister handler
        val utils = DisplayManagerExUtils()
        utils.unregisterCoverDisplayEnabledCallback(TAG)
        // enable activity
        handleEvent(ICoverDisplayEnabledCallback.STATE_ENABLED)
    }

    private fun handleEvent(i: Int) {
        setActivityEnabled(i == ICoverDisplayEnabledCallback.STATE_ENABLED)
    }
}