package com.kokatto.kobold.template

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import dev.patrickgold.florisboard.R

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
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction
                    .setReorderingAllowed(true)
                    .replace(
                        R.id.template_fragment_container_view,
                        TemplateInputFragment()
                    )
                    .commit()
            }
        }
    }
}
