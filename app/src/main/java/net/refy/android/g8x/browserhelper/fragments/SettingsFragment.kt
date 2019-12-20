package net.refy.android.g8x.browserhelper.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import net.refy.android.g8x.browserhelper.R
import net.refy.android.g8x.browserhelper.services.CoverEventService

class SettingsFragment : PreferenceFragmentCompat() {
    var sharedPreferences: SharedPreferences? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val switch = findPreference<SwitchPreferenceCompat>("syncstate")
        switch?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            setAutomaticMode(newValue as Boolean)
            true
        }
    }

    private fun setAutomaticMode(enabled : Boolean){
        val intent = Intent(activity?.applicationContext, CoverEventService::class.java)
        when (enabled) {
            true -> activity?.startService(intent)
            else -> activity?.stopService(intent)
        }
    }
}