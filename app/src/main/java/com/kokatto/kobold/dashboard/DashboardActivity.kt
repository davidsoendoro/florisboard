package com.kokatto.kobold.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.request.PageName
import com.kokatto.kobold.api.model.request.PageVisitTrackerRequest
import com.kokatto.kobold.component.CommonViewPagerAdapter
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.crm.ContactListActivity
import com.kokatto.kobold.dashboardcheckshippingcost.CheckShippingcost
import com.kokatto.kobold.dashboardcreatetransaction.CreateTransactionActivity
import com.kokatto.kobold.databinding.ActivityDashboardBinding
import com.kokatto.kobold.setting.HelpActivity
import com.kokatto.kobold.setting.SettingActivity
import com.kokatto.kobold.template.TemplateActivity
import com.kokatto.kobold.tracker.viewmodel.TrackerViewModel
import dev.patrickgold.florisboard.settings.SettingsMainActivity
import dev.patrickgold.florisboard.setup.SetupActivity
import dev.patrickgold.florisboard.util.checkIfImeIsSelected
import timber.log.Timber

interface DashboardActivityListener {
    fun onLaterClick(type: Int)
    fun onOKButtonClick(type: Int)
}

class DashboardActivity : AppCompatActivity(), DashboardActivityListener {
    private lateinit var binding: ActivityDashboardBinding
    private var viewPager: ViewPager2? = null
    private lateinit var adapter: CommonViewPagerAdapter
    private var onboardingCompleted = false
    private var keyboardActivated = false
    private var totalFragments = 0
    private lateinit var trackerViewModel: TrackerViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        trackerViewModel = TrackerViewModel()
        trackerViewModel.postPageVisitTracker(PageVisitTrackerRequest(PageName.DASHBOARD_OPEN))
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewPager = findViewById(R.id.kobold_DashboardWelcomeViewPager)
        setSupportActionBar(binding.toolbar)
        keyboardActivated = checkIfImeIsSelected(this)
        binding.koboldDashboardContent.keyboardActivated = keyboardActivated
        Timber.d("keyboardActivated: $keyboardActivated")
        adapter = CommonViewPagerAdapter(this)
        if (!onboardingCompleted) {
            //add onboardingFragment to viewpager adapter
            adapter.addFragment(DashboardOnboardingFragment(), ActivityConstantCode.ONBOARDING_FRAGMENT_CODE)
            totalFragments++
        }
        if (!keyboardActivated) {
            //add activatekeyboard to view pager adapter
            adapter.addFragment(ActivateKeyboardFragment(), ActivityConstantCode.ACTIVATE_KEYBOARD_FRAGMENT_CODE)
            totalFragments++
        }
        if (totalFragments == 0) {
            binding.koboldDashboardContent.koboldDashboardWelcomeViewPager.visibility = View.GONE
        }

        viewPager?.adapter = adapter
        viewPager?.requestTransform()

        binding.koboldDashboardSettingsButton.setOnClickListener {
            //TODO: change activity after ready
            //Toast.makeText(this, "Activity still not ready", Toast.LENGTH_LONG).show()
            startActivity(Intent(this@DashboardActivity, SettingActivity::class.java))
        }
        binding.koboldDashboardTutorialButton.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, DashboardMasterProfitActivity::class.java))
        }
        binding.koboldDashboardContent.apply {
            koboldDashboardSellerKeyboardActivateButton.setOnClickListener {
                startActivity(Intent(this@DashboardActivity, SettingsMainActivity::class.java))
            }

            koboldDashboardManageContactLayout.setOnClickListener {
                //Toast.makeText(this@DashboardActivity, "CRM Feature is coming soon!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@DashboardActivity, ContactListActivity::class.java))
            }

            koboldDashboardManageTransactionLayout.setOnClickListener {
                startActivity(Intent(this@DashboardActivity, CreateTransactionActivity::class.java))
            }

            koboldDashboardChatTemplateLayout.setOnClickListener {
                startActivity(Intent(this@DashboardActivity, TemplateActivity::class.java))
            }

            koboldCheckDeliveryFeeLayout.setOnClickListener {
                startActivity(Intent(this@DashboardActivity, CheckShippingcost::class.java))
            }

            koboldDashboardSupportButton.setOnClickListener {
                startActivity(Intent(this@DashboardActivity, HelpActivity::class.java))
            }

            koboldDashboardContactCustomerServiceButton.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/6285692689187"))
                startActivity(browserIntent)
            }

            koboldDashboardDeliverOrderLayout.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://m.bukalapak.com/bukasend/form?referrer=c-commerce-kokatto"))
                startActivity(browserIntent)
            }

            koboldDashboardSellerKeyboardWatchVideoButton.setOnClickListener {
                //TODO: change youtube url
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/c/bukalapak/videos"))
                startActivity(intent)
            }

            koboldDashboardSellerKeyboardActivateButton.setOnClickListener {
                startActivity(Intent(this@DashboardActivity, SettingsMainActivity::class.java))
            }

            koboldDashboardActivateMagicKeyboardBannerBtn.setOnClickListener {
                startActivity(Intent(this@DashboardActivity, SetupActivity::class.java))
            }
        }
    }

    override fun onLaterClick(type: Int) {
        Timber.d("Later Button Clicked: $type")
        when(type){
            1 -> {
//                startActivity(Intent(this@DashboardActivity, DashboardMasterProfitActivity::class.java))
            }
        }
        //remove fragment on position, refresh viewpager
        adapter.removeFragmentByCode(10000+type)
    }

    override fun onOKButtonClick(type: Int) {
        Timber.d("Ok Button Clicked: $type")
        when(type){
            1 -> {
                startActivity(Intent(this@DashboardActivity, DashboardMasterProfitActivity::class.java))
            }
            2 -> {
                startActivity(Intent(this@DashboardActivity, SettingsMainActivity::class.java))
            }
        }
        //remove fragment on position, refresh viewpager
        adapter.removeFragmentByCode(10000+type)
    }
}
