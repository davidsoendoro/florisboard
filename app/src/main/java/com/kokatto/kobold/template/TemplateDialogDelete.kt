package com.kokatto.kobold.template

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kokatto.kobold.R
import com.kokatto.kobold.extension.showToast

class TemplateDialogDelete : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "DeleteBottomDialog"
        fun newInstance(): TemplateDialogDelete {
            return TemplateDialogDelete()
        }
    }

    private var buttonConfirm: Button? = null
    private var buttonCancel: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.template_dialog_confirm_delete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonConfirm = view.findViewById(R.id.confirm_button)
        buttonCancel = view.findViewById(R.id.cancel_button)

        buttonConfirm?.let { button -> button.setOnClickListener { onButtonClicked(button) } }
        buttonCancel?.let { button -> button.setOnClickListener { onButtonClicked(button) } }
    }

    private fun onButtonClicked(view: View) {
        when (view.id) {
            R.id.confirm_button -> {
                requireActivity().finish()
                showToast(resources.getString(R.string.template_delete_success))
            }
            R.id.cancel_button -> {
                dismiss()
            }
        }
    }
}
