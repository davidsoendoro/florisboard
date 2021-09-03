package com.kokatto.kobold.identity

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.ime.core.FlorisBoard

class KeyboardIdentityLogin: LinearLayout {
    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var loginButton: CardView? = null
        private set

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        loginButton = findViewById(R.id.login_button)
        loginButton?.let { button -> button.setOnClickListener { onButtonClicked(button) } }
    }

    private fun onButtonClicked(view: View) {
        when (view.id) {
            R.id.login_button -> florisboard?.setActiveInput(R.id.kobold_mainmenu)
        }
    }
}
