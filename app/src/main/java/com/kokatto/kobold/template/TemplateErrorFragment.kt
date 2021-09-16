package com.kokatto.kobold.template

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.kokatto.kobold.R

class TemplateErrorFragment : Fragment(R.layout.template_fragment_error) {

    var templateActivityListener: TemplateActivityListener? = null
    private var reloadButton: Button? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reloadButton = view.findViewById(R.id.reload_button)
        reloadButton?.let { button -> button.setOnClickListener { onButtonClicked(button) } }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            templateActivityListener = context as TemplateActivityListener
        } catch (castException: ClassCastException) {
            // Listener cannot be attached
        }
    }

    private fun onButtonClicked(view: View) {
        when (view.id) {
            R.id.reload_button -> {
                templateActivityListener?.openDataListFragment()
            }
        }
    }


}
