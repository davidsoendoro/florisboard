package com.kokatto.kobold.crm

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.core.view.isVisible
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.DashboardSessionExpiredEventHandler
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.api.model.basemodel.ContactModel
import com.kokatto.kobold.component.DashboardThemeActivity
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.crm.adapter.ContactListRecyclerAdapter
import com.kokatto.kobold.databinding.ActivityContactSearchBinding
import com.kokatto.kobold.extension.onTextChanged
import com.kokatto.kobold.extension.showKeyboard
import com.kokatto.kobold.extension.showSnackBar

class ContactSearchActivity : DashboardThemeActivity() {

    private lateinit var binding: ActivityContactSearchBinding

    private var contactViewModel: ContactViewModel? = ContactViewModel()
    private var contactsList: MutableList<ContactModel> = ArrayList()
    private var recyclerAdapter: ContactListRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityContactSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchEdittext.showKeyboard()

        binding.clearButton.setOnClickListener {
            if (binding.searchEdittext.text.isNotEmpty()) {
                binding.searchEdittext.text = null
                binding.searchEdittext.requestFocus()
            }
        }

        binding.searchEdittext.onTextChanged {
            if (it.length > 0) {
                binding.clearButton.visibility = View.VISIBLE
                binding.searchIcon.visibility = View.GONE
            } else {
                binding.clearButton.visibility = View.GONE
                binding.searchIcon.visibility = View.VISIBLE
            }
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.searchEdittext.setOnKeyListener { v, keyCode, event -> onKeyEdit(v, keyCode, event) }

        callAPISearch("")
    }

    private fun onKeyEdit(view: View, keyCode: Int?, event: KeyEvent?): Boolean {
        when (view.id) {
            R.id.search_edittext -> {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event?.action == KeyEvent.ACTION_DOWN) {
                    callAPISearch(binding.searchEdittext.text.toString())
                    return true
                }
            }
        }
        return false
    }

    private fun callAPISearch(valueToSearch: String) {
        contactViewModel?.getPaginated(1, 10, "", valueToSearch,
            onLoading = {
                binding.fullscreenLoading.isVisible = it
            },
            onSuccess =
            {
                contactsList.clear()
                if (it.data.contents.isNotEmpty()) {
                    contactsList.addAll(it.data.contents)
                    bindAdapterContact(contactsList)
                    binding.layoutNotFound.visibility = View.GONE
                    binding.layoutData.visibility = View.VISIBLE
                } else {
                    binding.layoutNotFound.visibility = View.VISIBLE
                    binding.layoutData.visibility = View.GONE
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
        binding.searchContactRecyclerView.adapter = recyclerAdapter
        recyclerAdapter!!.notifyDataSetChanged()
        recyclerAdapter?.onItemClick = {
            val intent = Intent(this, ContactInfoActivity::class.java)
            intent.putExtra(ActivityConstantCode.EXTRA_DATA, it)
            startActivity(intent)
        }
    }

}
