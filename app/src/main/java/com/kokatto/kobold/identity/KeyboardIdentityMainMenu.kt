package com.kokatto.kobold.identity

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kokatto.kobold.R
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData

class KeyboardIdentityMainMenu: LinearLayout {
    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var backButton: TextView? = null
        private set

    var chatTemplateButton: LinearLayout? = null
    private set

    var transactionButton: LinearLayout? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        backButton = findViewById(R.id.back_button)
        backButton?.let { button -> button.setOnClickListener { onButtonClicked(button) } }

        chatTemplateButton = findViewById(R.id.chat_template_button)
        chatTemplateButton?.let { button -> button.setOnClickListener { onButtonClicked(button) } }

        transactionButton = findViewById(R.id.create_transaction_button)
        transactionButton?.let { button -> button.setOnClickListener { onButtonClicked(button) } }
    }

    private fun onButtonClicked(view: View) {
        florisboard?.inputFeedbackManager?.keyPress()
        when (view.id) {
            R.id.back_button -> florisboard?.setActiveInput(R.id.text_input)
            R.id.chat_template_button -> florisboard?.setActiveInput(R.id.kobold_menu_chat_template)
            R.id.create_transaction_button -> florisboard?.setActiveInput(R.id.kobold_menu_transaction)
        }
    }
}
