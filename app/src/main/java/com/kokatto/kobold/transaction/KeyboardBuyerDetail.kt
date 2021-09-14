package com.kokatto.kobold.transaction

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import com.kokatto.kobold.R
import dev.patrickgold.florisboard.ime.core.FlorisBoard

class KeyboardBuyerDetail: LinearLayout {
    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    var buyerNameEditText: EditText? = null
        private set

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        buyerNameEditText = findViewById(R.id.kobold_buyer_name)

        buyerNameEditText?.let { editText -> editText.setOnFocusChangeListener { v, hasFocus ->  onEditTextFocusChangeListener(v, hasFocus) } }
    }

    private fun onEditTextFocusChangeListener(view: View, hasFocus: Boolean) {
        when (view.id) {
            R.id.kobold_buyer_name -> {
                if (hasFocus) {
                    florisboard?.setActiveInput(R.id.kobold_editor)
                }
            }
        }
    }
}
