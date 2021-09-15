package com.kokatto.kobold.template

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kokatto.kobold.R

class TemplateDialogSelection : BottomSheetDialogFragment() {

    private var mListener: TemplateDialogSelectionClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.template_dialog_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton = view.findViewById<ImageView>(R.id.back_button)
        val btn0 = view.findViewById<Button>(R.id.btn0)
        val btn1 = view.findViewById<Button>(R.id.btn1)
        val btn2 = view.findViewById<Button>(R.id.btn2)
        val btn3 = view.findViewById<Button>(R.id.btn3)
        val btn4 = view.findViewById<Button>(R.id.btn4)
        val btn5 = view.findViewById<Button>(R.id.btn5)

        btn0.let { button -> button.setOnClickListener { onButtonClicked(button) } }
        btn1.let { button -> button.setOnClickListener { onButtonClicked(button) } }
        btn1.let { button -> button.setOnClickListener { onButtonClicked(button) } }
        btn2.let { button -> button.setOnClickListener { onButtonClicked(button) } }
        btn3.let { button -> button.setOnClickListener { onButtonClicked(button) } }
        btn4.let { button -> button.setOnClickListener { onButtonClicked(button) } }
        btn5.let { button -> button.setOnClickListener { onButtonClicked(button) } }
        backButton.let { button -> button.setOnClickListener { onButtonClicked(button) } }

    }

    private fun onButtonClicked(view: View) {
        when (view.id) {
            R.id.btn0 -> {
                mListener!!.onItemClick("Pesan Pembuka")
                dismiss()
            }
            R.id.btn1 -> {
                mListener!!.onItemClick("Form Pesanan")
                dismiss()
            }
            R.id.btn2 -> {
                mListener!!.onItemClick("Ucapan Terimakasih")
                dismiss()
            }
            R.id.btn3 -> {
                mListener!!.onItemClick("Ketersediaan Barang")
                dismiss()
            }
            R.id.btn4 -> {
                mListener!!.onItemClick("Cek Barang")
                dismiss()
            }
            R.id.btn5 -> {
                mListener!!.onItemClick("Custom")
                dismiss()
            }
            R.id.back_button -> {
                dismiss()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mListener = if (context is TemplateDialogSelectionClickListener) {
            context
        } else {
            throw RuntimeException(
                context.toString() + "Must implement TemplateDialogSelectionClickListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

}
