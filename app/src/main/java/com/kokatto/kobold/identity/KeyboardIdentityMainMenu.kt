package com.kokatto.kobold.identity

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kokatto.kobold.R
import dev.patrickgold.florisboard.ime.core.FlorisBoard

class KeyboardIdentityMainMenu : LinearLayout {
    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var toDashboardButton: TextView? = null
        private set

    var chatTemplateButton: LinearLayout? = null
        private set

    var transactionButton: LinearLayout? = null
        private set

    var checkShippingcost: LinearLayout? = null
        private set

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        toDashboardButton = findViewById(R.id.to_dashboard)
        toDashboardButton?.let { button -> button.setOnClickListener { onButtonClicked(button) } }

        chatTemplateButton = findViewById(R.id.chat_template_button)
        chatTemplateButton?.let { button -> button.setOnClickListener { onButtonClicked(button) } }

        transactionButton = findViewById(R.id.create_transaction_button)
        transactionButton?.let { button -> button.setOnClickListener { onButtonClicked(button) } }

        checkShippingcost = findViewById(R.id.check_shippingcost_button)
        checkShippingcost?.let { button -> button.setOnClickListener { onButtonClicked(button) } }
    }

    private fun onButtonClicked(view: View) {
        florisboard?.inputFeedbackManager?.keyPress()
        when (view.id) {
            R.id.to_dashboard -> florisboard?.launchToDashboard()
            R.id.chat_template_button -> florisboard?.setActiveInput(R.id.kobold_menu_chat_template)
            R.id.create_transaction_button -> florisboard?.setActiveInput(R.id.kobold_menu_transaction)
            R.id.check_shippingcost_button -> florisboard?.setActiveInput(R.id.kobold_menu_check_shippingcost)
        }
    }
}
