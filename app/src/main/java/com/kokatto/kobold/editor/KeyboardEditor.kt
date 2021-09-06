package com.kokatto.kobold.editor

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.appcompat.widget.AppCompatEditText
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.ime.clip.provider.ClipboardItem
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import kotlin.math.roundToInt

class KeyboardEditor: LinearLayout {
    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    var closeMenuButton: Button? = null
        private set
    var editTextEditor: AppCompatEditText? = null
        private set

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        closeMenuButton = findViewById(R.id.kobold_button_close_menu)
        editTextEditor = findViewById(R.id.kobold_edittext_editor)

        closeMenuButton?.let { button -> button.setOnClickListener { onButtonClicked(button) } }
    }

    private fun onButtonClicked(view: View) {
        when (view.id) {
            R.id.kobold_button_close_menu -> florisboard?.setActiveInput(R.id.text_input)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                // Must be this size
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                // Can't be bigger than...
                (florisboard?.uiBinding?.inputView?.desiredSmartbarHeight ?: resources.getDimension(R.dimen.smartbar_baseHeight)).coerceAtMost(heightSize)
            }
            else -> {
                // Be whatever you want
                florisboard?.uiBinding?.inputView?.desiredSmartbarHeight ?: resources.getDimension(R.dimen.smartbar_baseHeight)
            }
        }

        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height.roundToInt(), MeasureSpec.EXACTLY))
    }

}