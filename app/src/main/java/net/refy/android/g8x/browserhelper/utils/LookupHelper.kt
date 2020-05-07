package net.refy.android.g8x.browserhelper.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.WindowManager
import androidx.preference.PreferenceManager

class CustomIntent {
    companion object {
        const val ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService"
    }
}

fun makeUrlIntent(): Intent{
    return Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com/"))
}

fun Context.getPreferredPackageName(): String? {
    val preference = PreferenceManager.getDefaultSharedPreferences(this)
    return preference.getString("choose_browser", "")
}

fun Context.lookupPackage(packageName: String?): String? {
    return if (packageName.isNullOrEmpty()) {
        val resolved = packageManager.queryIntentActivities(makeUrlIntent(), 0)
        val packageNames = resolved.map { it.activityInfo.packageName }
        resolvePackagesSupportingCustomTabs(packageNames).firstOrNull()
    } else {
        resolvePackagesSupportingCustomTabs(listOf(packageName)).firstOrNull()
    }
}

fun Context.lookupActivityClass(packageName: String): String? {
    return resolveActivityClass(packageName)
}

fun Context.lookupServiceClass(packageName: String): String? {
    return resolveServiceClass(packageName)
}

fun Context.lookupPreferredDisplayId(): Int{
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val currentDisplayId = wm.defaultDisplay.displayId
    val coverDisplayId = DisplayHelperUtils(this).getCoverDisplayId()
    return when (currentDisplayId) {
        coverDisplayId -> 0
        else -> coverDisplayId
    }
}

//
// Private functions
//
private fun Context.resolvePackagesSupportingCustomTabs(packageNames: List<String>): List<String> {
    val resolvedPackages = ArrayList<String>()
    for (packageName in packageNames) {
        if (resolveServiceClass(packageName) == null) continue
        resolvedPackages.add(packageName)
    }
    return resolvedPackages
}

private fun Context.resolveActivityClass(packageName: String): String? {
    val activityIntent = makeUrlIntent()
    activityIntent.setPackage(packageName)
    return packageManager.resolveActivity(activityIntent, 0)?.activityInfo?.name
}

private fun Context.resolveServiceClass(packageName: String): String? {
    val serviceIntent = Intent()
    serviceIntent.setAction(CustomIntent.ACTION_CUSTOM_TABS_CONNECTION)
    serviceIntent.setPackage(packageName)
    return packageManager.resolveService(serviceIntent, 0)?.serviceInfo?.name
}