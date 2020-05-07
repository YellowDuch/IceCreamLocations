package com.example.findmygolda.alerts

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.findmygolda.R
import com.example.findmygolda.database.AlertEntity
import java.text.SimpleDateFormat

@BindingAdapter("dateFormat")
fun TextView.setDateFormat(item: AlertEntity) {
    text = convertDate(item.time)
}

@BindingAdapter("checkMarakImage")
fun ImageView.setCheckMarkImage(item: AlertEntity) {
    setImageResource(when (item.isRead) {
        true -> R.drawable.check_mark_read
        false -> R.drawable.check_mark_not_read
    })
}

private fun convertDate(systemTime: Long): String {
    return SimpleDateFormat("HH:mm'\n'dd-MM-yy'\n'")
        .format(systemTime).toString()
}