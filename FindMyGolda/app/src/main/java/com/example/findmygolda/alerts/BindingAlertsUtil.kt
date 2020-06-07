package com.example.findmygolda.alerts

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.findmygolda.Constants
import com.example.findmygolda.Constants.Companion.DATE_FORMAT
import java.text.SimpleDateFormat

@BindingAdapter("dateFormat")
fun TextView.setDateFormat(time: Long) {
    text = convertDate(time)
}

private fun convertDate(systemTime: Long): String {
    return SimpleDateFormat(DATE_FORMAT)
        .format(systemTime).toString()
}

fun parseMinutesToMilliseconds(minutes : Int) : Long{
    return (minutes * Constants.MINUTES_TO_MILLISECONDS).toLong()
}
