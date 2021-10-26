package com.kokatto.kobold.crm

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.ContactChannelModel
import com.kokatto.kobold.api.model.basemodel.ContactModel
import com.kokatto.kobold.api.model.request.PostContactRequest
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.crm.adapter.AddContactRecyclerAdapter
import com.kokatto.kobold.databinding.ActivityAddContactBinding
import com.kokatto.kobold.extension.createBottomSheetDialog
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.extension.vertical

class EditContactActivity : AppCompatActivity(), AddContactRecyclerAdapter.OnItemClickListener {
    lateinit var uiBinding: ActivityAddContactBinding
    private val dataList = ArrayList<ContactChannelModel>()
    private val adapter = AddContactRecyclerAdapter(dataList, this)
    var contactRequest: PostContactRequest = PostContactRequest()
    val contactViewModel = ContactViewModel()
    var contactModel = ContactModel()

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

        uiBinding.titleText.text = "Edit kontak"

        uiBinding.koboltAddContactAddChannelText.setOnClickListener {
            dataList.add(ContactChannelModel())
            adapter.notifyDataSetChanged()
        }

        uiBinding.backButton.setOnClickListener {
            onBackPressed()
        }

        uiBinding.submitButton.setOnClickListener {
            contactRequest.channels.clear()
            contactRequest.channels.addAll(dataList)
            intent.getStringExtra(ActivityConstantCode.EXTRA_DATA)?.let { it ->
                contactViewModel?.update(
                    id = it,
                    request = contactRequest,
                    onSuccess = {
                        showSnackBar("Kontak berhasil diubah.")
                    },
                    onError = {
                        showSnackBar("Kontak gagal diubah, silakan coba lagi.", R.color.snackbar_error)
                    }
                )
            }
            val i = Intent(this@EditContactActivity, ContactListActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
            finish()
        }

        uiBinding.edittextAddContactName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
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
        val acceptText = bottomDialog.findViewById<TextView>(R.id.kobold_add_contact_back_text_yes)

        acceptText?.setText("Lanjutkan ubah kontak")

        acceptButton?.setOnClickListener {
            bottomDialog.dismiss()
        }

        discardButton?.setOnClickListener {
            bottomDialog.dismiss()
            finish()
        }

        bottomDialog.show()
    }

    override fun onResume() {
        super.onResume()
        intent.getStringExtra(ActivityConstantCode.EXTRA_DATA)?.let {
            contactViewModel?.findById(
                id = it,
                onSuccess = {
                    //on data success loaded from backend
                    uiBinding.edittextAddContactName.setText(
                        if (it.name.isNullOrEmpty()) "-"
                        else it.name)

                    uiBinding.edittextAddContactPhone.setText(
                        if (it.phoneNumber.isNullOrEmpty()) "-"
                        else it.phoneNumber)

                    uiBinding.edittextAddContactEmail.setText(
                        if (it.email.isNullOrEmpty()) "-"
                        else it.email)

                    uiBinding.edittextAddContactAddress.setText(
                        if (it.address.isNullOrEmpty()) "-"
                        else it.address)

                    contactModel = it

                    if(it.channels.isNullOrEmpty()) {
                        dataList.clear()
                        dataList.add(ContactChannelModel())
                        adapter.notifyDataSetChanged()
                    }else{
                        dataList.clear()
                        dataList.addAll(it.channels)
                        adapter.notifyDataSetChanged()
                    }

                },
                onError = {
                    //on data error when loading from backend
                    showSnackBar(it)
                }
            )
        }
    }
}


