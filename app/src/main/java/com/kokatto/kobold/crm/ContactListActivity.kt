package com.kokatto.kobold.crm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.DashboardSessionExpiredEventHandler
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.api.model.basemodel.ContactModel
import com.kokatto.kobold.component.DashboardThemeActivity
import com.kokatto.kobold.component.DovesRecyclerViewPaginator
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.constant.ContactSortEnum
import com.kokatto.kobold.crm.adapter.ContactListRecyclerAdapter
import com.kokatto.kobold.crm.dialog.DialogContactSort
import com.kokatto.kobold.crm.dialog.DialogLoadingSmall
import com.kokatto.kobold.databinding.ActivityContactBinding
import com.kokatto.kobold.extension.addRipple
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.showToast
import java.util.concurrent.atomic.AtomicBoolean

class ContactListActivity : DashboardThemeActivity() {
    private lateinit var binding: ActivityContactBinding
    private var contactEmpty: Boolean = true
    private var sortingType: String = ContactSortEnum.NEWEST.code

    private var contactViewModel: ContactViewModel? = ContactViewModel()
    private var contactsList: MutableList<ContactModel> = ArrayList()
    private var recyclerAdapter: ContactListRecyclerAdapter? = null
    private val loading = DialogLoadingSmall(this)

    private var spinnerContactSort: DialogContactSort? = DialogContactSort()
    private var selectedSort: ContactSortEnum = ContactSortEnum.NEWEST

    private val isLast = AtomicBoolean(false)

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showContactImport()
            } else {
                showSnackBar("Import dari phonebook tidak bisa digunakan", R.color.snackbar_error)
            }
        }

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

        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.koboldContactContent.apply {
            /////////ini
            buttonAddContact.setOnClickListener {
                val dialogContactMenu = DialogContactMenu().newInstance()
                dialogContactMenu.show(supportFragmentManager, dialogContactMenu.TAG)

                dialogContactMenu.onImportClick = {
                    dialogContactMenu.dismiss()
                    checkContactImportView()
                }

                dialogContactMenu.onManualClick = {
                    dialogContactMenu.dismiss()
                    showContactManualview()
                }
            }

            binding.koboltContactAddContactFab.setOnClickListener {
                val dialogContactMenu = DialogContactMenu().newInstance()
                dialogContactMenu.show(supportFragmentManager, dialogContactMenu.TAG)

                dialogContactMenu.onImportClick = {
                    dialogContactMenu.dismiss()
                    checkContactImportView()
                }

                dialogContactMenu.onManualClick = {
                    dialogContactMenu.dismiss()
                    showContactManualview()
                }
            }

            layoutSort.addRipple()

            layoutSort.setOnClickListener {
                spinnerContactSort = DialogContactSort().newInstance()
                spinnerContactSort!!.openSelector(supportFragmentManager, selectedSort)
                spinnerContactSort!!.onConfirmClick = {
                    selectedSort = it
                    textSorting.text = it.desc
                    sortingType = it.code
                    callAPISearch(1, "", sortingType)
                }
            }
        }

        binding.koboldContactListSearchImg.setOnClickListener {
            if (contactEmpty.not()) {
                startActivity(Intent(this@ContactListActivity, ContactSearchActivity::class.java))
            }
        }

        DovesRecyclerViewPaginator(
            recyclerView = binding.koboldContactContent.recyclerViewContact,
            isLoading = { true },
            loadMore = {
                callAPISearch(it + 1, "", sortingType)
            },
            onLast = { isLast.get() }
        ).run {
            threshold = 3
        }

//        callAPISearch(1, "", sortingType)
    }

    override fun onResume() {
        super.onResume()
        callAPISearch(1, "", sortingType)
    }

    private fun showContactManualview() {
        startActivity(Intent(this@ContactListActivity,AddContactActivity ::class.java))
    }

    private fun checkContactImportView() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showContactImport()
        } else requestReadContactPermission()
    }

    private fun requestReadContactPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    private fun showContactImport() {
        startActivity(Intent(this, ContactImportActivity::class.java))
    }

    private fun callAPISearch(page: Int = 1, valueToSearch: String, sorting: String) {
        contactViewModel?.getPaginated(page, 10, sorting, valueToSearch,
            onLoading = {
                showLoading(it)
            },
            onSuccess =
            {
                if (page <= 1) {
                    contactsList.clear()
                }

                if (it.data.contents.isNotEmpty()) {
                    contactEmpty = false
                    contactsList.addAll(it.data.contents)
                    bindAdapterContact(contactsList)
                    binding.koboldContactContent.koboldContactListEmptyLayout.visibility = View.GONE
                    binding.koboldContactContent.koboldContactListLayout.visibility = View.VISIBLE
                } else {
                    binding.koboldContactContent.koboldContactListEmptyLayout.visibility = View.VISIBLE
                    binding.koboldContactContent.koboldContactListLayout.visibility = View.GONE
                    contactEmpty = true
                }
            },
            onError = {
                if (ErrorResponseValidator.isSessionExpiredResponse(it))
                    DashboardSessionExpiredEventHandler(this).onSessionExpired()
                else
                    showSnackBar(it, R.color.snackbar_error)
            }
        )
    }

    private fun bindAdapterContact(list: MutableList<ContactModel>) {
        recyclerAdapter = ContactListRecyclerAdapter(list)
        binding.koboldContactContent.recyclerViewContact.adapter = recyclerAdapter
        recyclerAdapter!!.notifyDataSetChanged()
        recyclerAdapter?.onItemClick = {
            val intent = Intent(this, ContactInfoActivity::class.java)
            intent.putExtra(ActivityConstantCode.EXTRA_DATA, it)
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean = false) {
        if (isLoading) {
            loading.startLoading()
        } else {
            loading.isDismiss()
        }
    }

}
