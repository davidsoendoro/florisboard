package com.kokatto.kobold.crm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kokatto.kobold.databinding.DialogAddContactBinding
import com.kokatto.kobold.extension.RoundedBottomSheet
import com.kokatto.kobold.extension.addRipple

class DialogContactMenu : RoundedBottomSheet() {

    val TAG = "DialogContactMenu"

    fun newInstance(): DialogContactMenu {
        return DialogContactMenu()
    }

    private var uiBinding: DialogAddContactBinding? = null

    var onManualClick: ((Boolean) -> Unit)? = null
    var onImportClick: ((Boolean) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogAddContactBinding.inflate(inflater, container, false)
        uiBinding = binding

        uiBinding!!.addContactManualLayout.addRipple()
        uiBinding!!.importContactLayout.addRipple()

        uiBinding?.addContactManualLayout?.setOnClickListener {
            onManualClick?.invoke(true)
        }

        uiBinding?.importContactLayout?.setOnClickListener {
            onImportClick?.invoke(true)
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

}
