package com.kokatto.kobold.crm

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.ContactChannelModel
import com.kokatto.kobold.api.model.basemodel.ContactModel
import com.kokatto.kobold.api.model.basemodel.getContactList
import com.kokatto.kobold.api.model.request.PostContactRequest
import com.kokatto.kobold.crm.adapter.AddContactRecyclerAdapter
import com.kokatto.kobold.dashboardcreatetransaction.autocompleteadapter.ContactAutocompleteAdapter
import com.kokatto.kobold.databinding.ActivityAddContactBinding
import com.kokatto.kobold.extension.createBottomSheetDialog
import timber.log.Timber
import java.lang.Exception


class AddContactActivity : AppCompatActivity(), AddContactRecyclerAdapter.OnItemClickListener {
    lateinit var uiBinding: ActivityAddContactBinding
    private val dataList = ArrayList<ContactChannelModel>()
    private val adapter = AddContactRecyclerAdapter(dataList, this)
    val newItem = ContactChannelModel()
    val contactViewModel = ContactViewModel()
    val contactRequest: PostContactRequest = PostContactRequest()
    private var contactAutocompleteAdapter: ContactAutocompleteAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)
        uiBinding = ActivityAddContactBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        dataList.add(newItem)

        val recyclerView: RecyclerView = findViewById(R.id.add_contact_recycler_view)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(false)

        val contactList = getContactList(this)
        Timber.d("[CONTACT] getContactList: $contactList")
        contactAutocompleteAdapter = ContactAutocompleteAdapter(this, getContactList(this))

        uiBinding.edittextAddContactName.setAdapter(contactAutocompleteAdapter)
        uiBinding.edittextAddContactName.setOnItemClickListener { adapterView, view, i, l ->
            try {
                val contact = adapterView.getItemAtPosition(i) as ContactModel
                uiBinding.edittextAddContactName.setText(contact.name)
                uiBinding.edittextAddContactPhone.setText(contact.phoneNumber)
                uiBinding.edittextAddContactEmail.setText(contact.email)
                uiBinding.edittextAddContactAddress.setText(contact.address)
            } catch (e: Exception) {

            }
        }

        uiBinding.koboltAddContactAddChannelText.setOnClickListener {
            dataList.add(newItem)
            adapter.notifyDataSetChanged()
        }

        uiBinding.backButton.setOnClickListener {
            createConfirmationDialog()
        }

        uiBinding.submitButton.setOnClickListener {
            Toast.makeText(this, "TEST", Toast.LENGTH_LONG).show()
            contactViewModel.create(
                contactRequest,
                onSuccess = {
                    Toast.makeText(this, "Berhasil menambah kontak.", Toast.LENGTH_LONG).show()
                },
                onError = {
                    Toast.makeText(this, "Kontak gagal ditambahkan, silakan coba lagi.", Toast.LENGTH_LONG).show()
                }
            )
        }

        uiBinding.edittextAddContactName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                contactAutocompleteAdapter?.notifyDataSetChanged()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                contactRequest.name = s.toString()
            }
        })

        uiBinding.edittextAddContactPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
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
        Toast.makeText(this, "Item clicked", Toast.LENGTH_SHORT).show()
        val clickedItem: ContactChannelModel = dataList[position]
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
        }

        bottomDialog.show()
    }

    fun onClickCalled() {
        Toast.makeText(this, "Apel", Toast.LENGTH_SHORT).show()

    }

}
