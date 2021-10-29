package com.kokatto.kobold.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.DashboardSessionExpiredEventHandler
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.component.DashboardThemeActivity
import com.kokatto.kobold.crm.dialog.DialogLoadingSmall
import com.kokatto.kobold.dashboard.component.CardComponentComplete
import com.kokatto.kobold.dashboard.component.CardComponentNew
import com.kokatto.kobold.databinding.ActivityDashboardMasterProfitBinding
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.showToast
import timber.log.Timber

class DashboardMasterProfitActivity : DashboardThemeActivity() {

    private lateinit var binding: ActivityDashboardMasterProfitBinding
    private lateinit var tutorialViewModel: TutorialViewModel

    private val MasterManage: MutableList<CardComponentNew> = arrayListOf()
    private val MasterProfit: MutableList<CardComponentNew> = arrayListOf()
    private val MasterCompleted: MutableList<CardComponentComplete> = arrayListOf()
    private var emptyNote: TextView? = null

    private val loading = DialogLoadingSmall(this)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardMasterProfitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(getDrawable(R.drawable.kobold_main_close))

        tutorialViewModel = TutorialViewModel()
        loading.startLoading()

        binding.koboldDashboardProfitMasterContent.completedFeatures = 0
        binding.koboldDashboardProfitMasterContent.totalFeatures = 0

        emptyNote = findViewById<TextView>(R.id.kobold_DashboardProfitMasterCompletedNoneLabel)
        refreshProgress()

    }

    private fun setupCard(complete: Int, total: Int) {
        binding.koboldDashboardProfitMasterContent.apply {

            koboldDashboardProfitMasterManageSellHeaderLayout.setOnClickListener {
                if (koboldDashboardProfitMasterManageSellContentLayout.visibility == View.GONE) {
                    koboldDashboardProfitMasterManageSellContentLayout.visibility = View.VISIBLE
                    koboldDashboardProfitMasterManageSellHeaderArrow.rotation = -90f
                } else {
                    koboldDashboardProfitMasterManageSellContentLayout.visibility = View.GONE
                    koboldDashboardProfitMasterManageSellHeaderArrow.rotation = 90f
                }
            }
            koboldDashboardProfitMasterProfitSupportHeaderLayout.setOnClickListener {
                if (koboldDashboardProfitMasterProfitSupportContentLayout.visibility == View.GONE) {
                    koboldDashboardProfitMasterProfitSupportContentLayout.visibility = View.VISIBLE
                    koboldDashboardProfitMasterProfitSupportHeaderArrow.rotation = -90f
                } else {
                    koboldDashboardProfitMasterProfitSupportContentLayout.visibility = View.GONE
                    koboldDashboardProfitMasterProfitSupportHeaderArrow.rotation = 90f
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

            setIndicatorComplete(complete, koboldDashboardProfitMasterBannerFinishedImg)

            MasterManage.forEach {
                koboldDashboardProfitMasterManageSellContentLayout.addView(it)
            }

            MasterProfit.forEach {
                koboldDashboardProfitMasterProfitSupportContentLayout.addView(it)
            }

            MasterCompleted.forEach {
                koboldDashboardProfitMasterCompletedTutorialContentLayout.addView(it)
            }

            loading.isDismiss()
        }
    }

    private fun setIndicatorComplete(complete: Int = 0, imageView: ImageView) {
        when (complete) {
            6 -> {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@DashboardMasterProfitActivity,
                        R.drawable.ic_kobold_done_complete
                    )
                )
            }
            in (3..5) -> {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@DashboardMasterProfitActivity,
                        R.drawable.ic_kobold_done_progress_half
                    )
                )
            }
            else -> {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@DashboardMasterProfitActivity,
                        R.drawable.ic_kobold_done_progress_0
                    )
                )
            }
        }
    }

    private fun submitCompleteAction(code: String) {
        tutorialViewModel.updateTutorialProgress(code,
            onSuccess = {
                refreshProgress()
                showToast("Selamat Jadi Cuan di Koala")
            },
            onError = {
                if (ErrorResponseValidator.isSessionExpiredResponse(it))
                    DashboardSessionExpiredEventHandler(this).onSessionExpired()
                else
                    showSnackBar(it, R.color.snackbar_error)
            }
        )
    }

    private fun refreshProgress() {

        clearList()

        tutorialViewModel.getTutorialProgress(
            onSuccess = {
                Timber.d("Result: $it")
                binding.koboldDashboardProfitMasterContent.completedFeatures = it.data.complete
                binding.koboldDashboardProfitMasterContent.totalFeatures = it.data.total


                it.data.contents.forEach {

                    if (it.topic.equals("Buat kelola jualan", true) && it.status.equals("new")) {
                        MasterManage.add(CardComponentNew(this, it, onCompleteAction = { submitCompleteAction(it) }))
                    } else if (it.topic.equals("Buat pendukung cuan", true) && it.status.equals("new")) {
                        MasterProfit.add(CardComponentNew(this, it, onCompleteAction = { submitCompleteAction(it) }))
                    } else {
                        MasterCompleted.add(CardComponentComplete(this, it))
                    }
                }

                if(MasterCompleted.size <= 0){
                    binding.koboldDashboardProfitMasterContent.koboldDashboardProfitMasterCompletedTutorialContentLayout
                        .addView(emptyNote)
                }

                setupCard(it.data.complete, it.data.total)
            },
            onError = {
                if (ErrorResponseValidator.isSessionExpiredResponse(it))
                    DashboardSessionExpiredEventHandler(this).onSessionExpired()
                else
                    showSnackBar(it, R.color.snackbar_error)
            }
        )
    }

    private fun clearList() {
        MasterManage.clear()
        MasterProfit.clear()
        MasterCompleted.clear()
        binding.koboldDashboardProfitMasterContent.koboldDashboardProfitMasterManageSellContentLayout.removeAllViews()
        binding.koboldDashboardProfitMasterContent.koboldDashboardProfitMasterProfitSupportContentLayout.removeAllViews()
        binding.koboldDashboardProfitMasterContent.koboldDashboardProfitMasterCompletedTutorialContentLayout.removeAllViews()

    }
}
