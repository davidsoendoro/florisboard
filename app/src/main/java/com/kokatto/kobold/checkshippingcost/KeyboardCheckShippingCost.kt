package com.kokatto.kobold.checkshippingcost

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.kokatto.kobold.R
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData

class KeyboardCheckShippingCost : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    private var backButton: TextView? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        backButton = findViewById(R.id.back_button)
        backButton?.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress(TextKeyData(code = KeyCode.CANCEL))
            florisboard?.activeEditorInstance?.activeEditText = null
            florisboard?.setActiveInput(R.id.kobold_mainmenu)
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (changedView == this.rootView && visibility == View.VISIBLE && florisboard?.koboldState == FlorisBoard.KoboldState.TEMPLATE_LIST_RELOAD) {

        }
    }
}
