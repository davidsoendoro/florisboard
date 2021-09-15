package com.kokatto.kobold.chattemplate

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.kokatto.kobold.editor.SpinnerEditorAdapter
import com.kokatto.kobold.editor.SpinnerEditorItem
import com.kokatto.kobold.uicomponent.KoboldEditText
import com.kokatto.kobold.R
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData
import android.os.ResultReceiver
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.extension.showToast


class KeyboardCreateChatTemplate : ConstraintLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    private var koboldExpandView: TextView? = null
    private var koboldTemplatePickInput: KoboldEditText? = null
    private var koboldTemplateNameInput: KoboldEditText? = null
    private var koboldTemplateContent: KoboldEditText? = null
    private var saveButton: Button? = null

    private var chatTemplateViewModel: ChatTemplateViewModel? = ChatTemplateViewModel()

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
        koboldExpandView = findViewById(R.id.kobold_createtemplate_expand_view)
        koboldTemplatePickInput = findViewById(R.id.kobold_template_select_input)
        koboldTemplateNameInput = findViewById(R.id.kobold_template_name_input)
        koboldTemplateContent = findViewById(R.id.kobold_template_name_content)
        saveButton = findViewById(R.id.save_button)

        backButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress(TextKeyData(code = KeyCode.CANCEL))
            florisboard?.activeEditorInstance?.activeEditText = null
            florisboard?.setActiveInput(R.id.kobold_menu_chat_template)
        }
        koboldExpandView?.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()

            val pickInput = koboldTemplatePickInput?.editText?.text.toString()
            val nameInput = koboldTemplatePickInput?.editText?.text.toString()
            val content = koboldTemplatePickInput?.editText?.text.toString()
            florisboard?.launchExpandView(pickInput, nameInput, content)
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
            val imeOptions = koboldTemplateNameInput?.imeOptions ?: 0
            val inputType = koboldTemplateNameInput?.inputType ?: 0
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openEditor(
                R.id.kobold_menu_create_chat_template,
                imeOptions,
                inputType,
                koboldTemplateNameInput?.label?.text.toString(),
                koboldTemplateNameInput?.editText?.text.toString()
            ) { result ->
                koboldTemplateNameInput?.editText?.text = result
                invalidateSaveButton()
            }
        }
        koboldTemplateContent?.setOnClickListener {
            val imeOptions = koboldTemplateContent?.imeOptions ?: 0
            val inputType = koboldTemplateContent?.inputType ?: 0
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openEditor(
                R.id.kobold_menu_create_chat_template,
                imeOptions,
                inputType,
                koboldTemplateContent?.label?.text.toString(),
                koboldTemplateContent?.editText?.text.toString()
            ) { result ->
                koboldTemplateContent?.editText?.text = result
                invalidateSaveButton()
            }
        }

        saveButton?.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            val model = AutoTextModel(
                template = koboldTemplatePickInput?.editText?.text.toString(),
                title = koboldTemplateNameInput?.editText?.text.toString(),
                content = koboldTemplateContent?.editText?.text.toString()
            )

            chatTemplateViewModel?.createChatTemplate(
                model,
                onSuccess = {
                    florisboard?.activeEditorInstance?.activeEditText = null
                    florisboard?.koboldState = FlorisBoard.KoboldState.TEMPLATE_LIST_RELOAD
                    florisboard?.setActiveInput(R.id.kobold_menu_chat_template)
                },
                onError = {
                    showToast(it)
                }
            )
        }
    }

    fun invalidateSaveButton() {
        var isInputValid = false
        koboldTemplateNameInput?.let { templateNameInput ->
            koboldTemplateContent?.let { templateContent ->
                isInputValid = templateNameInput.isInputValid() && templateContent.isInputValid()
            }
        }
        saveButton?.isEnabled = isInputValid
    }
}