package com.kokatto.kobold.template

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kokatto.kobold.R
import com.kokatto.kobold.extension.RoundedBottomSheet

class TemplateDialogSelection : RoundedBottomSheet() {

    private var mListener: TemplateDialogSelectionClickListener? = null
    private var templatePick: String? = null

    companion object {
        const val TAG = "TemplateDialogSelection"
        const val TEMPLATE = "TEMPLATE"
        fun newInstance(_id: String): TemplateDialogSelection {
            return TemplateDialogSelection().apply {
                arguments = Bundle().apply {
                    putString(TEMPLATE, _id)
                }
            }
        }
    }

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
        val txt0 = view.findViewById<TextView>(R.id.txt0)
        val txt1 = view.findViewById<TextView>(R.id.txt1)
        val txt2 = view.findViewById<TextView>(R.id.txt2)
        val txt3 = view.findViewById<TextView>(R.id.txt3)
        val txt4 = view.findViewById<TextView>(R.id.txt4)
        val txt5 = view.findViewById<TextView>(R.id.txt5)

        txt0.let { button -> button.setOnClickListener { onButtonClicked(button) } }
        txt1.let { button -> button.setOnClickListener { onButtonClicked(button) } }
        txt1.let { button -> button.setOnClickListener { onButtonClicked(button) } }
        txt2.let { button -> button.setOnClickListener { onButtonClicked(button) } }
        txt3.let { button -> button.setOnClickListener { onButtonClicked(button) } }
        txt4.let { button -> button.setOnClickListener { onButtonClicked(button) } }
        txt5.let { button -> button.setOnClickListener { onButtonClicked(button) } }
        backButton.let { button -> button.setOnClickListener { onButtonClicked(button) } }

        when (templatePick) {
            "Pesan Pembuka" -> setSelected(txt0)
            "Form Pesanan" -> setSelected(txt1)
            "Ucapan Terimakasih" -> setSelected(txt2)
            "Ketersediaan Barang" -> setSelected(txt3)
            "Cek Barang" -> setSelected(txt4)
            "Custom" -> setSelected(txt5)
            else -> {
                setSelected(txt0)
            }
        }

    }

    private fun onButtonClicked(view: View) {
        when (view.id) {
            R.id.txt0 -> {
                mListener!!.onItemClick("Pesan Pembuka")
                dismiss()
            }
            R.id.txt1 -> {
                mListener!!.onItemClick("Form Pesanan")
                dismiss()
            }
            R.id.txt2 -> {
                mListener!!.onItemClick("Ucapan Terimakasih")
                dismiss()
            }
            R.id.txt3 -> {
                mListener!!.onItemClick("Ketersediaan Barang")
                dismiss()
            }
            R.id.txt4 -> {
                mListener!!.onItemClick("Cek Barang")
                dismiss()
            }
            R.id.txt5 -> {
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

        arguments?.getString(TemplateDialogSelection.TEMPLATE)?.let {
            templatePick = it
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    private fun setSelected(textView: TextView) {
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checked, 0)
        textView.setTypeface(textView.typeface, Typeface.BOLD)

    }

}
