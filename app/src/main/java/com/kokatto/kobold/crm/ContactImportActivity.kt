package com.kokatto.kobold.crm

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.DashboardSessionExpiredEventHandler
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.api.model.basemodel.ContactImportModel
import com.kokatto.kobold.api.model.request.PostBulkContactRequest
import com.kokatto.kobold.component.DashboardThemeActivity
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.crm.adapter.ContactImportRecyclerAdapter
import com.kokatto.kobold.crm.dialog.DialogLoadingSmall
import com.kokatto.kobold.databinding.ActivityContactImportBinding
import com.kokatto.kobold.extension.addRipple
import com.kokatto.kobold.extension.showSnackBar

class ContactImportActivity : DashboardThemeActivity() {
    private lateinit var binding: ActivityContactImportBinding
    private var contactsList: MutableList<ContactImportModel> = ArrayList()
    private var selectedContactsList: MutableList<ContactImportModel> = ArrayList()
    private var filteredContactsList: MutableList<ContactImportModel> = ArrayList()
    private var recyclerAdapter: ContactImportRecyclerAdapter? = null
    private val loading = DialogLoadingSmall(this)
    private var contactViewModel: ContactViewModel? = ContactViewModel()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityContactImportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.koboldContactSelectAll.addRipple()

        retrieveContactsList()

        binding.koboldContactSelectAll.setOnClickListener {
            contactsList.forEachIndexed { index, contactModel ->
                contactModel.isSelected = contactModel.isSelected.not()
                if (contactModel.isSelected) {
                    selectedContactsList.add(contactModel)
                } else {
                    selectedContactsList.remove(contactModel)
                }
                recyclerAdapter?.notifyItemChanged(index)
            }
            validateImportButton()
        }

        binding.clearButton.setOnClickListener {
            binding.searchEdittext.setText("")
        }

        binding.searchEdittext.doAfterTextChanged {
            binding.clearButton.isVisible = it.toString().isNotEmpty()
            binding.koboldContactSearchCancel.isVisible = it.toString().isNotEmpty()
        }

        binding.koboldContactSearchCancel.setOnClickListener {
            binding.searchEdittext.setText("")
            filteredContactsList.clear()
            bindAdapterContact(contactsList)
            binding.recyclerViewContact.visibility = View.VISIBLE
            binding.layoutSeacrhNotfound.visibility = View.GONE
        }

        binding.searchEdittext.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {

                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    doFilteredContact()
                    return true
                }
                return false
            }
        })

        binding.buttonImport.setOnClickListener {
            loading.startLoading()

            contactViewModel?.createBulk(
                selectedContactsList.map {
                    PostBulkContactRequest(it.name, it.phoneNumber)
                },
                onSuccess = {
                    //showSnackBar(resources.getString(R.string.kobold_contact_import_success, it.data.totalRecord))
                    loading.isDismiss()
                    selectedContactsList.clear()
                    filteredContactsList.clear()
                    bindAdapterContact(contactsList)

                    val data = Intent()
                    data.putExtra(ActivityConstantCode.EXTRA_DATA, resources.getString(R.string.kobold_contact_import_success, it.data.totalRecord));
                    setResult(ActivityConstantCode.RESULT_OK_CREATED, data);
                    finish()
                },
                onError = {
                    if (ErrorResponseValidator.isSessionExpiredResponse(it)) {
                        DashboardSessionExpiredEventHandler(this).onSessionExpired()
                    } else {
                        loading.isDismiss()
                        val data = Intent()
                        data.putExtra(ActivityConstantCode.EXTRA_DATA, resources.getString(R.string.kobold_contact_import_failed));
                        setResult(ActivityConstantCode.RESULT_FAILED_SAVE, data);
                        finish()
                    }
                })

        }

        recyclerAdapter?.onItemClick = {
            validateImportButton()
        }

        validateImportButton()
    }

    private fun bindAdapterContact(list: MutableList<ContactImportModel>) {
        recyclerAdapter = ContactImportRecyclerAdapter(list, selectedContactsList)
        binding.recyclerViewContact.adapter = recyclerAdapter
        recyclerAdapter!!.notifyDataSetChanged()
    }

    private fun doFilteredContact() {
        loading.startLoading()
        filteredContactsList.clear()
        contactsList.forEachIndexed { index, contactModel ->
            val searchValue = binding.searchEdittext.text.toString()
            if (contactModel.name.contains(searchValue, true)) {
                filteredContactsList.add(contactModel)
            }
        }

        if (filteredContactsList.isEmpty()) {
            binding.recyclerViewContact.visibility = View.GONE
            binding.layoutSeacrhNotfound.visibility = View.VISIBLE
        } else {
            bindAdapterContact(filteredContactsList)
            binding.recyclerViewContact.visibility = View.VISIBLE
            binding.layoutSeacrhNotfound.visibility = View.GONE
        }
        loading.isDismiss()
        validateImportButton()
    }

    private fun validateImportButton() {
        if (selectedContactsList.isNotEmpty()) {
            binding.submitButtonText.setBackgroundColor(resources.getColor(R.color.kobold_blue_button))
            binding.buttonImport.isEnabled = true
        } else {
            binding.submitButtonText.setBackgroundColor(resources.getColor(R.color.kobold_blue_button_disabled))
            binding.buttonImport.isEnabled = false
        }
    }

    private fun retrieveContactsList() {
        loading.startLoading()
        contactsList = getContacts()

        if (contactsList.isNotEmpty()) {
            binding.recyclerViewContact.visibility = View.VISIBLE
            bindAdapterContact(contactsList)
        } else {
            showSnackBar("Tidak ada kontak untuk diimport", R.color.snackbar_error)
        }

        loading.isDismiss()
    }

    private fun getContacts(): MutableList<ContactImportModel> {
        val list: MutableList<ContactImportModel> = ArrayList()
        val contentResolver: ContentResolver = contentResolver

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val cursor =
            contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                ContactsContract.Contacts.HAS_PHONE_NUMBER + ">0 AND LENGTH(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ")>0",
                null,
                "display_name ASC"
            )

        if (cursor != null && cursor.count > 0) {
            var lastHeader = ""
            while (cursor.moveToNext()) {
                val id =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val mobileNumber =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                val header = getFirstChar(name)

                list.add(ContactImportModel(id, header, name, mobileNumber, false))
            }
            cursor.close()
        }

        return list
    }

    private fun getFirstChar(name: String?): String {
        return name?.get(0).toString()
    }
}
