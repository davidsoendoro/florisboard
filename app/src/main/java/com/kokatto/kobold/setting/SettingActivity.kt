package com.kokatto.kobold.setting

//import com.google.android.play.core.review.ReviewInfo
//import com.google.android.play.core.review.ReviewManager
//import com.google.android.play.core.review.ReviewManagerFactory
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.kokatto.kobold.R
import com.kokatto.kobold.bank.BankHomeActivity
import com.kokatto.kobold.dashboard.DashboardActivity
import com.kokatto.kobold.databinding.SettingsActivityBinding
import com.kokatto.kobold.extension.createBottomSheetDialog
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.persistance.AppPersistence

class SettingActivity : AppCompatActivity() {
    private lateinit var ivProfilToko: ImageView
    private lateinit var btnLogOut: Button
    private lateinit var rlBankAccount: RelativeLayout
    private lateinit var rlGiveRating: RelativeLayout
    private lateinit var rlTermCondition: RelativeLayout
    private lateinit var llShareApp: LinearLayout
    private lateinit var tvStorePhone: TextView
    private lateinit var tvStoreName: TextView
    lateinit var uiBinding: SettingsActivityBinding

    var settingViewModel: SettingViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        settingViewModel = SettingViewModel()

        tvStoreName = findViewById(R.id.tv_merchant_store)
        tvStorePhone = findViewById(R.id.tv_merchant_phone)
        settingViewModel?.getMerhcantInfo(
            onLoading = {
//                        on data is loading
//                loadingLayout.isVisible = it
            },
            onSuccess = {
//                        on data success loaded from backend
                    tvStoreName.text = it.name

            },
            onError = {
//                on data error when loading from backend
                showSnackBar(it)
            }
        )

        uiBinding = SettingsActivityBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }
        uiBinding.backButton.setOnClickListener {
            onBackPressed()
        }

        ivProfilToko = findViewById(R.id.kobold_setting_store_profile)
        ivProfilToko.setOnClickListener {
            startActivity(Intent(this@SettingActivity, SettingProfilTokoActivity::class.java))
        }

        btnLogOut = findViewById(R.id.kobold_SettingLogoutButton)
        btnLogOut.setOnClickListener{
            createConfirmationDialog()
        }

        rlBankAccount = findViewById(R.id.kubold_open_bank_account)
        rlBankAccount.setOnClickListener{
            startActivity(Intent(this@SettingActivity, BankHomeActivity::class.java))
        }

        llShareApp = findViewById(R.id.kubold_card_share)
        llShareApp.setOnClickListener{
            createShareDialog()
        }

        rlGiveRating = findViewById(R.id.kubold_open_give_rating)
        rlGiveRating.setOnClickListener{
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=xyz.appmaker.cqshec")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=xyz.appmaker.cqshec")))
            }
        }

        rlTermCondition = findViewById(R.id.kubold_open_term_conditions)
        rlTermCondition.setOnClickListener{

        }


    }

    private fun createConfirmationDialog() {
        val bottomDialog = createBottomSheetDialog(
            layoutInflater.inflate(
                R.layout.dialog_confirm_log_out,
                null
            )
        )

        val acceptButton = bottomDialog.findViewById<MaterialCardView>(R.id.logout_button)
        val discardButton = bottomDialog.findViewById<MaterialCardView>(R.id.cancel_button)

        acceptButton?.setOnClickListener {
            bottomDialog.dismiss()
            AppPersistence.clear()
            onDestroy()
        }

        discardButton?.setOnClickListener {
            bottomDialog.dismiss()
        }

        bottomDialog.show()
    }

    private fun createShareDialog() {
        val bottomDialog = createBottomSheetDialog(
            layoutInflater.inflate(
                R.layout.dialog_confirm_share,
                null
            )
        )

        val acceptButton = bottomDialog.findViewById<MaterialCardView>(R.id.share_button)
        val discardButton = bottomDialog.findViewById<MaterialCardView>(R.id.cancel_button)

        acceptButton?.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)

            bottomDialog.dismiss()
        }

        discardButton?.setOnClickListener {
            bottomDialog.dismiss()
        }

        bottomDialog.show()
    }



}


