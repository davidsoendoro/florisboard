package com.kokatto.kobold.template

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.chattemplate.ChatTemplateViewModel
import com.kokatto.kobold.extension.showToast
import java.util.concurrent.atomic.AtomicInteger


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
    private var textInputContent: TextInputEditText? = null
    private var buttonSave: Button? = null
    private var buttonBack: ImageView? = null
    private var buttonDelete: ImageView? = null
    private var extraStateInput: Int? = -1
    private var extraId: String? = ""
    private val maxTitleLength = AtomicInteger(50)
    private val maxContentLength = AtomicInteger(1000)


    private var chatTemplateViewModel: ChatTemplateViewModel? = ChatTemplateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.template_fragment_input)

        textInputTemplate = findViewById(R.id.choose_template_edittext)
        textInputTemplate?.isFocusable = false
        textInputTitle = findViewById(R.id.title_template_edittext)
        textInputContent = findViewById(R.id.content_template_edittext)
        buttonSave = findViewById(R.id.create_template_button)
        buttonBack = findViewById(R.id.back_button)
        titleText = findViewById(R.id.title_text)
        buttonDelete = findViewById(R.id.delete_button)

        buttonSave?.let { button -> button.setOnClickListener { onClicked(button) } }
        buttonBack?.let { button -> button.setOnClickListener { onClicked(button) } }
        buttonDelete?.let { button -> button.setOnClickListener { onClicked(button) } }
        textInputTemplate?.let { textInput -> textInput.setOnClickListener { onClicked(textInput) } }

        // textInputTitleError?.isVisible = false
        // textInputContentError?.isVisible = false

        textInputTitle?.addTextChangedListener { text ->
            val textInputLayout = findViewById<TextInputLayout>(R.id.title_template_textinputlayout)
            val textInputError = findViewById<TextView>(R.id.title_template_edittext_error)

            text?.let { _text ->
                if (_text.length > maxTitleLength.toInt()) {
                    textInputLayout.boxStrokeColor = resources.getColor(R.color.colorEditTextError, null)
                    textInputError.visibility = View.VISIBLE
                    validateInput()
                } else {
                    textInputLayout.boxStrokeColor = resources.getColor(R.color.colorEditTextDefault, null)
                    textInputError.visibility = View.GONE
                    validateInput()
                }
            }
        }

        textInputContent?.addTextChangedListener { text ->
            val textInputLayout = findViewById<TextInputLayout>(R.id.content_template_textinputlayout)
            val textInputError = findViewById<TextView>(R.id.content_template_edittext_error)

            text?.let { _text ->
                if (_text.length > maxContentLength.toInt()) {
                    textInputLayout.boxStrokeColor = resources.getColor(R.color.colorEditTextError, null)
                    textInputLayout.isErrorEnabled = true
                    textInputError.visibility = View.VISIBLE
                    validateInput()
                } else {
                    textInputLayout.boxStrokeColor = resources.getColor(R.color.colorEditTextDefault, null)
                    textInputError.visibility = View.GONE
                    validateInput()
                }
            }
        }

        extraStateInput = intent.getIntExtra(EXTRA_STATE_INPUT, -1)
        extraId = intent.getStringExtra(EXTRA_ID)

        val extraTemplate = intent.getStringExtra(EXTRA_TEMPLATE)
        val extraTitle = intent.getStringExtra(EXTRA_TITLE)
        val extraContent = intent.getStringExtra(EXTRA_CONTENT)
        markButtonSaveDisable(true)

        if (extraStateInput!! >= 0) {
            titleText?.text = resources.getString(R.string.detail_template)
            buttonDelete?.isVisible = true
            setEditTextValue(extraTemplate, extraTitle, extraContent)
        } else {
            extraId = "NEW"
            titleText?.text = resources.getString(R.string.buat_template)
            buttonDelete?.isVisible = false

            // accepted form expand view or button create
            if (extraTemplate == null || extraTemplate == "") {
                setEditTextValue("Pesan Pembuka", "", "")
                markButtonSaveDisable(true)
            } else {
                setEditTextValue(extraTemplate, extraTitle, extraContent)
                // Allow to save
                markButtonSaveDisable(false)
            }
        }
    }


    override fun onItemClick(item: String?) {
        textInputTemplate?.setText(item)
    }

    private fun onClicked(view: View) {
        when (view.id) {
            R.id.create_template_button -> {
                extraId?.let { _extraId ->

                    if (extraStateInput!! >= 0) {
                        val model = AutoTextModel(
                            _id = _extraId,
                            template = textInputTemplate?.text.toString(),
                            title = textInputTitle?.text.toString(),
                            content = textInputContent?.text.toString()
                        )

                        chatTemplateViewModel?.updateAutotextById(
                            _extraId,
                            model,
                            onSuccess = {
                                super.finish()
                                showToast(resources.getString(R.string.template_create_success))
                            },
                            onError = {
                                showToast(it)
                            }
                        )
                    } else {
                        val model = AutoTextModel(
                            template = textInputTemplate?.text.toString(),
                            title = textInputTitle?.text.toString(),
                            content = textInputContent?.text.toString()
                        )

                        chatTemplateViewModel?.createChatTemplate(
                            model,
                            onSuccess = {
                                super.finish()
                                showToast(resources.getString(R.string.template_create_success))
                            },
                            onError = {
                                showToast(it)
                            }
                        )
                    }
                }
            }
            R.id.back_button -> {
                super.onBackPressed()
            }
            R.id.choose_template_edittext -> {
                // val modalSheetView = TemplateDialogActionBottom.newInstance()
                val modalSheetView = textInputTemplate?.text.toString().let { TemplateDialogSelection.newInstance(it) }
                modalSheetView.show(supportFragmentManager, TemplateDialogSelection.TAG)
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

    private fun setEditTextValue(template: String?, title: String?, content: String?) {
        textInputTemplate?.setText(template)
        textInputTitle?.setText(title)
        textInputContent?.setText(content)
    }

    fun validateInput() {
        var titleLength = 0
        var contentLength = 0
        textInputTitle?.let { text ->
            titleLength = text.length()
        }

        textInputContent?.let { text ->
            contentLength = text.length()
        }


        if (titleLength < maxTitleLength.toInt() && contentLength < maxContentLength.toInt()) {
            markButtonSaveDisable(false)
        } else {
            markButtonSaveDisable(true)
        }
    }

}
