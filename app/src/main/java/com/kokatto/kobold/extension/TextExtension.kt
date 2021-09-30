package com.kokatto.kobold.extension

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

fun String.toThousandSeperatedString(suffix: String): String? {
    val beforeFormat = this.toLong()
    var afterFormat = ""

    try {
        val formatter: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("#,###,###,###")
        afterFormat = formatter.format(beforeFormat).replace(",", ".")

    } catch (nfe: java.lang.NumberFormatException) {
        nfe.printStackTrace()
    }

    return suffix + afterFormat
}

fun EditText.setThousandSeparator() {
    Log.e("edittext", "in")
    this.addTextChangedListener(thousandSeparator(this))
}

fun thousandSeparator(editText: EditText): TextWatcher {
    return object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            editText.removeTextChangedListener(this)
            try {
                var originalString = s.toString()
                val longval: Long
                if (originalString.contains(".")) {
                    originalString = originalString.replace(".", "")
                }
                longval = originalString.toLong()
                val formatter: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
                formatter.applyPattern("#,###,###,###")
                val formattedString: String = formatter.format(longval).replace(",", ".")

                //setting text after format to EditText
                editText.setText(formattedString)
                editText.setSelection(editText.text.length)
            } catch (nfe: NumberFormatException) {
                nfe.printStackTrace()
            }
            editText.addTextChangedListener(this)
        }
    }
}
