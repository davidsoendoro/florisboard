package com.kokatto.kobold.crm

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
        recyclerView.isNestedScrollingEnabled = false

        uiBinding.titleText.text = "Edit kontak"

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
                contactViewModel?.update(
                    id = "617108a4b96a3d0009df9635",
                    request = contactRequest,
                    onSuccess = {
                        showToast("Berhasil mengubah data!")
                        //finish()
                    },
                    onError = {
                        showToast("Gagal mengubah data!")
                        showSnackBar(it, R.color.snackbar_error)
                    }
                )
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

    override fun onDataChange(data: ContactChannelModel?, index: Int) {
        val clickedItem: ContactChannelModel = dataList[index]

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

        acceptButton?.setOnClickListener {
            bottomDialog.dismiss()

//            val i = Intent(this@AddContactActivity, LoginActivity ::class.java)        // Specify any activity here e.g. home or splash or login etc
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            i.putExtra("EXIT", true)
//            startActivity(i)
//            finish()

        }

        discardButton?.setOnClickListener {
            bottomDialog.dismiss()
            startActivity(Intent(this@EditContactActivity, ContactListActivity::class.java))
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


