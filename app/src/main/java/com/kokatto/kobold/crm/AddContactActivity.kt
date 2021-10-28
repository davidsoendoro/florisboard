package com.kokatto.kobold.crm

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.ContactChannelModel
import com.kokatto.kobold.api.model.basemodel.ContactModel
import com.kokatto.kobold.api.model.basemodel.MerchantModel
import com.kokatto.kobold.api.model.request.PostContactRequest
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.crm.adapter.AddContactRecyclerAdapter
import com.kokatto.kobold.dashboardcreatetransaction.InputActivity
import com.kokatto.kobold.databinding.ActivityAddContactBinding
import com.kokatto.kobold.extension.createBottomSheetDialog
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.extension.vertical


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
        isSaveButtonValid()
        dataList.clear()
        dataList.add(ContactChannelModel())

        uiBinding.addContactRecyclerView.adapter = adapter
        uiBinding.addContactRecyclerView.vertical()

        uiBinding.koboltAddContactAddChannelText.setOnClickListener {
            uiBinding.addContactManualLayout.clearFocus()

            dataList.add(ContactChannelModel())
            adapter.notifyDataSetChanged()
        }

        uiBinding.backButton.setOnClickListener {
            onBackPressed()
        }

        uiBinding.submitButton.setOnClickListener {
            uiBinding.addContactManualLayout.clearFocus()

            contactRequest.channels.clear()
            contactRequest.channels.addAll(dataList.filter { it.type != "" && it.account != "" })
            contactViewModel.create(
                request = contactRequest,
                onSuccess = {
                    val response: Boolean = it.isProfileUpdated
                    val intentResult = Intent()
                    if (response == false){
                        intentResult.putExtra("snackbarMessage", "Berhasil menambah kontak.")
                        intentResult.putExtra("snackbarBackground", R.color.snackbar_default)
                        intentResult.putExtra("snackbarResponse", "false")
                    }else{
                        intentResult.putExtra("snackbarMessage", "Kontak sudah pernah disimpan sebelumnya")
                        intentResult.putExtra("snackbarBackground", R.color.snackbar_default)
                        intentResult.putExtra("snackbarResponse", "true")
                        intentResult.putExtra(ActivityConstantCode.EXTRA_DATA, it.contact)
                    }
                    setResult(ActivityConstantCode.RESULT_ADD_CONTACT_SUCCESS, intentResult)
                    finish()
                },
                onError = {
                    val intentResult = Intent()
                    intentResult.putExtra("snackbarMessage", "Kontak gagal ditambahkan, silakan coba lagi.")
                    intentResult.putExtra("snackbarBackground", R.color.snackbar_error)
                    setResult(ActivityConstantCode.RESULT_ADD_CONTACT_FAILED, intentResult)
                    finish()
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
                if (s.toString().length > 100) {
                    uiBinding.edittextAddContactNameError.visibility = View.VISIBLE
                } else {
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
        try {
            if (data == null) {
                if (dataList.size <= 1) {
                    dataList[0] = ContactChannelModel()
                    adapter.notifyItemChanged(0)
                } else {
                    dataList.removeAt(index)
                    //adapter.notifyItemChanged(index)
                    adapter.notifyDataSetChanged()
                    //adapter.notifyItemRemoved(index)
                }
            } else {
                if (data.type == "WhatsApp" && data.account == "")
                    data.account = uiBinding.edittextAddContactPhone.text.toString()

                dataList[index] = data

                adapter.notifyItemChanged(index)
            }

        } catch (e: Exception) {
//            adapter.notifyDataSetChanged()

        }
//        uiBinding.addContactRecyclerView.post {
////            adapter.notifyDataSetChanged()
//        }
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
