package com.kokatto.kobold.transaction

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.kokatto.kobold.R
import com.kokatto.kobold.checkshippingcost.KeyboardCheckShippingCost
import com.kokatto.kobold.extension.showToast
import dev.patrickgold.florisboard.ime.core.FlorisBoard

class KeyboardCreateTransactionSaveConfirmation : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    var cancelButton: Button? = null
    var submitButton: Button? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        cancelButton = findViewById(R.id.cancel_button)
        submitButton = findViewById(R.id.submit_button)

        cancelButton?.setOnClickListener {
            onClick(this)
        }
        submitButton?.setOnClickListener {
            onClick(this)
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (changedView is KeyboardCheckShippingCost && visibility == View.VISIBLE && changedView == this) {
//            invalidateSaveButton()
        }
    }

    fun onClick(id: View) {
        if (id == submitButton) {
            //call  is profile change API
            showToast("change profile")
        }
        florisboard?.inputFeedbackManager?.keyPress()
        florisboard?.textInputManager?.activeEditorInstance?.commitText(
            florisboard.createTransactionText
        )
        florisboard?.setActiveInput(R.id.text_input)
    }
}
