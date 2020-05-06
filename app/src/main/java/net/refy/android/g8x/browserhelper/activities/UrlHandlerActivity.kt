package net.refy.android.g8x.browserhelper.activities

import android.app.Activity
import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import net.refy.android.g8x.browserhelper.R
import net.refy.android.g8x.browserhelper.receivers.ChosenComponentReceiver
import net.refy.android.g8x.browserhelper.utils.*

class UrlHandlerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri = Uri.parse(intent.dataString)
        val targetPackageName = lookupPackage(getPreferredPackageName())
        // if not package selected, open chooser always
        if (targetPackageName == null) {
            openBrowserChooser(uri)
        }
        // if cover enabled, launch browser in other side
        else if (DisplayManagerExUtils().isCoverEnabled()) {
            openBrowser(targetPackageName, uri)
        }
        // if cover disabled, launch browser or transfer custom tabs intent
        else {
            val activityClass = lookupActivityClass(targetPackageName)
            if (activityClass == null) {
                openBrowserChooser(uri)
            } else {
                transferCustomTabs(targetPackageName, activityClass)
            }
        }
        finish()
    }

    private fun transferCustomTabs(packageName: String, activityClass: String){
        intent.setClassName(packageName, activityClass)
        startActivity(intent)
    }

    private fun openBrowser(packageName: String, uri: Uri) {
        val (intent, bundle) = makeBrowserIntent(uri)
        intent.setPackage(packageName)
        startActivity(intent, bundle)
    }

    private fun openBrowserChooser(uri: Uri) {
        val (intent, bundle) = makeBrowserIntent(uri)
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

    private fun makeBrowserIntent(uri: Uri): Pair<Intent, Bundle> {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        val bundle = ActivityOptions.makeBasic()
        if (DisplayManagerExUtils().isCoverEnabled()) {
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            bundle.launchDisplayId = lookupPreferredDisplayId()
        }
        return intent to bundle.toBundle()
    }
}