package com.kokatto.kobold.dashboard

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.kokatto.kobold.R
import com.kokatto.kobold.databinding.ActivityDashboardMasterProfitBinding
import timber.log.Timber
import android.R.id.toggle
import android.content.Intent
import android.net.Uri
import com.kokatto.kobold.dashboardcheckshippingcost.CheckShippingcost
import com.kokatto.kobold.dashboardcreatetransaction.CreateTransactionActivity
import com.kokatto.kobold.template.TemplateActivity
import dev.patrickgold.florisboard.settings.SettingsMainActivity


class DashboardMasterProfitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardMasterProfitBinding

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

        binding = ActivityDashboardMasterProfitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(getDrawable(R.drawable.kobold_main_close))
        binding.koboldDashboardProfitMasterContent.apply {
            isChatTemplateDone = true
            isCheckDeliveryFeeDone = false
            isDeliverOrderDone = true
            isManageCustomerDone = true
            isManageTransactionDone = false
            isSellerKeyboardDone = true
            completedFeatures = 4

            koboldDashboardProfitMasterManageSellHeaderLayout.setOnClickListener {
                if(isManageCustomerDone == false || isManageTransactionDone == false){
                    if(koboldDashboardProfitMasterManageSellContentLayout.visibility == View.GONE){
                        koboldDashboardProfitMasterManageSellContentLayout.visibility = View.VISIBLE
                        koboldDashboardProfitMasterManageSellHeaderArrow.rotation = -90f
                    } else {
                        koboldDashboardProfitMasterManageSellContentLayout.visibility = View.GONE
                        koboldDashboardProfitMasterManageSellHeaderArrow.rotation = 90f
                    }
                }
            }

            koboldDashboardProfitMasterProfitSupportHeaderLayout.setOnClickListener {
                if(isSellerKeyboardDone == false || isChatTemplateDone == false || isDeliverOrderDone == false || isCheckDeliveryFeeDone == false){
                    if(koboldDashboardProfitMasterProfitSupportContentLayout.visibility == View.GONE ){
                        koboldDashboardProfitMasterProfitSupportContentLayout.visibility = View.VISIBLE
                        koboldDashboardProfitMasterProfitSupportHeaderArrow.rotation = -90f
                    } else {
                        koboldDashboardProfitMasterProfitSupportContentLayout.visibility = View.GONE
                        koboldDashboardProfitMasterProfitSupportHeaderArrow.rotation = 90f
                    }
                }
            }
            koboldDashboardProfitMasterCompletedTutorialHeaderLayout.setOnClickListener {
                if(koboldDashboardProfitMasterCompletedTutorialContentLayout.visibility == View.GONE){
                    koboldDashboardProfitMasterCompletedTutorialContentLayout.visibility = View.VISIBLE
                    koboldDashboardProfitMasterCompletedTutorialHeaderArrow.rotation = -90f
                } else {
                    koboldDashboardProfitMasterCompletedTutorialContentLayout.visibility = View.GONE
                    koboldDashboardProfitMasterCompletedTutorialHeaderArrow.rotation = 90f
                }
            }

            when (completedFeatures) {
                6 -> {
                    koboldDashboardProfitMasterBannerFinishedImg.setImageDrawable(resources.getDrawable(R.drawable.ic_kobold_done_complete, null));
                }
                in(3..5) -> {
                    koboldDashboardProfitMasterBannerFinishedImg.setImageDrawable(resources.getDrawable(R.drawable.ic_kobold_done_progress_half, null));
                }
                else -> {
                    koboldDashboardProfitMasterBannerFinishedImg.setImageDrawable(resources.getDrawable(R.drawable.ic_kobold_done_progress_0, null));
                }
            }

            koboldDashboardProfitMasterManageCustomerFeatureBtn.setOnClickListener {

            }

            koboldDashboardProfitMasterManageTransactionFeatureBtn.setOnClickListener {
                startActivity(Intent(this@DashboardMasterProfitActivity, CreateTransactionActivity::class.java))
            }

            koboldDashboardProfitMasterSellerKeyboardFeatureBtn.setOnClickListener {
                startActivity(Intent(this@DashboardMasterProfitActivity, SettingsMainActivity::class.java))
            }

            koboldDashboardProfitMasterChatTemplateFeatureBtn.setOnClickListener {
                startActivity(Intent(this@DashboardMasterProfitActivity, TemplateActivity::class.java))
            }

            koboldDashboardProfitMasterCheckDeliveryFeeFeatureBtn.setOnClickListener {
                startActivity(Intent(this@DashboardMasterProfitActivity, CheckShippingcost::class.java))
            }

            koboldDashboardProfitMasterDeliverOrderFeatureBtn.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.bukalapak.com/bukasend/"))
                startActivity(browserIntent)
            }
        }

    }
}
