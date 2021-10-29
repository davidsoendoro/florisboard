package com.kokatto.kobold.extension

import android.R.attr
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.button.MaterialButton
import com.kokatto.kobold.R
import com.kokatto.kobold.uicomponent.KoboldEditText
import dev.patrickgold.florisboard.ime.core.DELAY
import java.util.*
import android.R.attr.editable

import android.text.SpannableStringBuilder




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

fun View.findTextViewId(id: Int): TextView {
    return this.findViewById(id)
}

fun View.addRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
    foreground = AppCompatResources.getDrawable(context, resourceId)
}

fun View.findKoboldEditTextId(id: Int): KoboldEditText {
    return this.findViewById(id)
}

fun MaterialButton.koboldSetEnabled(enabled: Boolean) {
    if (enabled)
        this.setBackgroundColor(resources.getColor(R.color.kobold_blue_button))
    else
        this.setBackgroundColor(resources.getColor(R.color.kobold_blue_button_disabled))

    this.isEnabled = enabled
}

fun EditText.setPhoneNumberWatcher(){
    val watcher = object : TextWatcher {

        override fun beforeTextChanged(
            s: CharSequence, start: Int, count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence, start: Int, before: Int,
            count: Int
        ) {
        }

        override fun afterTextChanged(s: Editable) {
            s.phoneFormatter()
        }
    }

    this.addTextChangedListener(watcher)
}

fun Editable.phoneFormatter(){
    if (this.toString().contains("-")) {
        val ab: Editable = SpannableStringBuilder(this.toString().replace("-", "").replace("(", "").replace(")", ""))
        this.replace(0, this.length, ab)
    }
}


