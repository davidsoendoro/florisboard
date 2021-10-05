package com.kokatto.kobold.login.dialog

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.kokatto.kobold.R
import com.kokatto.kobold.databinding.DialogChangeOtpBinding
import com.kokatto.kobold.extension.RoundedBottomSheet
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
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)

        val binding = DialogChangeOtpBinding.inflate(inflater, container, false)
        uiBinding = binding

        uiBinding?.edittextNewNumber?.setOnKeyListener { v, keyCode, event ->
            onKeyEdit(v, keyCode, event)
        }

        uiBinding?.closeButton?.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onDestroyView() {
        uiBinding = null
        super.onDestroyView()
    }

    fun openDialog(fragmentManager: FragmentManager) {
        uiBinding?.edittextNewNumber?.setText("")
        this.show(fragmentManager, TAG)

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

}

