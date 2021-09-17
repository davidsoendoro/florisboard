package com.kokatto.kobold.template

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kokatto.kobold.R
import com.kokatto.kobold.chattemplate.ChatTemplateViewModel
import com.kokatto.kobold.extension.showToast

class TemplateDialogDelete : BottomSheetDialogFragment() {


    companion object {
        const val TAG = "DeleteBottomDialog"
        const val ARG_ID = "ARG_ID"
        fun newInstance(_id: String): TemplateDialogDelete {
            return TemplateDialogDelete().apply {
                arguments = Bundle().apply {
                    putString(ARG_ID, _id)
                }
            }
        }
    }

    private var buttonConfirm: Button? = null
    private var buttonCancel: Button? = null
    private var argID: String? = null
    private var buttonConfirmLoading: ProgressBar? = null

    private var chatTemplateViewModel: ChatTemplateViewModel? = ChatTemplateViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.template_dialog_confirm_delete, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getString(ARG_ID)?.let {
            argID = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonConfirm = view.findViewById(R.id.confirm_button)
        buttonCancel = view.findViewById(R.id.cancel_button)
        buttonConfirmLoading = view.findViewById(R.id.confirm_button_loading)
        buttonConfirmLoading?.isVisible = false

        buttonConfirm?.let { button -> button.setOnClickListener { onButtonClicked(button) } }
        buttonCancel?.let { button -> button.setOnClickListener { onButtonClicked(button) } }
    }

    private fun onButtonClicked(view: View) {
        when (view.id) {
            R.id.confirm_button -> {
                buttonConfirm?.isVisible = false
                buttonCancel?.isVisible = false
                buttonConfirmLoading?.isVisible = true
                chatTemplateViewModel?.deleteAutotextById(
                    argID!!,
                    onSuccess = { it ->
                        requireActivity().finish()
                        showToast(resources.getString(R.string.template_delete_success))
                        buttonConfirm?.isVisible = true
                        buttonCancel?.isVisible = true
                        buttonConfirmLoading?.isVisible = false
                    },
                    onError = {
                        buttonConfirm?.isVisible = true
                        buttonCancel?.isVisible = true
                        buttonConfirmLoading?.isVisible = false
                        showToast(it)
                    }
                )

            }
            R.id.cancel_button -> {
                dismiss()
            }
        }
    }
}
