package com.kokatto.kobold.template

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.chattemplate.ChatTemplateViewModel
import com.kokatto.kobold.extension.showToast
import com.google.android.material.textfield.TextInputLayout


class TemplateActivityInput : AppCompatActivity(), TemplateDialogSelectionClickListener {

    companion object {
        const val EXTRA_STATE_INPUT = "EXTRA_STATE_INPUT"
        const val EXTRA_ID = "ID"
        const val EXTRA_TEMPLATE = "TEMPLATE"
        const val EXTRA_TITLE = "TITLE"
        const val EXTRA_CONTENT = "CONTENT"
        const val EXTRA_STATE_CREATE = -1
        const val EXTRA_STATE_EDIT = 1
    }

    private var titleText: TextView? = null
    private var textInputTemplate: TextInputEditText? = null
    private var textInputTitle: TextInputEditText? = null
    private var textInputTitleLayout: TextInputLayout? = null
    private var textInputTitleError: TextView? = null
    private var textInputContent: TextInputEditText? = null
    private var textInputContentLayout: TextInputLayout? = null
    private var textInputContentError: TextView? = null
    private var buttonSave: Button? = null
    private var buttonBack: ImageView? = null
    private var buttonDelete: ImageView? = null
    private var extraStateInput: Int? = -1
    private var extraId: String? = ""
    private var isEdited: Boolean? = false

    private var chatTemplateViewModel: ChatTemplateViewModel? = ChatTemplateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.template_fragment_input)

        textInputTemplate = findViewById(R.id.choose_template_edittext)
        textInputTemplate?.isFocusable = false
        textInputTitle = findViewById(R.id.title_template_edittext)
        textInputTitleLayout = findViewById(R.id.title_template_textinputlayout)
        textInputTitleError = findViewById(R.id.title_template_edittext_error)
        textInputContent = findViewById(R.id.content_template_edittext)
        textInputContentLayout = findViewById(R.id.content_template_textinputlayout)
        textInputContentError = findViewById(R.id.content_template_edittext_error)
        buttonSave = findViewById(R.id.create_template_button)
        buttonBack = findViewById(R.id.back_button)
        titleText = findViewById(R.id.title_text)
        buttonDelete = findViewById(R.id.delete_button)

        buttonSave?.let { button -> button.setOnClickListener { onClicked(button) } }
        buttonBack?.let { button -> button.setOnClickListener { onClicked(button) } }
        buttonDelete?.let { button -> button.setOnClickListener { onClicked(button) } }
        textInputTemplate?.let { textInput -> textInput.setOnClickListener { onClicked(textInput) } }

        textInputTitleError?.isVisible = false
        textInputContentError?.isVisible = false


        textInputTemplate?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                markButtonSaveDisable(false)
            }
        })

        textInputTitle?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if(s.length > 50 ) {
                    textInputTitleLayout?.boxStrokeColor = resources.getColor(R.color.colorEditTextError)
                    textInputTitleError?.isVisible = true
                    markButtonSaveDisable(true)
                } else {
                    textInputTitleLayout?.boxStrokeColor = resources.getColor(R.color.colorEditTextDefault)
                    textInputTitleError?.isVisible = false
                    markButtonSaveDisable(false)
                }
            }
        })

        textInputContent?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if(s.length > 1000 ) {
                    textInputContentLayout?.boxStrokeColor = resources.getColor(R.color.colorEditTextError)
                    textInputContentError?.isVisible = true
                    markButtonSaveDisable(true)
                } else {
                    textInputContentLayout?.boxStrokeColor = resources.getColor(R.color.colorEditTextDefault)
                    textInputContentError?.isVisible = false
                    markButtonSaveDisable(false)
                }
            }
        })

        extraStateInput = intent.getIntExtra(EXTRA_STATE_INPUT, -1)

        if (extraStateInput!! >= 0) {
            extraId = intent.getStringExtra(EXTRA_ID)
            val extraTemplate = intent.getStringExtra(EXTRA_TEMPLATE)
            val extraTitle = intent.getStringExtra(EXTRA_TITLE)
            val extraContent = intent.getStringExtra(EXTRA_CONTENT)

            titleText?.text = resources.getString(R.string.detail_template)
            textInputTemplate?.setText(extraTemplate)
            textInputTitle?.setText(extraTitle)
            textInputContent?.setText(extraContent)

            buttonDelete?.isVisible = true

            buttonSave?.isEnabled = false
            buttonSave?.isAllCaps = false
            buttonSave?.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_10))
            buttonSave?.setTextColor(ContextCompat.getColor(this, R.color.text_color_white))

        } else {
            titleText?.text = resources.getString(R.string.buat_template)
            buttonDelete?.isVisible = false
        }

    }

    override fun onItemClick(item: String?) {
        textInputTemplate?.setText(item)
    }

    private fun onClicked(view: View) {
        when (view.id) {
            R.id.create_template_button -> {

                var model = AutoTextModel()
                model.template = textInputTemplate?.text.toString()
                model.title = textInputTitle?.text.toString()
                model.content = textInputContent?.text.toString()

                if (extraStateInput!! >= 0) {
                    model._id = extraId
                    chatTemplateViewModel?.updateAutotextById(
                        extraId!!,
                        model,
                        onSuccess = { it ->
                            super.finish()
                            showToast(resources.getString(R.string.template_create_success))
                        },
                        onError = {
                            showToast(it)
                        }
                    )
                } else {
                    model._id = null
                    chatTemplateViewModel?.createChatTemplate(
                        model,
                        onSuccess = { it ->
                            super.finish()
                            showToast(resources.getString(R.string.template_create_success))
                        },
                        onError = {
                            showToast(it)
                        }
                    )
                }
            }
            R.id.back_button -> {
                super.onBackPressed()
            }
            R.id.choose_template_edittext -> {
                val modalSheetView = TemplateDialogActionBottom.newInstance()
                modalSheetView.show(supportFragmentManager, TemplateDialogActionBottom.TAG)
            }
            R.id.delete_button -> {
                val modalSheetView = extraId?.let { TemplateDialogDelete.newInstance(it) }
                modalSheetView?.show(supportFragmentManager, TemplateDialogDelete.TAG)
            }
        }
    }

    fun markButtonSaveDisable(isDisable: Boolean) {
        if (isDisable) {
            buttonSave?.isAllCaps = false
            buttonSave?.isEnabled = false
            buttonSave?.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_10))
            buttonSave?.setTextColor(ContextCompat.getColor(this, R.color.text_color_white))
        } else {
            buttonSave?.isAllCaps = false
            buttonSave?.isEnabled = true
            buttonSave?.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_50))
            buttonSave?.setTextColor(ContextCompat.getColor(this, R.color.text_color_white))
        }
    }


}
