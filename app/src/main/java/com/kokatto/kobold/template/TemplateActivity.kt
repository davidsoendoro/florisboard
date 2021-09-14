package com.kokatto.kobold.template

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.kokatto.kobold.R
import dev.patrickgold.florisboard.setup.SetupActivity
import dev.patrickgold.florisboard.util.checkIfImeIsEnabled

class TemplateActivity : AppCompatActivity() {

    private var activeButton: Button? = null
    private var warningLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.template_activity)

        activeButton = findViewById(R.id.popup_keyboard_active_button)
        warningLayout = findViewById(R.id.layout_active_keyboard)

        activeButton?.let { button -> button.setOnClickListener { onButtonClicked(button) } }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<TemplateEmptyFragment>(R.id.template_fragment_container_view)
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
                    putExtra(SetupActivity.EXTRA_SHOW_SINGLE_STEP, SetupActivity.Step.ENABLE_IME)
                    startActivity(this)
                    warningLayout?.let { layout -> layout.visibility = View.GONE }
                }
            }
        }
    }

}
