package com.kokatto.kobold.template

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.google.android.material.snackbar.Snackbar
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.databinding.SettingsActivityBinding
import com.kokatto.kobold.databinding.TemplateActivityBinding
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.showToast
import dev.patrickgold.florisboard.settings.FRAGMENT_TAG
import dev.patrickgold.florisboard.setup.SetupActivity
import dev.patrickgold.florisboard.util.checkIfImeIsEnabled

interface TemplateActivityListener {
    fun openCreateTemplate(pickInput: String? = null, nameInput: String? = null, content: String? = null)
    fun openInputTemplate()
    fun openEditTemplate(autoTextModel: AutoTextModel)
    fun openErrorFragment()
    fun openEmptyFragment()
    fun openDataListFragment()
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
    lateinit var binding: TemplateActivityBinding
    private var launchInputActivity: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.template_activity)

        binding = TemplateActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activeButton = findViewById(R.id.popup_keyboard_active_button)
        searchButton = findViewById(R.id.search_button)
        warningLayout = findViewById(R.id.layout_active_keyboard)

        activeButton?.let { button -> button.setOnClickListener { onButtonClicked(button) } }
        searchButton?.let { button -> button.setOnClickListener { onButtonClicked(button) } }

        // check if data is available or not
        if (savedInstanceState == null) {
            loadFragment(TemplateDataListFragment())
//            supportFragmentManager.commit {
//                setReorderingAllowed(true)
//                add<TemplateDataListFragment>(R.id.template_fragment_container_view)
//                add<TemplateEmptyFragment>(R.id.template_fragment_container_view)
//                add<TemplateErrorFragment>(R.id.template_fragment_container_view)
//            }
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
            openCreateTemplate(templatePickInputValue,templateNameInputValue,templateContentValue)
        }

        launchInputActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when(result.resultCode) {
                ActivityConstantCode.RESULT_OK_CREATED -> {
                    showSnackBar(findViewById(R.id.parent_layout), resources.getString(R.string.template_create_success))
                }
                ActivityConstantCode.RESULT_OK_UPDATED -> {
                    showSnackBar(findViewById(R.id.parent_layout), resources.getString(R.string.template_update_success))
                }
                ActivityConstantCode.RESULT_OK_DELETED -> {
                    showSnackBar(findViewById(R.id.parent_layout), resources.getString(R.string.template_delete_success))
                }
            }
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
        val intent = Intent(this, TemplateActivityInput::class.java)
        pickInput?.let { _pickInput ->
                intent.putExtra(TemplateActivityInput.EXTRA_TEMPLATE, _pickInput)
            }
        nameInput?.let { _nameInput ->
                        intent.putExtra(TemplateActivityInput.EXTRA_TITLE, _nameInput)
            }
        content?.let { _content ->
                intent.putExtra(TemplateActivityInput.EXTRA_CONTENT, _content)
            }
        launchInputActivity?.launch(intent)
    }

    override fun openInputTemplate() {
        val intent = Intent(applicationContext, TemplateActivityInput::class.java)
        intent.putExtra(ActivityConstantCode.EXTRA_MODE, ActivityConstantCode.EXTRA_CREATE)
        launchInputActivity?.launch(intent)
    }

    override fun openEditTemplate(model: AutoTextModel) {
        val intent = Intent(applicationContext, TemplateActivityInput::class.java)
        intent.putExtra(TemplateActivityInput.EXTRA_ID, model._id)
        intent.putExtra(ActivityConstantCode.EXTRA_DATA, model)
        intent.putExtra(ActivityConstantCode.EXTRA_MODE, ActivityConstantCode.EXTRA_EDIT)
        launchInputActivity?.launch(intent)
    }

    override fun openErrorFragment() {
        loadFragment(TemplateErrorFragment())
    }

    override fun openEmptyFragment() {
        loadFragment(TemplateEmptyFragment())
    }

    override fun openDataListFragment() {
        loadFragment(TemplateDataListFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(binding.templateFragmentContainerView.id, fragment, FRAGMENT_TAG)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        openDataListFragment()
    }



}
