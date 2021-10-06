package com.kokatto.kobold.login.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kokatto.kobold.R
import com.kokatto.kobold.databinding.DialogConfirmBinding

class DialogCancelRegister : BottomSheetDialogFragment() {

    val TAG = "DialogCancelRegister"

    fun newInstance(): DialogCancelRegister? {
        return DialogCancelRegister()
    }

    var onConfirmClick: ((Boolean) -> Unit)? = null
    var onCancelClick: ((Boolean) -> Unit)? = null

    private var uiBinding: DialogConfirmBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogConfirmBinding.inflate(inflater, container, false)
        uiBinding = binding

        uiBinding?.confirmTitle?.text = resources.getString(R.string.kobold_cancel_registration_title)
        uiBinding?.confirmDesc?.text = resources.getString(R.string.kobold_cancel_registration_desc)
        uiBinding?.confirmButton?.text = resources.getString(R.string.kobold_cancel_registration_action_confirm)
        uiBinding?.confirmCancelButton?.text = resources.getString(R.string.kobold_cancel_registration_action_cancel)

        uiBinding?.confirmButton?.setOnClickListener {
            onConfirmClick?.invoke(true)
        }

        uiBinding?.confirmCancelButton?.setOnClickListener {
            onCancelClick?.invoke(true)
        }

        return binding.root
    }

    override fun onDestroyView() {
        uiBinding = null
        super.onDestroyView()
    }

    fun openDialog(fragmentManager: FragmentManager) {
        this.show(fragmentManager, TAG)
    }

    fun closeDialog() {
        dismiss()
    }

    fun progressLoading(isLoading: Boolean) {
        if (isLoading) {
            uiBinding?.confirmButtonLoading?.visibility = View.VISIBLE
            uiBinding?.confirmButton?.visibility = View.GONE
            uiBinding?.confirmCancelButton?.visibility = View.GONE
        } else {
            uiBinding?.confirmButtonLoading?.visibility = View.GONE
            uiBinding?.confirmButton?.visibility = View.VISIBLE
            uiBinding?.confirmCancelButton?.visibility = View.VISIBLE
        }
    }
}

