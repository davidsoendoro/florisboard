package com.kokatto.kobold.bank

import android.os.Bundle
import android.view.View
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.kokatto.kobold.R
import com.kokatto.kobold.bank.dialog.DialogBankDelete
import com.kokatto.kobold.databinding.FragmentDataEmptyBinding

class BankEmptyFragment : Fragment(R.layout.fragment_data_empty) {

    val TAG = "BankEmptyFragment"

    fun newInstance(): DialogBankDelete? {
        return DialogBankDelete()
    }

    private var binding: FragmentDataEmptyBinding? = null

    var onActionClick: ((Boolean) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.imgPlaceholder?.setImageResource(R.drawable.img_medium_fun_bukabank_account)
        binding?.titlePlaceholder?.setText(R.string.kobold_bank_empty_title)
        binding?.descPlaceholder?.setText(R.string.kobold_bank_empty_desc)

        view.findViewById<CardView>(R.id.action_placeholder).setOnClickListener {
            onActionClick?.invoke(true)
        }
    }
}
