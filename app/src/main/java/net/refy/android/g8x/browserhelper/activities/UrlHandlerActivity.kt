package net.refy.android.g8x.browserhelper.activities

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import net.refy.android.g8x.browserhelper.utils.DisplayHelperUtils
import net.refy.android.g8x.browserhelper.utils.DisplayManagerExUtils

class UrlHandlerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val displayUtils = DisplayHelperUtils(this)
        val displayManager = DisplayManagerExUtils()
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val currentDisplayId = wm.defaultDisplay.displayId
        val coverDisplayId = displayUtils.getCoverDisplayId()
        val preferredDisplayId = when {
            currentDisplayId == coverDisplayId -> 0
            else -> coverDisplayId
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(intent.dataString))
        intent.setPackage("com.android.chrome")
        if(displayManager.isCoverEnabled()) {
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            val options = ActivityOptions.makeBasic()
            options.launchDisplayId = preferredDisplayId
            startActivity(intent, options.toBundle())
        }else{
            startActivity(intent)
        }
        finish()
    }
}