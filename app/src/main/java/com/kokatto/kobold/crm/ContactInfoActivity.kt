package com.kokatto.kobold.crm

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.material.tabs.TabLayoutMediator
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.ContactModel
import com.kokatto.kobold.component.CommonViewPagerAdapter
import com.kokatto.kobold.component.DashboardThemeActivity
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.crm.fragment.ContactInfoFragment
import com.kokatto.kobold.crm.fragment.ContactTransactionFragment
import com.kokatto.kobold.dashboardcreatetransaction.InputActivity
import com.kokatto.kobold.databinding.ActivityContactInfoBinding
import com.kokatto.kobold.extension.addRipple

class ContactInfoActivity : DashboardThemeActivity() {
    private lateinit var binding: ActivityContactInfoBinding
    private lateinit var adapter: CommonViewPagerAdapter

    private var currentContact: ContactModel? = null
    private val tabTexts = mutableListOf("Transaksi", "Info Kontak")

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initView() {
        binding.textEdit.addRipple()
        binding.layoutEmail.addRipple()
        binding.layoutSms.addRipple()
        binding.layoutWhatsapp.addRipple()
        binding.layoutPhone.addRipple()

        getIntentData()

        if (currentContact?.name.isNullOrBlank()) {
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.layoutEmptyState.emptyCreateButton.visibility = View.VISIBLE
            binding.layoutNotEmpty.visibility = View.GONE

            binding.layoutEmptyState.emptyCreateButton.setOnClickListener {
                startActivity(Intent(this, InputActivity::class.java))
                finish()
            }

        } else {
            binding.layoutNotEmpty.visibility = View.VISIBLE
            binding.layoutEmpty.visibility = View.GONE
            initFragmentPager()
            initTab()
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.textEdit.setOnClickListener {
            val intent = Intent(this, EditContactActivity::class.java)
            intent.putExtra(ActivityConstantCode.EXTRA_DATA, currentContact)
            startActivity(intent)
        }
    }

    private fun initTab() {
        TabLayoutMediator(binding.tabLayout, binding.viewpagerLayout, true) { tab, position ->
            tab.text = tabTexts[position]
        }.attach()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initFragmentPager() {
        adapter = CommonViewPagerAdapter(this)
        adapter.addFragment(ContactTransactionFragment(currentContact), 0)
        adapter.addFragment(ContactInfoFragment(currentContact), 1)
        binding.viewpagerLayout.adapter = adapter
        binding.viewpagerLayout.requestTransform()

        if (currentContact?.email.isNullOrBlank()) {
            binding.iconEmail.setColorFilter(R.color.secondary_30)
            binding.textEmail.setTextColor(resources.getColor(R.color.secondary_30, null))
        }
    }

    private fun getIntentData() {
        currentContact = intent.getParcelableExtra<ContactModel>(ActivityConstantCode.EXTRA_DATA)

        if (currentContact?.name.isNullOrBlank()) {
            binding.textName.text = currentContact?.phoneNumber
        } else {
            binding.textName.text = currentContact?.name
        }
    }

}
