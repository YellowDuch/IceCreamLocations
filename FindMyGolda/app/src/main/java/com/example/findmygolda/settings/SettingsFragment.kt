package com.example.findmygolda.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.example.findmygolda.Constants.Companion.HUNDREDS_METERS
import com.example.findmygolda.Constants.Companion.JUMPS_OF_5_MINUTES
import com.example.findmygolda.Constants.Companion.PREFERENCE_RADIUS_FROM_BRANCH
import com.example.findmygolda.Constants.Companion.PREFERENCE_TIME_BETWEEN_NOTIFICATIONS
import com.example.findmygolda.R
import com.example.findmygolda.alerts.AlertManager
import com.example.findmygolda.parseMinutesToMilliseconds

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val alertManager = context?.let { AlertManager.getInstance(it) }
        setPreferencesFromResource(R.xml.app_preferences, rootKey)
        val radiusPreference: SeekBarPreference? = findPreference(PREFERENCE_RADIUS_FROM_BRANCH)
        radiusPreference?.apply {
            summary = this.value.times(HUNDREDS_METERS).toString() + getString(R.string.meters)

            setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = (newValue as Int).times(HUNDREDS_METERS).toString() + getString(R.string.meters)
                alertManager?.apply {
                    maxDistanceFromBranch = newValue.times(HUNDREDS_METERS)
                }
                true
            }
        }

        val timePreference: SeekBarPreference? = findPreference(PREFERENCE_TIME_BETWEEN_NOTIFICATIONS)
        timePreference?.apply {
            summary = this.value.times(JUMPS_OF_5_MINUTES).toString() + getString(R.string.minutes)

            setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = (newValue as Int).times(JUMPS_OF_5_MINUTES).toString() + getString(R.string.minutes)
                alertManager?.apply {
                    intervalBetweenIdenticalNotifications = parseMinutesToMilliseconds(newValue.times(JUMPS_OF_5_MINUTES))
                }
                true
            }
        }
    }
}