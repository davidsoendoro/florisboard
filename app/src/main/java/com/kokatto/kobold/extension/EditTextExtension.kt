package com.kokatto.kobold.extension

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.lang.Exception
import java.text.DecimalFormat
import java.util.*

fun EditText.addSeparator(
    targetEdit: EditText?,
    thousand: String = ",",
    decimal: String = "."
) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            try {
                targetEdit!!.removeTextChangedListener(this)
                val value = targetEdit.text.toString()
                if (value != "") {
                    if (value.startsWith(decimal)) {
                        targetEdit.setText("0${decimal}")
                    }
                    if (value.startsWith("0") && !value.startsWith("0${decimal}")) {
                        targetEdit.setText("")
                    }
                    val str = targetEdit.text.toString().replace(thousand, "")
                    if (value != "") targetEdit.setText(getDecimalFormattedString(str, thousand, decimal))
                    targetEdit.setSelection(targetEdit.text.toString().length)
                }
                targetEdit.addTextChangedListener(this)
                return
            } catch (ex: Exception) {
                ex.printStackTrace()
                targetEdit!!.addTextChangedListener(this)
            }
        }

    })
}

fun getDecimalFormattedString(value: String, thousand: String = ",", decimal: String = "."): String? {
    val lst = StringTokenizer(value, decimal)
    var str1 = value
    var str2 = ""
    if (lst.countTokens() > 1) {
        str1 = lst.nextToken()
        str2 = lst.nextToken()
    }
    var str3 = ""
    var i = 0
    var j = -1 + str1.length
    if (str1[-1 + str1.length] == decimal.single()) {
        j--
        str3 = decimal
    }
    var k = j
    while (true) {
        if (k < 0) {
            if (str2.length > 0) str3 = "$str3.$str2"
            return str3
        }
        if (i == 3) {
            str3 = "$thousand$str3"
            i = 0
        }
        str3 = str1[k].toString() + str3
        i++
        k--
    }
}
