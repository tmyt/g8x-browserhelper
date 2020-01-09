package net.refy.android.g8x.browserhelper.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import net.refy.android.g8x.browserhelper.activities.UrlHandlerActivity

fun Context.setActivityEnabled(enabled: Boolean) {
    this.packageManager.setComponentEnabledSetting(
        ComponentName(this, UrlHandlerActivity::class.java),
        if (enabled) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP
    )
}
