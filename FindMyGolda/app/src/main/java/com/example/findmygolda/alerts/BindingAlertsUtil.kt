package com.example.findmygolda.alerts

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.findmygolda.Constants.Companion.SIMPLE_DATE_FORMAT

@BindingAdapter("dateFormat")
fun TextView.setDateFormat(time: Long) {
    text = SIMPLE_DATE_FORMAT.format(time).toString()
}
