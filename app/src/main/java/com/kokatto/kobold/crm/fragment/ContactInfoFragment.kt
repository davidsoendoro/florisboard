package com.kokatto.kobold.crm.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.ContactModel
import com.kokatto.kobold.crm.component.ChannelInfoComponent
import com.kokatto.kobold.databinding.FragmentContactInfoBinding
import com.kokatto.kobold.extension.addDrawableStart
import com.kokatto.kobold.extension.addSeparator
import com.kokatto.kobold.utility.CurrencyUtility
import java.util.*

class ContactInfoFragment(val contact: ContactModel?) : Fragment() {

    private lateinit var binding: FragmentContactInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactInfoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (contact !== null) {
            binding.contactInfoPhone.text = contact.phoneNumber
            binding.contactInfoEmail.text = contact.email
            binding.contactInfoAddress.text = contact.address
            binding.switchHasDebt.isChecked = contact.debt > 0

            if (binding.switchHasDebt.isChecked) {
                binding.layoutDebtAmount.visibility = View.VISIBLE
                binding.textDebAmount.setText(CurrencyUtility.currencyFormatterNoPrepend(contact.debt))
            } else {
                binding.layoutDebtAmount.visibility = View.GONE
                binding.textDebAmount.setText("")
            }

            if(contact.channels.size > 0){
                contact.channels.forEach {
                    val channelInfo = ChannelInfoComponent(requireContext(), it)
                    binding.layoutChannelCard.addView(channelInfo)
                }
            } else {
                binding.layoutChannelCard.visibility = View.GONE
            }
        }

        binding.switchHasDebt.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.layoutDebtAmount.isVisible = isChecked
        }

        binding.textDebAmount.addSeparator(binding.textDebAmount,".",",")
    }


}
