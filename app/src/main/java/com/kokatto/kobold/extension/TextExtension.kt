package com.kokatto.kobold.extension

import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.kokatto.kobold.R
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

fun String.highlightText(textToHighlight: CharSequence, @ColorInt highlightColor: Int): Spannable {
    if (this.isBlank()) return SpannableString("")
    if (textToHighlight.isEmpty()) return SpannableString(this)

    val target = this.lowercase()
    val targetLength = target.length
    val targetSpan = SpannableString(this)
    val highlight = textToHighlight.toString().lowercase()

    var ofe = target.indexOf(highlight, 0)
    var ofs = 0
    while (ofs < targetLength && ofe != -1) {
        ofe = target.indexOf(highlight, ofs)
        if (ofe == -1) break
        else {
            targetSpan.setSpan(
                BackgroundColorSpan(highlightColor),
                ofe,
                ofe + textToHighlight.length,
                0
            )
        }
        ofs = ofe + 1
    }
    return targetSpan
}

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

fun String.removeThousandSeparatedString(): String {
    return this.filter { it.isDigit() }
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

fun TextView.showStrikeThrough(show: Boolean) {
    paintFlags =
        if (show) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
}

fun EditText.setThousandSeparator() {
    this.addTextChangedListener(thousandSeparator(this))
}

fun TextView.addDrawableStart(view: TextView, assetUrl: String) {
    Glide.with(this).load(assetUrl)
        .diskCacheStrategy(DiskCacheStrategy.ALL).into(
        object : CustomTarget<Drawable>(50, 50) {
            override fun onLoadCleared(placeholder: Drawable?) {
                view.setCompoundDrawablesWithIntrinsicBounds(
                    placeholder, null, null, null
                )
                view.compoundDrawablePadding = 12
            }

            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                view.setCompoundDrawablesWithIntrinsicBounds(
                    resource, null, null, null
                )
                view.compoundDrawablePadding = 12
            }
        }
    )
}
