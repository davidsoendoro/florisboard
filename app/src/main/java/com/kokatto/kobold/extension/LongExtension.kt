package com.kokatto.kobold.extension

import android.annotation.SuppressLint
import android.text.format.DateFormat.format
import java.lang.String.format
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun Long.toStringDate(outputFormat: String): String {
    val simpleDateFormat = SimpleDateFormat(outputFormat)
    return simpleDateFormat.format(this)
}
