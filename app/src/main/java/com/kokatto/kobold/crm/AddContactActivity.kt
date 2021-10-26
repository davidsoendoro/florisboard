package com.kokatto.kobold.crm

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.ContactChannelModel
import com.kokatto.kobold.api.model.request.PostContactRequest
import com.kokatto.kobold.crm.adapter.AddContactRecyclerAdapter
import com.kokatto.kobold.crm.adapter.AddContactRecyclerAdapter.Companion.getData
import com.kokatto.kobold.databinding.ActivityAddContactBinding
import com.kokatto.kobold.extension.createBottomSheetDialog
import com.kokatto.kobold.extension.vertical
import timber.log.Timber
import java.lang.Exception
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.registration.RegistrationActivity
import com.kokatto.kobold.registration.spinner.DialogBusinessFieldSelector
import kotlinx.serialization.json.JsonNull.content
import android.R.string
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.kokatto.kobold.extension.showSnackBar
import dev.patrickgold.florisboard.util.getActivity


class AddContactActivity : AppCompatActivity(), AddContactRecyclerAdapter.OnItemClickListener {
    lateinit var uiBinding: ActivityAddContactBinding
    private val dataList = ArrayList<ContactChannelModel>()
    private val adapter = AddContactRecyclerAdapter(dataList, this)
    val contactViewModel = ContactViewModel()
    val contactRequest: PostContactRequest = PostContactRequest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)
        uiBinding = ActivityAddContactBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }
//        buat mastiin kalo datalist yang dibuat kosong
        dataList.clear()
        dataList.add(ContactChannelModel())

        uiBinding.addContactRecyclerView.adapter = adapter
        uiBinding.addContactRecyclerView.vertical()
//        recyclerView.setHasFixedSize(true)

        uiBinding.koboltAddContactAddChannelText.setOnClickListener {
            dataList.add(ContactChannelModel())
            adapter.notifyDataSetChanged()
        }

        uiBinding.backButton.setOnClickListener {
//            ini panggil onBackPressed aja supaya bisa antisipasi user pencet tombol di atas atau pencet back dari hp mereka
            onBackPressed()
        }

        uiBinding.submitButton.setOnClickListener {
            contactRequest.channels.clear()
            contactRequest.channels.addAll(dataList)
            contactRequest.channels.addAll(adapter.getData())
            contactViewModel.create(
                request = contactRequest,
                onSuccess = {
                    showToast("Berhasil")
                    showSnackBar("Berhasil menambah kontak.")
                    finish()
                },
                onError = {
                    showToast("Gagal")
                    showSnackBar("Kontak gagal ditambahkan, silakan coba lagi.", R.color.snackbar_error)
                }
            )
        }

        uiBinding.edittextAddContactName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isSaveButtonValid()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                contactRequest.name = s.toString()
                if(s.toString().length > 100){
                    uiBinding.edittextAddContactNameError.visibility = View.VISIBLE
                }else{
                    uiBinding.edittextAddContactNameError.visibility = View.GONE
                }
            }
        })

        uiBinding.edittextAddContactPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isSaveButtonValid()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                contactRequest.phoneNumber = s.toString()
            }
        })

        uiBinding.edittextAddContactEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                contactRequest.email = s.toString()
            }
        })

        uiBinding.edittextAddContactAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                contactRequest.address = s.toString()
            }
        })

    }

    override fun onDataChange(data: ContactChannelModel?, index: Int) {
        if (data == null) {
            dataList.removeAt(index)
        } else {
            if (data.type == "WhatsApp")
                data.account = uiBinding.edittextAddContactPhone.text.toString()
                dataList[index] = data
        }

        if (dataList.isEmpty())
            dataList.add(ContactChannelModel())

        uiBinding.addContactRecyclerView.post {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onBackPressed() {
        createConfirmationDialog()
    }

    fun isSaveButtonValid(): Boolean {
        var isInputValid = contactRequest.phoneNumber != "" && uiBinding.edittextAddContactNameError.visibility == View.GONE

        if (isInputValid)
            uiBinding.submitButton.setCardBackgroundColor(resources.getColor(R.color.kobold_blue_button))
        else
            uiBinding.submitButton.setCardBackgroundColor(resources.getColor(R.color.kobold_blue_button_disabled))

        return isInputValid
    }

    private fun createConfirmationDialog() {
        val bottomDialog = createBottomSheetDialog(
            layoutInflater.inflate(
                R.layout.dialog_confirm_back,
                null
            )
        )

        val acceptButton = bottomDialog.findViewById<MaterialCardView>(R.id.kobold_add_contact_back_btn_yes)
        val discardButton = bottomDialog.findViewById<MaterialCardView>(R.id.kobold_add_contact_back_btn_no)

        acceptButton?.setOnClickListener {
            bottomDialog.dismiss()
        }

        discardButton?.setOnClickListener {
            bottomDialog.dismiss()
            finish()
        }

        bottomDialog.show()
    }
}
