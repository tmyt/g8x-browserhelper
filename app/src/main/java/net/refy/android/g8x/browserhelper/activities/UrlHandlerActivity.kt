package net.refy.android.g8x.browserhelper.activities

import android.app.Activity
import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.WindowManager
import net.refy.android.g8x.browserhelper.R
import net.refy.android.g8x.browserhelper.receivers.ChosenComponentReceiver
import net.refy.android.g8x.browserhelper.utils.DisplayHelperUtils
import net.refy.android.g8x.browserhelper.utils.DisplayManagerExUtils

class UrlHandlerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val currentDisplayId = wm.defaultDisplay.displayId
        val coverDisplayId = DisplayHelperUtils(this).getCoverDisplayId()
        val preferredDisplayId = when (currentDisplayId) {
            coverDisplayId -> 0
            else -> coverDisplayId
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(intent.dataString))
        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        val packageName = preference.getString("choose_browser", "")
        if (!packageName.isNullOrEmpty()) {
            intent.setPackage(packageName)
        }
        var bundle: Bundle? = null
        if (DisplayManagerExUtils().isCoverEnabled()) {
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            bundle = ActivityOptions.makeBasic().apply {
                launchDisplayId = preferredDisplayId
            }.toBundle()
        }
        openBrowser(intent, bundle)
        finish()
    }

    private fun openBrowser(intent: Intent, bundle: Bundle?) {
        try {
            startActivity(intent, bundle)
        } catch (e: ActivityNotFoundException) {
            if (intent.getPackage().isNullOrEmpty()) return
            intent.setPackage(null)
            openBrowserChooser(intent, bundle)
        }
    }

    private fun openBrowserChooser(intent: Intent, bundle: Bundle?) {
        val receiver = Intent(this, ChosenComponentReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT)
        val chooser = Intent.createChooser(
            intent,
            getString(R.string.preference_choose_browser_title),
            pendingIntent.intentSender
        )
        val excludeComponent = ComponentName(this, UrlHandlerActivity::class.java)
        val activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        chooser.putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, arrayOf(excludeComponent))
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, activities.filter {
            it.activityInfo.packageName != packageName
        }.map {
            Intent(intent).apply {
                setPackage(it.activityInfo.packageName)
                flags = intent.flags
            }
        }.toTypedArray())
        startActivity(chooser, bundle)
    }
}