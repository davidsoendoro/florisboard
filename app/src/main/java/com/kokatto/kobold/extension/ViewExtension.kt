package com.kokatto.kobold.extension

import android.view.View
import android.view.ViewGroup

fun View.setMargins(
    left: Int? = null,
    top: Int? = null,
    right: Int? = null,
    bottom: Int? = null
) {
    val lp = layoutParams as? ViewGroup.MarginLayoutParams
        ?: return

    lp.setMargins(
        left ?: lp.leftMargin,
        top ?: lp.topMargin,
        right ?: lp.rightMargin,
        bottom ?: lp.bottomMargin
    )

    layoutParams = lp
}

fun View.padding(
    left: Int? = null,
    top: Int? = null,
    right: Int? = null,
    bottom: Int? = null
) {

    this.setPadding(
        left ?: this.paddingStart,
        top ?: this.paddingTop,
        right ?: this.paddingEnd,
        bottom ?: this.paddingBottom
    )

}
