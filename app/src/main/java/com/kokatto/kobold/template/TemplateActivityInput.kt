package com.kokatto.kobold.template

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import com.kokatto.kobold.R
import com.kokatto.kobold.extension.showToast
import dev.patrickgold.florisboard.setup.SetupActivity

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

        val extraStateInput = intent.getIntExtra(EXTRA_STATE_INPUT, -1)

        if (extraStateInput >= 0) {
            val extraId = intent.getStringExtra(EXTRA_ID)
            val extraTemplate = intent.getStringExtra(EXTRA_TEMPLATE)
            val extraTitle = intent.getStringExtra(EXTRA_TITLE)
            val extraContent = intent.getStringExtra(EXTRA_CONTENT)

            titleText?.setText(resources.getString(R.string.detail_template))
            textInputTemplate?.setText(extraTemplate)
            textInputTitle?.setText(extraTitle)
            textInputContent?.setText(extraContent)

            buttonDelete?.isVisible = true
        } else {
            titleText?.setText(resources.getString(R.string.buat_template))
            buttonDelete?.isVisible = false
        }

    }

    override fun onItemClick(item: String?) {
        textInputTemplate?.setText(item)
    }

    private fun onClicked(view: View) {
        when (view.id) {
            R.id.create_template_button -> {
                super.finish()
                showToast(resources.getString(R.string.template_create_success))
            }
            R.id.back_button -> {
                super.onBackPressed()
            }
            R.id.choose_template_edittext -> {
                val modalSheetView = TemplateDialogActionBottom.newInstance()
                modalSheetView.show(supportFragmentManager, TemplateDialogActionBottom.TAG)
            }
            R.id.delete_button -> {
                val modalSheetView = TemplateDialogDelete.newInstance()
                modalSheetView.show(supportFragmentManager, TemplateDialogDelete.TAG)
            }
        }
    }

}
