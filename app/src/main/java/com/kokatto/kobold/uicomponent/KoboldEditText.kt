package com.kokatto.kobold.uicomponent

import dev.patrickgold.florisboard.R
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.card.MaterialCardView

class KoboldEditText: MaterialCardView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.KoboldEditText, defStyleAttr, 0)

        val labelText: String? = a.getString(R.styleable.KoboldEditText_label)
        val textValue: String? = a.getString(R.styleable.KoboldEditText_android_text)
        val inputType: Int = a.getType(R.styleable.KoboldEditText_android_inputType)
        val isEditable: Boolean = a.getBoolean(R.styleable.KoboldEditText_editable, false)
//        val entries = a.getTextArray(R.styleable.KoboldEditText_android_entries)

        label.text = labelText
        editText.text = textValue
        this.inputType = inputType
//        this.entries = entries

        if (isEditable) {
            editText.visibility = View.GONE
            editable.visibility = View.VISIBLE
        } else {
            editText.visibility = View.VISIBLE
            editable.visibility = View.GONE
        }

        a.recycle();
    }

    val label: TextView
    val editText: TextView
    val editable: AppCompatEditText
    var inputType: Int = 0
    var entries = arrayOf<CharSequence>()

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.uicomponent_kobold_edittext, this, true)

        val layout = findViewById<LinearLayout>(R.id.kobold_edittext_layout)
        label = layout.findViewById(R.id.kobold_edittext_label)
        editText = layout.findViewById(R.id.kobold_edittext_text)
        editable = layout.findViewById(R.id.kobold_edittext_editable)
    }
}
