package com.example.findmygolda.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.example.findmygolda.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_preferences, rootKey)

        val radiusPreference: SeekBarPreference? = findPreference("radiusFromBranch")
        radiusPreference?.apply {
            summary = this.value.times(100).toString() + getString(R.string.meters)

            setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = (newValue as Int).times(100).toString() + getString(R.string.meters)
                true
            }
        }

        val timePreference: SeekBarPreference? = findPreference("timeBetweenNotifications")
        timePreference?.apply {
            summary = this.value.times(5).toString() + getString(R.string.minutes)

            setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = (newValue as Int).times(5).toString() + getString(R.string.minutes)
                true
            }
        }
    }
}