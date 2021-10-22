package com.kokatto.kobold.crm

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.DashboardSessionExpiredEventHandler
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.api.model.basemodel.ContactChannelModel
import com.kokatto.kobold.api.model.basemodel.ContactModel
import com.kokatto.kobold.api.model.basemodel.MerchantModel
import com.kokatto.kobold.api.model.request.PostContactRequest
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.crm.adapter.AddContactRecyclerAdapter
import com.kokatto.kobold.dashboardcreatetransaction.InputActivity
import com.kokatto.kobold.databinding.ActivityAddContactBinding
import com.kokatto.kobold.extension.createBottomSheetDialog
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.setting.SettingViewModel

class EditContactActivity : AppCompatActivity(), AddContactRecyclerAdapter.OnItemClickListener {
    lateinit var uiBinding: ActivityAddContactBinding
    private val dataList = ArrayList<ContactChannelModel>()
    private val adapter = AddContactRecyclerAdapter(dataList, this)
    val newItem = ContactChannelModel()
    var contactRequest: PostContactRequest = PostContactRequest()
    var contactViewModel: ContactViewModel? = null
    var contactModel = ContactModel()
    var count: Int = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)
        uiBinding = ActivityAddContactBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }
        isSaveButtonValid()
        contactViewModel = ContactViewModel()

        val recyclerView: RecyclerView = findViewById(R.id.add_contact_recycler_view)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.setNestedScrollingEnabled(false);

        uiBinding.titleText.setText("Edit kontak")

        uiBinding.koboltAddContactAddChannelText.setOnClickListener {
            dataList.add(newItem)
            //adapter.notifyDataSetChanged()
            adapter.notifyItemChanged(count)
            count++
        }

        uiBinding.backButton.setOnClickListener {
            createConfirmationDialog()
        }

        uiBinding.submitButton.setOnClickListener {
            if (isSaveButtonValid()) {
                intent.getStringExtra(ActivityConstantCode.EXTRA_DATA)?.let { it1 ->
                    contactViewModel?.update(
                        id = it1,
                        request = contactRequest,
                        onSuccess = {
                            showToast("Kontak berhasil diubah.")
                            finish()
                        },
                        onError = {
                            showSnackBar("Kontak gagal diubah, silakan coba lagi.", R.color.snackbar_error)
                        }
                    )
                }
            }
        }

        uiBinding.edittextAddContactName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                contactRequest.name = s.toString()
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

    override fun onItemClick(position: Int) {
        val clickedItem: ContactChannelModel = dataList[position]
    }

    fun isSaveButtonValid(): Boolean {
        var isInputValid = contactRequest.phoneNumber != ""

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
            val i = Intent(this@EditContactActivity, ContactInfoActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.putExtra(ActivityConstantCode.EXTRA_DATA, contactModel)
            startActivity(i)
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
                        dataList.add(newItem)
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


