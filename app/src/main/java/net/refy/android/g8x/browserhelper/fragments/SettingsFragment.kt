package net.refy.android.g8x.browserhelper.fragments

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import net.refy.android.g8x.browserhelper.R
import net.refy.android.g8x.browserhelper.utils.makeUrlIntent

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var sharedPreference: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(activity)
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<Preference>("choose_browser")?.setOnPreferenceClickListener {
            val (appNames, appPackages) = getBrowserApplications()
            var selectedIndex = appPackages.indexOf(sharedPreference.getString("choose_browser", "") ?: "")
            AlertDialog.Builder(activity)
                .setTitle(R.string.preference_choose_browser_title)
                .setSingleChoiceItems(appNames.toTypedArray(), selectedIndex) { _, i -> selectedIndex = i }
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    sharedPreference
                        .edit()
                        .putString("choose_browser", appPackages[selectedIndex])
                        .apply()
                    updateBrowserDescription()
                }
                .create()
                .show()
            true
        }
        findPreference<Preference>("browser_settings")?.setOnPreferenceClickListener {
            startActivity(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))
            true
        }
        updateBrowserDescription()
    }

    private fun updateBrowserDescription() {
        val (appNames, appPackages) = getBrowserApplications()
        val selectedIndex = appPackages.indexOf(sharedPreference.getString("choose_browser", "") ?: "")
        findPreference<Preference>("choose_browser")?.summary = when (selectedIndex) {
            -1 -> getString(R.string.browser_not_selected)
            else -> appNames[selectedIndex]
        }
    }

    private fun getBrowserApplications(): Pair<List<String>, List<String>> {
        val pm = requireActivity().packageManager
        val apps = pm.queryIntentActivities(makeUrlIntent(), PackageManager.MATCH_ALL)
            .filter { it.activityInfo.packageName != requireActivity().packageName }
        return Pair(
            apps.map { it.activityInfo.loadLabel(pm).toString() },
            apps.map { it.activityInfo.packageName }
        )
    }
}