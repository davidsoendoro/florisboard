package com.kokatto.kobold.setting

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.kokatto.kobold.R
import com.kokatto.kobold.databinding.ActivityBankHomeBinding
import com.kokatto.kobold.databinding.ActivityDashboardBinding
import com.kokatto.kobold.databinding.ActivitySettingProfilTokoBinding
import com.kokatto.kobold.databinding.SettingsActivityBinding
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.showToast

class SettingProfilTokoActivity : AppCompatActivity() {
    lateinit var uiBinding: ActivitySettingProfilTokoBinding
    var settingViewModel: SettingViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_profil_toko)

        uiBinding = ActivitySettingProfilTokoBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }
        uiBinding.backButton.setOnClickListener {
            onBackPressed()
        }

        uiBinding.settingStoreHelpText.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("http://api.whatsapp.com/send?phone=+6285692689187&text=Halo, saya ingin mengganti nomor telpon..." )
            startActivity(intent)
        }

        settingViewModel = SettingViewModel()
        settingViewModel?.getMerchantInfo(
            onLoading = {
                showToast(it.toString())
            },
            onSuccess = {
            //on data success loaded from backend
                uiBinding.edittextSettingStorename.setText(it.name)
                uiBinding.edittextSettingBusinessField.setText(it.businessField.toString())
                uiBinding.edittextSettingBusinessType.setText(it.businessType)
                uiBinding.edittextSettingStorephone.setText(it.phone)
                showToast(it.phone)
            },
            onError = {
                //on data error when loading from backend
                showSnackBar(it)
            }
        )



    }


}
