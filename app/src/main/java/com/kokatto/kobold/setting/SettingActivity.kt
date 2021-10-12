package com.kokatto.kobold.setting

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.kokatto.kobold.R
import com.kokatto.kobold.databinding.SettingsActivityBinding
import com.kokatto.kobold.extension.createBottomSheetDialog

class SettingActivity : AppCompatActivity() {
    private lateinit var ivProfilToko: ImageView
    private lateinit var btnLogOut: Button
    lateinit var uiBinding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        uiBinding = SettingsActivityBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }
        uiBinding.backButton.setOnClickListener {
            onBackPressed()
        }

        ivProfilToko = findViewById(R.id.kobold_setting_store_profile);
        ivProfilToko.setOnClickListener{
            startActivity(Intent(this@SettingActivity, SettingProfilTokoActivity::class.java))
        }

        btnLogOut = findViewById(R.id.kobold_SettingLogoutButton);
        btnLogOut.setOnClickListener{
            createConfirmationDialog();
        }



    }

    private fun createConfirmationDialog() {
        val bottomDialog = createBottomSheetDialog(
            layoutInflater.inflate(
                R.layout.dialog_confirm_log_out,
                null
            )
        )

        val acceptButton = bottomDialog.findViewById<MaterialCardView>(R.id.confirm_button)
        val discardButton = bottomDialog.findViewById<MaterialCardView>(R.id.cancel_button)

        acceptButton?.setOnClickListener {


            bottomDialog.dismiss()
        }

        discardButton?.setOnClickListener {
            bottomDialog.dismiss()
        }

        bottomDialog.show()
    }
}

