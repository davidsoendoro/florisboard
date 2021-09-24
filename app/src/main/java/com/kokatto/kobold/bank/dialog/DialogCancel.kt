package com.kokatto.kobold.bank.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kokatto.kobold.R
import com.kokatto.kobold.databinding.DialogConfirmBinding
import com.kokatto.kobold.extension.RoundedBottomSheet

class DialogCancel : RoundedBottomSheet() {

    val TAG = "DialogSent"

    fun newInstance(): DialogCancel? {
        return DialogCancel()
    }

    var onConfirmClick: ((Boolean) -> Unit)? = null

    lateinit var binding: DialogConfirmBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.confirmTitle.text = resources.getString(R.string.kobold_bank_dialog_cancel_title)
        binding.confirmDesc.text = resources.getString(R.string.kobold_bank_dialog_cancel_desc)
        binding.confirmButton.text = resources.getString(R.string.kobold_bank_dialog_cancel_action_confirm)
        binding.confirmButton.text = resources.getString(R.string.kobold_bank_dialog_cancel_action_confirm)
        binding.confirmCancelButton.text = resources.getString(R.string.kobold_bank_dialog_cancel_action_cancel)
    }

    fun closeDialog() {
        dismiss()
    }

}
