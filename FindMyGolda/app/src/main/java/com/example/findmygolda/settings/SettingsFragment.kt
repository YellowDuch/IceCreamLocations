package com.example.findmygolda.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.example.findmygolda.Constants.Companion.HUNDREDS_METERS
import com.example.findmygolda.Constants.Companion.SIZE_OF_JUMP
import com.example.findmygolda.Constants.Companion.PREFERENCE_RADIUS_FROM_BRANCH
import com.example.findmygolda.Constants.Companion.PREFERENCE_TIME_BETWEEN_NOTIFICATIONS
import com.example.findmygolda.R
import com.example.findmygolda.alerts.AlertManager
import com.example.findmygolda.alerts.parseMinutesToMilliseconds

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val alertManager = context?.let { AlertManager.getInstance(it) }
        setPreferencesFromResource(R.xml.app_preferences, rootKey)
        val radiusPreference: SeekBarPreference? = findPreference(PREFERENCE_RADIUS_FROM_BRANCH)
        radiusPreference?.apply {
            summary = this.value.times(HUNDREDS_METERS).toString() + getString(R.string.meters)

            setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = (newValue as Int).times(HUNDREDS_METERS).toString() + getString(R.string.meters)
                if (alertManager != null) {
                    alertManager.maxDistanceFromBranch = (newValue.times(HUNDREDS_METERS))
                }
                true
            }
        }

        val timePreference: SeekBarPreference? = findPreference(PREFERENCE_TIME_BETWEEN_NOTIFICATIONS)
        timePreference?.apply {
            summary = this.value.times(SIZE_OF_JUMP).toString() + getString(R.string.minutes)

            setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = (newValue as Int).times(SIZE_OF_JUMP).toString() + getString(R.string.minutes)
                if (alertManager != null) {
                    alertManager.intervalBetweenIdenticalNotifications = parseMinutesToMilliseconds(newValue.times(SIZE_OF_JUMP))
                }
                true
            }
        }
    }
}