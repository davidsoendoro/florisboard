package com.kokatto.kobold.template

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.kokatto.kobold.R

class TemplateEmptyFragment : Fragment(R.layout.template_fragment_empty) {
    private var createButton: Button? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createButton = view.findViewById(R.id.create_button)
        createButton?.let { button -> button.setOnClickListener { onButtonClicked(button) } }
    }

    private fun onButtonClicked(view: View) {
        when (view.id) {
            R.id.create_button -> {
                Intent(requireContext(), TemplateActivityInput::class.java).apply {
                    putExtra(TemplateActivityInput.EXTRA_STATE_INPUT, TemplateActivityInput.EXTRA_STATE_CREATE)
                    startActivity(this)
                }
            }
        }
    }
}
