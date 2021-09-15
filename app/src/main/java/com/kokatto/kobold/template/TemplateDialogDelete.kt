package com.kokatto.kobold.template

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kokatto.kobold.R

class TemplateDialogDelete : BottomSheetDialogFragment() {

    companion object{
        const val TAG = "DeleteBottomDialog"
        fun newInstance():TemplateDialogDelete{
            return TemplateDialogDelete()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.template_dialog_confirm_delete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}
