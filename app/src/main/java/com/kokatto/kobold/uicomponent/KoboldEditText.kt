package com.kokatto.kobold.uicomponent

import android.content.Context
import android.content.res.TypedArray
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.google.android.material.card.MaterialCardView
import com.kokatto.kobold.R
import com.kokatto.kobold.helpers.UiMetricHelper
import dev.patrickgold.florisboard.ime.core.FlorisBoard

class KoboldEditText : ConstraintLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.KoboldEditText, defStyleAttr, 0)

        val labelText: String? = a.getString(R.styleable.KoboldEditText_label)
        val textValue: String? = a.getString(R.styleable.KoboldEditText_android_text)
        val textHint: String? = a.getString(R.styleable.KoboldEditText_android_hint)
        val imeOptions: Int = a.getInt(R.styleable.KoboldEditText_android_imeOptions, 0)
        val inputType: Int = a.getInt(R.styleable.KoboldEditText_android_inputType, 0)
        val isEditable: Boolean = a.getBoolean(R.styleable.KoboldEditText_editable, false)
        val showChevron: Boolean = a.getBoolean(R.styleable.KoboldEditText_showChevron, false)
        val showCalculator: Boolean = a.getBoolean(R.styleable.KoboldEditText_showCalculator, false)
        val maxAllowedCharacters: Int = a.getInt(R.styleable.KoboldEditText_maxAllowedCharacters, 0)
        val isAutofill: Boolean = a.getBoolean(R.styleable.KoboldEditText_isAutofill, false)
        val isClearable: Boolean = a.getBoolean(R.styleable.KoboldEditText_isClearable, false)
        val isNeedThousandSeparator: Boolean = a.getBoolean(R.styleable.KoboldEditText_isNeedThousandSeparator, false)
        val imageLeft: String? = a.getString(R.styleable.KoboldEditText_imageLeft)
//        val entries = a.getTextArray(R.styleable.KoboldEditText_android_entries)

        label.text = labelText
        editText.text = textValue
        editText.hint = textHint
        this.imeOptions = imeOptions
        this.inputType = inputType
        this.maxAllowedCharacters = maxAllowedCharacters
        this.isAutofill = isAutofill
//        this.entries = entries

        if (isEditable) {
            editText.visibility = View.GONE
            editable.visibility = View.VISIBLE
        } else {
            editText.visibility = View.VISIBLE
            editable.visibility = View.GONE
        }

        if (showChevron) {
            chevronRight.visibility = VISIBLE
        } else {
            chevronRight.visibility = GONE
        }

        if (showCalculator) {
            calculatorRight.visibility = VISIBLE
        } else {
            calculatorRight.visibility = GONE
        }

        if (isClearable) {
            clearButton.visibility = VISIBLE
        } else {
            clearButton.visibility = GONE
        }

        if ((inputType and InputType.TYPE_TEXT_FLAG_MULTI_LINE) > 0) {
            editText.isSingleLine = false
        }

        a.recycle()
    }

    val label: TextView
    val editText: TextView
    val editable: AppCompatEditText
    val clearButton: ImageView
    val chevronRight: ImageView
    val calculatorRight: ImageView
    val errorText: TextView
    var imeOptions: Int = 0
    var inputType: Int = 0
    var maxAllowedCharacters: Int = 0
    var isAutofill = false
    val imageLeft: ImageView

    var entries = arrayOf<CharSequence>()

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.uicomponent_kobold_edittext, this, true)

        label = findViewById(R.id.kobold_edittext_label)
        editText = findViewById(R.id.kobold_edittext_text)
        editable = findViewById(R.id.kobold_edittext_editable)
        clearButton = findViewById(R.id.kobold_edittext_clear)
        chevronRight = findViewById(R.id.kobold_edittext_chevron)
        calculatorRight = findViewById(R.id.kobold_edittext_calculator)
        errorText = findViewById(R.id.kobold_edittext_errorText)
        imageLeft = findViewById(R.id.kobold_edittext_imageleft)

        editText.addTextChangedListener { text ->
            val editTextLayout = findViewById<MaterialCardView>(R.id.layout_kobold_edittext)

            if (maxAllowedCharacters > 0) {
                text?.let { _text ->
                    if (_text.length > maxAllowedCharacters) {
                        editTextLayout.strokeColor = resources.getColor(R.color.text_error_red, null)
                        if (errorText.visibility == View.GONE) {
                            layoutParams.height += UiMetricHelper.dpToPx(context, 16)
                            errorText.visibility = View.VISIBLE
                        }
                    } else {
                        editTextLayout.strokeColor = resources.getColor(R.color.gray_1, null)
                        if (errorText.visibility == View.VISIBLE) {
                            layoutParams.height -= UiMetricHelper.dpToPx(context, 16)
                            errorText.visibility = View.GONE
                        }
                    }
                }
            } else {
                editTextLayout.strokeColor = resources.getColor(R.color.gray_1, null)
                errorText.visibility = View.GONE
            }
        }

        clearButton.setOnClickListener {
            FlorisBoard.getInstance().inputFeedbackManager.keyPress()
            editable.text?.clear()
        }
    }

    fun isInputValid(): Boolean {
        return editText.text.length < maxAllowedCharacters
    }
}
