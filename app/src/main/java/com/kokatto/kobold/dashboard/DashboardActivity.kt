package com.kokatto.kobold.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.kokatto.kobold.R
import com.kokatto.kobold.component.CommonViewPagerAdapter
import com.kokatto.kobold.dashboardcheckshippingcost.CheckShippingcost
import com.kokatto.kobold.dashboardcreatetransaction.CreateTransactionActivity
import com.kokatto.kobold.databinding.ActivityDashboardBinding
import com.kokatto.kobold.setting.SettingActivity
import com.kokatto.kobold.template.TemplateActivity
import dev.patrickgold.florisboard.settings.AboutActivity
import dev.patrickgold.florisboard.settings.SettingsMainActivity

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private var viewPager: ViewPager2? = null
    private lateinit var adapter: CommonViewPagerAdapter
    private var onboardingCompleted = false
    private var keyboardActivated = false
    private var totalFragments = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewPager = findViewById(R.id.kobold_DashboardWelcomeViewPager)
        setSupportActionBar(binding.toolbar)

        adapter = CommonViewPagerAdapter(this)
        if (!onboardingCompleted) {
            //add onboardingFragment to viewpager adapter
            adapter.addFragment(DashboardOnboardingFragment(), "")
            totalFragments++
        }
        if (!keyboardActivated) {
            //add activatekeyboard to view pager adapter
            adapter.addFragment(ActivateKeyboardFragment(), "")
            totalFragments++
        }
        if (totalFragments == 0) {
            binding.koboldDashboardContent.koboldDashboardWelcomeViewPager.visibility = View.GONE
        }

        viewPager?.adapter = adapter

        binding.koboldDashboardSettingsButton.setOnClickListener {
            //TODO: change activity after ready
            //Toast.makeText(this, "Activity still not ready", Toast.LENGTH_LONG).show()
            startActivity(Intent(this@DashboardActivity, SettingActivity::class.java))
        }
        binding.koboldDashboardContent.apply {
            koboldDashboardSellerKeyboardActivateButton.setOnClickListener {
                startActivity(Intent(this@DashboardActivity, SettingsMainActivity::class.java))
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
            koboldDashboardContactCustomerServiceButton.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/6281951245122"))
                startActivity(browserIntent)
            }
            koboldDashboardDeliverOrderLayout.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.bukalapak.com/bukasend/"))
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
                startActivity(Intent(this@DashboardActivity, SettingsMainActivity::class.java))
            }
        }

    }

}
