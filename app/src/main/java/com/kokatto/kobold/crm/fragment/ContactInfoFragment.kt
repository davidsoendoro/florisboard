package com.kokatto.kobold.crm.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.kokatto.kobold.api.model.basemodel.ContactModel
import com.kokatto.kobold.api.model.request.PostContactRequest
import com.kokatto.kobold.crm.ContactViewModel
import com.kokatto.kobold.crm.component.ChannelInfoComponent
import com.kokatto.kobold.databinding.FragmentContactInfoBinding
import com.kokatto.kobold.extension.addSeparator
import com.kokatto.kobold.extension.removeThousandSeparatedString
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.utility.CurrencyUtility
import dev.patrickgold.florisboard.ime.core.OVER_40_DELAY
import dev.patrickgold.florisboard.ime.core.UNDER_40_DELAY
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class ContactInfoFragment(val contact: ContactModel?) : Fragment() {

    private lateinit var binding: FragmentContactInfoBinding
    private val contactViewModel = ContactViewModel()
    private var initialDebtValue: String = "0"
    private var isLoading = AtomicBoolean(false)


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
            initialDebtValue = contact.debt.toString()

            if (binding.switchHasDebt.isChecked) {
                binding.layoutDebtAmount.visibility = View.VISIBLE
                binding.textDebAmount.setText(CurrencyUtility.currencyFormatterNoPrepend(contact.debt))
            } else {
                binding.layoutDebtAmount.visibility = View.GONE
                binding.textDebAmount.setText("")
            }

            if (contact.channels.size > 0) {
                contact.channels.forEach {
                    val channelInfo = ChannelInfoComponent(requireContext(), it)
                    binding.layoutChannelCard.addView(channelInfo)
                }
            } else {
                binding.layoutChannelCard.visibility = View.GONE
            }

            initialDebtValue = contact.debt.toString()
        }

        binding.switchHasDebt.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.layoutDebtAmount.isVisible = isChecked
            if (isChecked.not())
                postSaveDebt("0")
        }

        binding.textDebAmount.doAfterTextChanged {
            val timer = Timer()
            timer.schedule(
                object : TimerTask() {
                    override fun run() {
                        postSaveDebt(
                            if (it.toString() == "")
                                "0"
                            else
                                it.toString()
                        )
                    }
                },
                if (it!!.length <= 40)
                    UNDER_40_DELAY
                else
                    OVER_40_DELAY
            )

        }

        binding.textDebAmount.addSeparator(binding.textDebAmount, ".", ",")
    }

    override fun onPause() {
        super.onPause()
//        if data is changed
        if (initialDebtValue != binding.textDebAmount.text.toString().removeThousandSeparatedString()
            && isLoading.get().not() && binding.textDebAmount.text.toString().length > 0
        ) {
            postSaveDebt(binding.textDebAmount.text.toString())
        }
    }

    fun postSaveDebt(string: String) {
        var stringTemp = string.removeThousandSeparatedString()

        if (stringTemp.isNullOrBlank()) {
            stringTemp = "0"
        }

        contactViewModel.update(contact!!._id,
            PostContactRequest(debt = stringTemp.toDouble()),
            onLoading = {
                isLoading.set(it)
            },
            onSuccess = {
//                requireActivity().showSnackBar(it)
                showToast("Berhasil merubah data!")
                initialDebtValue = stringTemp
            },
            onError = {
//                requireActivity().showSnackBar(it)
                showToast(it)
            }
        )
    }
}
