package com.kokatto.kobold.identity

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData

class KeyboardIdentityHeader: LinearLayout {
    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    var closeMenuButton: ImageView? = null
        private set

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        closeMenuButton = findViewById(R.id.kobold_button_close_menu)

        closeMenuButton?.let { button -> button.setOnTouchListener { v, event -> onButtonClicked(v, event) } }
    }

    private fun onButtonClicked(view: View, event: MotionEvent?): Boolean {
        florisboard?.inputFeedbackManager?.keyPress(TextKeyData(code = KeyCode.CANCEL))
        when (view.id) {
            R.id.kobold_button_close_menu -> florisboard?.setActiveInput(R.id.text_input)
        }
        return true
    }
}
