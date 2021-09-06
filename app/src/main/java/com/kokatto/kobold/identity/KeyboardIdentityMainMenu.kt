package com.kokatto.kobold.identity

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.ime.core.FlorisBoard

class KeyboardIdentityMainMenu: LinearLayout {
    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var backButton: TextView? = null
        private set

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        backButton = findViewById(R.id.back_button)
        backButton?.let { button -> button.setOnClickListener { onBackButtonClicked(button) } }
    }

    private fun onBackButtonClicked(view: View) {
        when (view.id) {
            R.id.back_button -> florisboard?.setActiveInput(R.id.text_input)
        }
    }
}