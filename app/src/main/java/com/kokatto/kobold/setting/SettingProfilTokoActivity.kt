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

class SettingProfilTokoActivity : AppCompatActivity() {
    lateinit var uiBinding: ActivitySettingProfilTokoBinding
    private lateinit var tvCustService: TextView
    private lateinit var binding: ActivitySettingProfilTokoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_profil_toko)
        binding = ActivitySettingProfilTokoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uiBinding = ActivitySettingProfilTokoBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }
        uiBinding.backButton.setOnClickListener {
            onBackPressed()
        }

        tvCustService = findViewById(R.id.setting_store_help_text);
        tvCustService.setOnClickListener{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/6281951245122"))
            startActivity(browserIntent)
        }

//        binding.setting_store_help_text.setOnClickListener {
//            //TODO: change activity after ready
//
//            Toast.makeText(this, "Activity still not ready", Toast.LENGTH_LONG).show()
//
//        }

    }
}
