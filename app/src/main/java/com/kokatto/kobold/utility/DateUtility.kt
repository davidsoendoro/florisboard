package com.kokatto.kobold.utility

import android.annotation.SuppressLint
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class DateUtility {
    companion object {
        @SuppressLint("SimpleDateFormat")
        @JvmStatic
        fun getCurrentDate(format: String = "dd-MM-yyyy"):String{
            val sdf = SimpleDateFormat(format)
            return sdf.format(Date())
        }
    }
}
