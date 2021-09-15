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

class TemplateActivity : AppCompatActivity(){

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
}
