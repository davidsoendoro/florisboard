package com.kokatto.kobold.template

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.kokatto.kobold.R
import java.lang.ClassCastException

class TemplateEmptyFragment : Fragment(R.layout.template_fragment_empty) {

    var templateActivityListener: TemplateActivityListener? = null

    private var createButton: Button? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            templateActivityListener = context as TemplateActivityListener
        } catch (castException: ClassCastException) {
            // Listener cannot be attached
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createButton = view.findViewById(R.id.create_button)
        createButton?.let { button -> button.setOnClickListener { onButtonClicked(button) } }
    }

    private fun onButtonClicked(view: View) {
        when (view.id) {
            R.id.create_button -> {
                templateActivityListener?.openInputTemplate()
            }
        }
    }
}
