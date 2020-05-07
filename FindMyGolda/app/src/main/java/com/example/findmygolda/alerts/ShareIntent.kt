package com.example.findmygolda.alerts

import android.content.Intent

class ShareIntent {
    fun getShareIntent(description: String, title: String): Intent? {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "$description at $title")
            putExtra(Intent.EXTRA_TITLE, title)
            type = "text/plain"
        }

        return Intent.createChooser(sendIntent, null)
    }
}