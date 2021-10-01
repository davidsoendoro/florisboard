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
            return "Rp$x"
        }

        fun parseValueToRbAbreviation(beforeFormat: Number): String {
            if (beforeFormat==0)
                return "Rp-"
            val numberFormat = DecimalFormat("#,###")
            return numberFormat.format(roundToHundreds(beforeFormat.toInt())).replace(",000", "rb")
        }

        fun currencyFormatter(beforeFormat: Number): String {
                val numberFormat = DecimalFormat("#,###")
                return "Rp"+numberFormat.format(beforeFormat).replace(",", ".")
        }

        fun currencyFormatterNoPrepend(beforeFormat: Number): String {
            val numberFormat = DecimalFormat("#,###")
            return numberFormat.format(beforeFormat).replace(",", ".")
        }

        fun roundToHundreds(number: Int, multiple: Int = 100): Number {
            var result= number

            //If not already multiple of given number


            //If not already multiple of given number
            if (number % multiple !== 0) {
                val division: Int = number / multiple + 1
                result = division * multiple
            }

            return result
        }
    }
}
