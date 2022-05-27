package net.refy.android.g8x.browserhelper.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.refy.android.g8x.browserhelper.R
import net.refy.android.g8x.browserhelper.databinding.ActivitySettingsBinding
import net.refy.android.g8x.browserhelper.fragments.SettingsFragment

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.settingsToolbar)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settingsContainer, SettingsFragment())
            .commit()
    }
}

