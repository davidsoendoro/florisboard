package com.kokatto.kobold.extension

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import com.kokatto.kobold.R


fun Context.showToastLong(message: String?) {
    this.showToast(message, Toast.LENGTH_LONG)
}

fun Context.showToast(message: String?, duration: Int? = null) {
    this.createToast(message, duration).show()
}

fun Context.createToast(message: String?, duration: Int? = null): Toast {
    return Toast.makeText(this, message, duration ?: Toast.LENGTH_SHORT)
}

fun Fragment.showToast(message: String?, duration: Int? = null) {
    this.createToast(message, duration)?.show()
}

fun Fragment.showToast(@StringRes messageRes: Int, duration: Int? = null) {
    this.createToast(this.getString(messageRes), duration)?.show()
}

fun Fragment.showToastLong(message: String?) {
    this.activity?.showToastLong(message)
}

fun Fragment.createToast(message: String?, duration: Int? = null): Toast? {
    return this.activity?.createToast(message, duration)
}

fun Fragment.createToastLong(message: String?): Toast? {
    return this.activity?.createToast(message, Toast.LENGTH_LONG)
}

fun View.showToast(message: String?, duration: Int? = null) {
    this.context.createToast(message, duration).show()
}