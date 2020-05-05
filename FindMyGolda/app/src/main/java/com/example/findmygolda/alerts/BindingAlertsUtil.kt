package com.example.findmygolda.alerts

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.findmygolda.database.AlertEntity
import java.text.SimpleDateFormat

@BindingAdapter("sleepDurationFormatted")
fun TextView.setSleepDurationFormatted(item: AlertEntity) {
    text = convertDate(item.time)
}

private fun convertDate(systemTime: Long): String {
    return SimpleDateFormat("HH:mm'\n'dd-MM-yy'\n'")
        .format(systemTime).toString()
}