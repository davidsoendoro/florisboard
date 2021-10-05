package com.kokatto.kobold.login.dialog

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kokatto.kobold.R
import com.kokatto.kobold.databinding.DialogChangeOtpBinding
import com.kokatto.kobold.extension.RoundedBottomSheet
import com.kokatto.kobold.extension.hideKeyboard
import com.kokatto.kobold.extension.showKeyboard

class DialogChangeNumber : RoundedBottomSheet() {

    val TAG = "DialogChangeNumber"

    fun newInstance(): DialogChangeNumber {
        return DialogChangeNumber()
    }

    var onComplete: ((phone: String) -> Unit)? = null

    private var uiBinding: DialogChangeOtpBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = DialogChangeOtpBinding.inflate(inflater, container, false)
        uiBinding = binding

        uiBinding?.edittextNewNumber?.setOnKeyListener { v, keyCode, event ->
            onKeyEdit(v, keyCode, event)
        }

        uiBinding?.closeButton?.setOnClickListener {
            uiBinding?.edittextNewNumber?.hideKeyboard()
        }

        setDefaultColor(uiBinding!!.edittextNewNumber)
        return binding.root
    }

    override fun onDestroyView() {
        uiBinding = null
        super.onDestroyView()
    }

    fun openDialog(fragmentManager: FragmentManager) {
        this.show(fragmentManager, TAG)
        uiBinding?.edittextNewNumber?.requestFocus()
        uiBinding?.edittextNewNumber?.showKeyboard()
    }

    fun closeDialog() {
        dismiss()
    }

    private fun onKeyEdit(view: View, keyCode: Int?, event: KeyEvent?): Boolean {
        when (view.id) {
            R.id.edittext_new_number -> {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event?.action == KeyEvent.ACTION_DOWN) {
                    onComplete?.invoke(uiBinding?.edittextNewNumber?.text.toString())
                }
            }
        }
        return false
    }

    private fun setDefaultColor(textInputEditText: TextInputEditText) {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_focused),
            intArrayOf()
        )

        val colors = intArrayOf(
            Color.parseColor("#2B4AC7"),
            Color.parseColor("#D6D6D6")
        )
        val colorList = ColorStateList(states, colors)

        val inputlayout = textInputEditText.parent.parent as TextInputLayout
        inputlayout.setBoxStrokeColorStateList(colorList)

    }

}

