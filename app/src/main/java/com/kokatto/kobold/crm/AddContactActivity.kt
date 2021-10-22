package com.kokatto.kobold.crm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.kokatto.kobold.R
import com.kokatto.kobold.databinding.ActivityAddContactBinding
import com.kokatto.kobold.extension.createBottomSheetDialog
import com.kokatto.kobold.login.LoginActivity
import com.kokatto.kobold.persistance.AppPersistence
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kokatto.kobold.api.model.basemodel.BusinessFieldModel
import com.kokatto.kobold.api.model.basemodel.ContactChannelModel
import com.kokatto.kobold.api.model.basemodel.toBundle
import com.kokatto.kobold.api.model.basemodel.toTextFormat
import com.kokatto.kobold.api.model.request.PostContactRequest
import com.kokatto.kobold.crm.adapter.AddContactRecyclerAdapter
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.registration.RegistrationActivity
import com.kokatto.kobold.registration.spinner.DialogBusinessFieldSelector
import kotlinx.serialization.json.JsonNull.content
import android.R.string
import dev.patrickgold.florisboard.util.getActivity


class AddContactActivity : AppCompatActivity(), AddContactRecyclerAdapter.OnItemClickListener {
    lateinit var uiBinding: ActivityAddContactBinding
    private val dataList = ArrayList<ContactChannelModel>()
    private val adapter = AddContactRecyclerAdapter(dataList, this)
    val newItem = ContactChannelModel()
    val contactViewModel = ContactViewModel()
    val contactRequest: PostContactRequest = PostContactRequest()
    var count: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)
        uiBinding = ActivityAddContactBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }
        isSaveButtonValid()

        dataList.add(newItem)

        val recyclerView: RecyclerView = findViewById(R.id.add_contact_recycler_view)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.setNestedScrollingEnabled(false);

        uiBinding.koboltAddContactAddChannelText.setOnClickListener {
            dataList.add(newItem)
            //adapter.notifyItemChanged(count)
            adapter.notifyItemInserted(count)
            count++
        }

        uiBinding.backButton.setOnClickListener {
            createConfirmationDialog()
        }

        uiBinding.submitButton.setOnClickListener {
            contactRequest.channels.clear()
            contactRequest.channels.addAll(dataList)
            contactViewModel.create(
                request = contactRequest,
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
            startActivity(Intent(this@AddContactActivity, ContactListActivity::class.java))
            finish()
        }

        bottomDialog.show()
    }

    fun getWANumber(): String{
        var phone:String = contactRequest.phoneNumber
        var number:String = uiBinding.edittextAddContactPhone.text.toString()
        //Toast.makeText(this, "TEST $phone $number", Toast.LENGTH_LONG).show()
        return number
    }
}
