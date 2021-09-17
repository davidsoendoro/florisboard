package com.kokatto.kobold.utility

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class CurrencyUtility {
    companion object {
        @JvmStatic
        fun parseValueToIdr(value: Number?): String {
            if (value == null) return "Rp. 0"
            val x = when (value) {
                is Long -> NumberFormat.getNumberInstance(Locale.GERMAN).format(value)
                is Double -> NumberFormat.getNumberInstance(Locale.GERMAN).format(value)
                is Float -> NumberFormat.getNumberInstance(Locale.GERMAN).format(value.toDouble())
                else -> "$value"
            }
            return "Rp. $x"
        }

        fun parseValueToRbAbreviation(beforeFormat: Number?): String {
            val numberFormat = DecimalFormat("#,###")
            return numberFormat.format(beforeFormat).replace(",000", "rb")
        }

        fun currencyFormatter(beforeFormat: Float = 0F): String {
                val numberFormat = DecimalFormat("#,###")
                return numberFormat.format(beforeFormat).replace(",", ".")
        }
    }
}
