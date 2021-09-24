package com.kokatto.kobold.extension

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar



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

fun Fragment.createToast(message: String?, duration: Int? = null): Toast? {
    return this.activity?.createToast(message, duration)
}


fun View.showToast(message: String?, duration: Int? = null) {
    this.context.createToast(message, duration).show()
}

fun View.showSnackBar(message: String = "", duration: Int = Snackbar.LENGTH_LONG) {
    val snackbar = Snackbar
        .make(this, message, duration)
    snackbar.show()
}
