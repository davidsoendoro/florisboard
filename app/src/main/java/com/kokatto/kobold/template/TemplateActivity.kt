package com.kokatto.kobold.template

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.kokatto.kobold.R
import com.kokatto.kobold.extension.showToast
import dev.patrickgold.florisboard.setup.SetupActivity
import dev.patrickgold.florisboard.util.checkIfImeIsEnabled

interface TemplateActivityListener {
    fun openCreateTemplate(pickInput: String? = null, nameInput: String? = null, content: String? = null)
}

class TemplateActivity : AppCompatActivity(), TemplateActivityListener {

    object FromKeyboardRequest {
        val templatePickInputKey = "TEMPLATE_PICK_KEY"
        val templateNameInputKey = "TEMPLATE_NAME_KEY"
        val templateContentKey = "TEMPLATE_CONTENT_KEY"
    }

    private var activeButton: Button? = null
    private var searchButton: ImageView? = null
    private var warningLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.template_activity)

        activeButton = findViewById(R.id.popup_keyboard_active_button)
        searchButton = findViewById(R.id.search_button)
        warningLayout = findViewById(R.id.layout_active_keyboard)

        activeButton?.let { button -> button.setOnClickListener { onButtonClicked(button) } }
        searchButton?.let { button -> button.setOnClickListener { onButtonClicked(button) } }

        // check if data is available or not
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<TemplateDataListFragment>(R.id.template_fragment_container_view)
            }
        }

        if (checkIfImeIsEnabled(this)) {
            warningLayout?.let { layout -> layout.visibility = View.GONE }
        } else {
            warningLayout?.let { layout -> layout.visibility = View.VISIBLE }
        }

        // Check if this is from expand view
        val templatePickInputValue = intent.getStringExtra(FromKeyboardRequest.templatePickInputKey)
        val templateNameInputValue = intent.getStringExtra(FromKeyboardRequest.templateNameInputKey)
        val templateContentValue = intent.getStringExtra(FromKeyboardRequest.templateContentKey)

        if (templateNameInputValue != null || templatePickInputValue != null || templateContentValue != null) {
            openCreateTemplate()
        }
    }

    private fun onButtonClicked(view: View) {
        when (view.id) {
            R.id.popup_keyboard_active_button -> {
                Intent(this, SetupActivity::class.java).apply {
                    putExtra(SetupActivity.EXTRA_SHOW_SINGLE_STEP, SetupActivity.Step.FINISH)
                    startActivity(this)
                    warningLayout?.let { layout -> layout.visibility = View.GONE }
                }
            }
            R.id.search_button -> {
                Intent(this, TemplateActivitySearch::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }

    override fun openCreateTemplate(pickInput: String?, nameInput: String?, content: String?) {
        Intent(this, TemplateActivityInput::class.java).apply {
            putExtra(TemplateActivityInput.EXTRA_STATE_INPUT, TemplateActivityInput.EXTRA_STATE_CREATE)
            pickInput?.let { _pickInput ->
                putExtra(TemplateActivityInput.EXTRA_TEMPLATE, _pickInput)
            }
            nameInput?.let { _nameInput ->
                putExtra(TemplateActivityInput.EXTRA_TITLE, _nameInput)
            }
            content?.let { _content ->
                putExtra(TemplateActivityInput.EXTRA_CONTENT, _content)
            }
            startActivity(this)
        }
    }
}
