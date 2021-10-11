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
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.kokatto.kobold.dashboardcheckshippingcost.CheckShippingcost
import com.kokatto.kobold.dashboardcreatetransaction.CreateTransactionActivity
import com.kokatto.kobold.template.TemplateActivity
import dev.patrickgold.florisboard.settings.SettingsMainActivity


class DashboardMasterProfitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardMasterProfitBinding
    private lateinit var tutorialViewModel: TutorialViewModel
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

        tutorialViewModel = TutorialViewModel()
        tutorialViewModel.getTutorialProgress({
            Timber.d("Result: $it")
            binding.koboldDashboardProfitMasterContent.completedFeatures = it.data.complete

            it.data.contents.map {
                //assign to each cardview
            }
        }, {
            Toast.makeText(this, "Failed getting tutorial progress", Toast.LENGTH_LONG).show()
        })

        binding.koboldDashboardProfitMasterContent.apply {
            isChatTemplateDone = false
            isCheckDeliveryFeeDone = false
            isDeliverOrderDone = false
            isManageCustomerDone = true
            isManageTransactionDone = false
            isSellerKeyboardDone = false

            koboldDashboardProfitMasterManageSellHeaderLayout.setOnClickListener {
                if (isManageCustomerDone == false || isManageTransactionDone == false) {
                    if (koboldDashboardProfitMasterManageSellContentLayout.visibility == View.GONE) {
                        koboldDashboardProfitMasterManageSellContentLayout.visibility = View.VISIBLE
                        koboldDashboardProfitMasterManageSellHeaderArrow.rotation = -90f
                    } else {
                        koboldDashboardProfitMasterManageSellContentLayout.visibility = View.GONE
                        koboldDashboardProfitMasterManageSellHeaderArrow.rotation = 90f
                    }
                }
            }

            koboldDashboardProfitMasterProfitSupportHeaderLayout.setOnClickListener {
                if (isSellerKeyboardDone == false || isChatTemplateDone == false || isDeliverOrderDone == false || isCheckDeliveryFeeDone == false) {
                    if (koboldDashboardProfitMasterProfitSupportContentLayout.visibility == View.GONE) {
                        koboldDashboardProfitMasterProfitSupportContentLayout.visibility = View.VISIBLE
                        koboldDashboardProfitMasterProfitSupportHeaderArrow.rotation = -90f
                    } else {
                        koboldDashboardProfitMasterProfitSupportContentLayout.visibility = View.GONE
                        koboldDashboardProfitMasterProfitSupportHeaderArrow.rotation = 90f
                    }
                }
            }

            koboldDashboardProfitMasterCompletedTutorialHeaderLayout.setOnClickListener {
                if (koboldDashboardProfitMasterCompletedTutorialContentLayout.visibility == View.GONE) {
                    koboldDashboardProfitMasterCompletedTutorialContentLayout.visibility = View.VISIBLE
                    koboldDashboardProfitMasterCompletedTutorialHeaderArrow.rotation = -90f
                } else {
                    koboldDashboardProfitMasterCompletedTutorialContentLayout.visibility = View.GONE
                    koboldDashboardProfitMasterCompletedTutorialHeaderArrow.rotation = 90f
                }
            }

            when (completedFeatures) {
                6 -> {
                    koboldDashboardProfitMasterBannerFinishedImg.setImageDrawable(
                        ContextCompat.getDrawable(this@DashboardMasterProfitActivity, R.drawable.ic_kobold_done_complete)
                    )
                }
                in (3..5) -> {
                    koboldDashboardProfitMasterBannerFinishedImg.setImageDrawable(
                        ContextCompat.getDrawable(this@DashboardMasterProfitActivity, R.drawable.ic_kobold_done_progress_half)
                    )
                }
                else -> {
                    koboldDashboardProfitMasterBannerFinishedImg.setImageDrawable(
                        ContextCompat.getDrawable(this@DashboardMasterProfitActivity, R.drawable.ic_kobold_done_progress_0)
                    )
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
