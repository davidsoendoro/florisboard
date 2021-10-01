package com.kokatto.kobold.template

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kokatto.kobold.R
import com.kokatto.kobold.chattemplate.ChatTemplateViewModel
import com.kokatto.kobold.extension.showToast

class TemplateDialogDelete : BottomSheetDialogFragment() {

    val TAG = "DeleteBottomDialog"

    companion object {
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

    var onCancelClick: ((Boolean) -> Unit)? = null
    var onConfirmClick: ((id: String) -> Unit)? = null

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

        buttonConfirm?.setOnClickListener {
            onConfirmClick?.invoke(argID!!)
        }

        buttonCancel?.setOnClickListener {
            onCancelClick?.invoke(true)
        }

        //buttonConfirm?.let { button -> button.setOnClickListener { onButtonClicked(button) } }
        //buttonCancel?.let { button -> button.setOnClickListener { onButtonClicked(button) } }
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

    fun openDialog(fragmentManager: FragmentManager) {
        this.show(fragmentManager, TAG)
    }

    fun closeDialog() {
        dismiss()
    }

    fun performLoading(isLoading: Boolean) {
        buttonConfirm?.isVisible = !isLoading
        buttonCancel?.isVisible = !isLoading
        buttonConfirmLoading?.isVisible = isLoading
    }


}
