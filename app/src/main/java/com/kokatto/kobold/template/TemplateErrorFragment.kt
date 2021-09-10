package com.kokatto.kobold.template

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import dev.patrickgold.florisboard.R

class TemplateErrorFragment : Fragment(R.layout.template_fragment_error) {
    private var reloadButton: Button? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reloadButton = view.findViewById(R.id.create_button)
        reloadButton?.let { button -> button.setOnClickListener { onButtonClicked(button) } }
    }

    private fun onButtonClicked(view: View) {
        when (view.id) {
            R.id.reload_button -> {
              // Reload Recyler View
            }
        }
    }


}
