package com.example.findmygolda.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.example.findmygolda.Constants.Companion.JUMP_IN_METERS
import com.example.findmygolda.Constants.Companion.JUMP_IN_MINUTES
import com.example.findmygolda.Constants.Companion.RADIUS_FROM_BRANCH_PREFERENCE
import com.example.findmygolda.Constants.Companion.TIME_BETWEEN_NOTIFICATIONS_PREFERENCE
import com.example.findmygolda.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_preferences, rootKey)

        val radiusPreference: SeekBarPreference? = findPreference(RADIUS_FROM_BRANCH_PREFERENCE)
        radiusPreference?.apply {
            summary = this.value.times(JUMP_IN_METERS).toString() + getString(R.string.meters)

            setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = (newValue as Int).times(JUMP_IN_METERS).toString() + getString(R.string.meters)
                true
            }
        }

        val timePreference: SeekBarPreference? = findPreference(TIME_BETWEEN_NOTIFICATIONS_PREFERENCE)
        timePreference?.apply {
            summary = this.value.times(JUMP_IN_MINUTES).toString() + getString(R.string.minutes)

            setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = (newValue as Int).times(JUMP_IN_MINUTES).toString() + getString(R.string.minutes)
                true
            }
        }
    }
}