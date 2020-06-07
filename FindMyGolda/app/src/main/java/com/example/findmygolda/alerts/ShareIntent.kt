package com.example.findmygolda.alerts

import android.content.Intent
import com.example.findmygolda.Constants.Companion.SHARE_INTENT_TYPE

class ShareIntent {

    companion object{
        fun getShareIntent(description: String, title: String): Intent? {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "$description at $title")
                putExtra(Intent.EXTRA_TITLE, title)
                type = SHARE_INTENT_TYPE
            }

            return Intent.createChooser(sendIntent, null)
        }
    }

}