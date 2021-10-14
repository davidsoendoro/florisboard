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
import com.kokatto.kobold.databinding.ActivitySettingBinding
import com.kokatto.kobold.databinding.SettingsActivityBinding
import com.kokatto.kobold.extension.createBottomSheetDialog
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.persistance.AppPersistence

class SettingActivity : AppCompatActivity() {
    private lateinit var rlProfilToko: RelativeLayout
    private lateinit var btnLogOut: Button
    private lateinit var rlBankAccount: RelativeLayout
    private lateinit var rlGiveRating: RelativeLayout
    private lateinit var rlTermCondition: RelativeLayout
    private lateinit var rlHelp: RelativeLayout
    private lateinit var llShareApp: LinearLayout
    lateinit var uiBinding: ActivitySettingBinding

    var settingViewModel: SettingViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        uiBinding = ActivitySettingBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        settingViewModel = SettingViewModel()

        settingViewModel?.getMerchantInfo(
            onLoading = {
                //on data is loading
                showToast(it.toString())
            },
            onSuccess = {
                //on data success loaded from backend
//                uiBinding.koboltMerchantStoreName.setText(
//                    if(it.name.isEmpty())
//                        "-"
//                    else
//                        it.name)
//
//                uiBinding.koboltMerchantStorePhone.setText(
//                    if(it.phone.isEmpty())
//                        "-"
//                    else
//                        it.phone)
            },
            onError = {
                //on data error when loading from backend
                showSnackBar(it)
            }
        )


        uiBinding.backButton.setOnClickListener {
            onBackPressed()
        }

        rlProfilToko = findViewById(R.id.kobold_setting_store_profile)
        rlProfilToko.setOnClickListener {
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

        rlHelp = findViewById(R.id.kubold_open_help)
        rlHelp.setOnClickListener{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://kobold-microsite.kokatto.net/help"))
            startActivity(browserIntent)
        }

        rlTermCondition = findViewById(R.id.kubold_open_term_conditions)
        rlTermCondition.setOnClickListener{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"))
            startActivity(browserIntent)
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

            startActivity(Intent(this@SettingActivity,DashboardActivity ::class.java))
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
                putExtra(Intent.EXTRA_TEXT, "Yuk cobain Konekin, fitur dari Aplikasi yang bikin jualan online jadi lebih gampang. " +
                    "\n\nSalah satu fitur Konekin, Keyboard Jualan bisa bikin pesanan, cek ongkir sampai bikin invoice langsung dari aplikasi chat favorit kamu. " +
                    "\n\nTemukan Konekin di Play Store! " +
                    "\n\nKonekin.id")
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

