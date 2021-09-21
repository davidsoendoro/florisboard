package com.kokatto.kobold.dashboardcreatetransaction.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kokatto.kobold.R

class DialogFinish : BottomSheetDialogFragment() {

    val TAG = "DialogFinish"

    fun newInstance(): DialogFinish? {
        return DialogFinish()
    }

    var onConfirmClick: ((Boolean) -> Unit)? = null

    private var progressBarLoading: ProgressBar? = null
    private var confirmBtn: TextView? = null
    private var cancelBtn: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_confirm_finish, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val confirmBtn = view.findViewById<TextView>(R.id.confirm_button)
        val cancelBtn = view.findViewById<TextView>(R.id.cancel_button)

        cancelBtn?.setOnClickListener {
            dismiss()
        }

        confirmBtn?.setOnClickListener {
            onConfirmClick?.invoke(true)
        }
    }

    fun openDialog(fragmentManager: FragmentManager) {
        this.show(fragmentManager, TAG)
    }

    fun progressLoading(_isLoading: Boolean) {
        if (_isLoading) {
            progressBarLoading?.visibility = View.VISIBLE
            confirmBtn?.visibility = View.GONE
            cancelBtn?.visibility = View.GONE
        } else {
            progressBarLoading?.visibility = View.GONE
            confirmBtn?.visibility = View.VISIBLE
            cancelBtn?.visibility = View.VISIBLE
        }
    }

    fun closeDialog() {
        dismiss()
    }

}
