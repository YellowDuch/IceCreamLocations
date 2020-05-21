package com.example.findmygolda.alerts

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.findmygolda.Constants
import java.text.SimpleDateFormat

@BindingAdapter("dateFormat")
fun TextView.setDateFormat(time: Long) {
    text = convertDate(time)
}

private fun convertDate(systemTime: Long): String {
    return SimpleDateFormat("HH:mm dd-MM-yy")
        .format(systemTime).toString()
}

fun parseMinutesToMilliseconds(minutes : Int) : Long{
    return (minutes * Constants.MINUTES_TO_MILLISECONDS).toLong()
}
