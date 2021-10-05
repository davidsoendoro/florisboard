package com.kokatto.kobold.login.dialog

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kokatto.kobold.R
import com.kokatto.kobold.databinding.DialogChangeOtpBinding
import com.kokatto.kobold.extension.RoundedBottomSheet
import com.kokatto.kobold.extension.showKeyboard
import com.kokatto.kobold.extension.showSnackBar

class DialogChangeNumber : RoundedBottomSheet() {

    val TAG = "DialogChangeNumber"

    fun newInstance(): DialogChangeNumber {
        return DialogChangeNumber()
    }

    var onComplete: ((phone: String) -> Unit)? = null
    var onClose: ((Boolean) -> Unit)? = null
    var onShow: ((Boolean) -> Unit)? = null

    private var uiBinding: DialogChangeOtpBinding? = null
    private var imm: InputMethodManager? = null

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
            closeDialog()
        }

        setDefaultColor(uiBinding!!.edittextNewNumber)

        uiBinding?.edittextNewNumber?.showKeyboard()
        uiBinding?.edittextNewNumber?.requestFocus()

        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        return binding.root
    }

    override fun onDestroyView() {
        uiBinding = null
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, InputMethodManager.HIDE_IMPLICIT_ONLY)
        super.onDestroyView()
    }

    fun openDialog(fragmentManager: FragmentManager) {
        uiBinding?.edittextNewNumber?.showKeyboard()
        uiBinding?.edittextNewNumber?.requestFocus()
        this.show(fragmentManager, TAG)
    }

    fun closeDialog() {
        uiBinding?.edittextNewNumber?.text?.clear()
        onClose?.invoke(true)
        dismiss()
    }

    private fun onKeyEdit(view: View, keyCode: Int?, event: KeyEvent?): Boolean {
        when (view.id) {
            R.id.edittext_new_number -> {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event?.action == KeyEvent.ACTION_DOWN) {

                    if(uiBinding?.edittextNewNumber?.text.toString().length > 0){
                        onComplete?.invoke(uiBinding?.edittextNewNumber?.text.toString())
                        dismiss()
                    } else {
                        //showSnackBar(requireView(), resources.getString(R.string.kobold_into_empty_phone), R.color.snackbar_error )
                    }

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

