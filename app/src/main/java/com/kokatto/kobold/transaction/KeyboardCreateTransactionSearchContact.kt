package com.kokatto.kobold.transaction

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.kokatto.kobold.chattemplate.KeyboardChatTemplate

class KeyboardCreateTransactionSearchContact : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (changedView is KeyboardChatTemplate && visibility == View.VISIBLE && changedView == this) {

        } else {

        }
    }
}
