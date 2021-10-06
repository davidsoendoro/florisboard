package com.kokatto.kobold.extension

import android.content.Context
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.createBottomSheetDialog(
    @LayoutRes resId: Int,
    view: ((View) -> Unit)? = null
): BottomSheetDialog? {
    val v = layoutInflater.inflate(resId, null)
    val b = this.activity?.createBottomSheetDialog(v)
    view?.invoke(v)
    return b
}

fun Fragment.createBottomSheetDialog(@LayoutRes resId: Int): BottomSheetDialog? {
    return this.activity?.createBottomSheetDialog(layoutInflater.inflate(resId, null))
}

fun Fragment.createBottomSheetDialog(contentView: View): BottomSheetDialog? {
    return this.activity?.createBottomSheetDialog(contentView)
}

fun Context.createBottomSheetDialog(contentView: View): BottomSheetDialog {
    val bottomSheetDialog = BottomSheetDialog(this)
    bottomSheetDialog.setContentView(contentView)
    return bottomSheetDialog
}
