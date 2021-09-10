package com.kokatto.kobold.chattemplate

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.chattemplate.recycleradapter.ChatTemplateRecyclerAdapter
import com.kokatto.kobold.extension.vertical
import com.kokatto.kobold.uicomponent.KoboldEditText
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData

class KeyboardCreateChatTemplate : ConstraintLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    private var koboldTemplateNameInput: KoboldEditText? = null
    private var koboldTemplateContent: KoboldEditText? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val backButton: TextView = findViewById(R.id.back_button)
        koboldTemplateNameInput = findViewById(R.id.kobold_template_name_input)
        koboldTemplateContent = findViewById(R.id.kobold_template_name_content)

        backButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress(TextKeyData(code = KeyCode.CANCEL))
            florisboard?.setActiveInput(R.id.kobold_menu_chat_template)
        }
        koboldTemplateNameInput?.setOnClickListener {
            val inputType = koboldTemplateNameInput?.inputType ?: 0
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openEditor(R.id.kobold_menu_create_chat_template, inputType) { result ->
                koboldTemplateNameInput?.editText?.text = result
            }
        }
        koboldTemplateContent?.setOnClickListener {
            val inputType = koboldTemplateContent?.inputType ?: 0
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openEditor(R.id.kobold_menu_create_chat_template, inputType) { result ->
                koboldTemplateContent?.editText?.text = result
            }
        }
    }
}
