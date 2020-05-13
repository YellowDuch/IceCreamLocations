package com.example.findmygolda.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.example.findmygolda.Constants.Companion.PREFERENCE_RADIUS_FROM_BRANCH
import com.example.findmygolda.Constants.Companion.PREFERENCE_TIME_BETWEEN_NOTIFICATIONS
import com.example.findmygolda.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_preferences, rootKey)

        val radiusPreference: SeekBarPreference? = findPreference(PREFERENCE_RADIUS_FROM_BRANCH)
        radiusPreference?.apply {
            summary = this.value.times(100).toString() + getString(R.string.meters)

            setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = (newValue as Int).times(100).toString() + getString(R.string.meters)
                true
            }
        }

        val timePreference: SeekBarPreference? = findPreference(PREFERENCE_TIME_BETWEEN_NOTIFICATIONS)
        timePreference?.apply {
            summary = this.value.times(5).toString() + getString(R.string.minutes)

            setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = (newValue as Int).times(5).toString() + getString(R.string.minutes)
                true
            }
        }
    }
}