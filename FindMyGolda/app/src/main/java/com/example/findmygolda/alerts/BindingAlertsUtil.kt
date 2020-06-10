package com.example.findmygolda.alerts

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.findmygolda.Constants
import com.example.findmygolda.Constants.Companion.DATE_FORMAT
import java.text.SimpleDateFormat

@BindingAdapter("dateFormat")
fun TextView.setDateFormat(time: Long) {
    text = SimpleDateFormat(DATE_FORMAT)
        .format(time).toString()
}
