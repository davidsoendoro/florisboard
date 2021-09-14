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
import com.kokatto.kobold.editor.SpinnerEditorAdapter
import com.kokatto.kobold.editor.SpinnerEditorItem
import com.kokatto.kobold.extension.vertical
import com.kokatto.kobold.uicomponent.KoboldEditText
import com.kokatto.kobold.R
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData

class KeyboardCreateChatTemplate : ConstraintLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    private var koboldTemplatePickInput: KoboldEditText? = null
    private var koboldTemplateNameInput: KoboldEditText? = null
    private var koboldTemplateContent: KoboldEditText? = null

    private var selectedOption = SpinnerEditorItem("Pesan Pembuka")
    private var pickTemplateOptions = arrayOf(
        SpinnerEditorItem("Pesan Pembuka"),
        SpinnerEditorItem("Form Pesanan"),
        SpinnerEditorItem("Ucapan Terima Kasih"),
        SpinnerEditorItem("Ketersediaan Barang"),
        SpinnerEditorItem("Cek Barang"),
        SpinnerEditorItem("Custom")
    )

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val backButton: TextView = findViewById(R.id.back_button)
        koboldTemplatePickInput = findViewById(R.id.kobold_template_select_input)
        koboldTemplateNameInput = findViewById(R.id.kobold_template_name_input)
        koboldTemplateContent = findViewById(R.id.kobold_template_name_content)

        backButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress(TextKeyData(code = KeyCode.CANCEL))
            florisboard?.setActiveInput(R.id.kobold_menu_chat_template)
        }
        koboldTemplatePickInput?.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openSpinner(
                R.id.kobold_menu_create_chat_template, SpinnerEditorAdapter(
                    context,
                    pickTemplateOptions, selectedOption
                ) { result ->
                    florisboard.inputFeedbackManager.keyPress()
                    koboldTemplatePickInput?.editText?.text = result.label
                    florisboard.setActiveInput(R.id.kobold_menu_create_chat_template)
                    selectedOption = result
                }
            )
        }
        koboldTemplateNameInput?.setOnClickListener {
            val inputType = koboldTemplateNameInput?.inputType ?: 0
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openEditor(
                R.id.kobold_menu_create_chat_template, inputType,
                koboldTemplateNameInput?.label?.text.toString()
            ) { result ->
                koboldTemplateNameInput?.editText?.text = result
            }
        }
        koboldTemplateContent?.setOnClickListener {
            val inputType = koboldTemplateContent?.inputType ?: 0
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openEditor(
                R.id.kobold_menu_create_chat_template,
                inputType,
                koboldTemplateContent?.label?.text.toString()
            ) { result ->
                koboldTemplateContent?.editText?.text = result
            }
        }
    }
}
