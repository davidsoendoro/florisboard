package com.kokatto.kobold.bank

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.kokatto.kobold.R
import com.kokatto.kobold.dashboardcreatetransaction.InputActivity
import com.kokatto.kobold.databinding.ActivityBankBinding
import dev.patrickgold.florisboard.settings.FRAGMENT_TAG

class BankActivity : AppCompatActivity() {

    lateinit var binding: ActivityBankBinding

    private var inputAtivityResult: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_bank)

        inputAtivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
            }
        }

        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            onBackPressed()
        }

        findViewById<CardView>(R.id.action_create).setOnClickListener {
            val intent = Intent(this, InputActivity::class.java)
            inputAtivityResult!!.launch(intent)
        }

        loadFragment(BankListFragment())

    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(binding.fragmentContainer.id, fragment, FRAGMENT_TAG)
            .commit()
    }


}
