package com.kokatto.kobold.bank

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.api.model.basemodel.BankModel
import com.kokatto.kobold.component.DashboardThemeActivity
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.databinding.ActivityBankHomeBinding
import com.kokatto.kobold.extension.showSnackBar
import dev.patrickgold.florisboard.settings.FRAGMENT_TAG

class BankHomeActivity : DashboardThemeActivity() {

    lateinit var uiBinding: ActivityBankHomeBinding

    private var inputActivityResult: ActivityResultLauncher<Intent>? = null
    private var bankListFragment: BankListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiBinding = ActivityBankHomeBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        inputActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            println("inputActivityResult ::: ${result.resultCode}")

            when (result.resultCode) {
                ActivityConstantCode.RESULT_OK_DELETED -> {
                    showSnackBar(
                        uiBinding.rootLayout,
                        resources.getString(R.string.kobold_bank_input_action_delete_success)
                    )
                    loadFragment(openDataListFragment())
                }
                ActivityConstantCode.RESULT_OK_CREATED -> {
                    showSnackBar(
                        uiBinding.rootLayout,
                        resources.getString(R.string.kobold_bank_input_action_submit_success)
                    )
                    loadFragment(openDataListFragment())
                }
                ActivityConstantCode.RESULT_OK_UPDATED -> {
                    showSnackBar(
                        uiBinding.rootLayout,
                        resources.getString(R.string.kobold_bank_input_action_update_success)
                    )
                    loadFragment(openDataListFragment())
                }
            }
        }

        uiBinding.backButton.setOnClickListener {
            onBackPressed()
        }

        findViewById<CardView>(R.id.action_create).setOnClickListener {
            //validate max 15 bank account
            if (bankListFragment != null) {
                if (bankListFragment!!.isReachMaximum()) {
                    showSnackBar(
                        findViewById(R.id.root_layout),
                        resources.getString(R.string.kobold_bank_maximum),
                        R.color.snackbar_error
                    )
                } else {
                    val intent = Intent(this, BankInputActivity::class.java)
                    inputActivityResult!!.launch(intent)
                }
            }
        }

        loadFragment(openDataListFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(uiBinding.fragmentContainer.id, fragment, FRAGMENT_TAG)
            .commit()
    }

    private fun openDataListFragment(): BankListFragment {
        bankListFragment = BankListFragment()

        bankListFragment?.onEmptyResult = {
            uiBinding.actionCreate.visibility = View.GONE
            loadFragment(openEmptyFragment())
        }

        bankListFragment?.onRowClick = {
            val intent = Intent(this, BankInputActivity::class.java)
            intent.putExtra(ActivityConstantCode.EXTRA_MODE, ActivityConstantCode.EXTRA_EDIT)
            intent.putExtra(ActivityConstantCode.EXTRA_DATA, it)
            inputActivityResult?.launch(intent)
        }

        uiBinding.actionCreate.visibility = View.VISIBLE
        return bankListFragment as BankListFragment
    }

    private fun openEmptyFragment(): Fragment {
        val fragment = BankEmptyFragment()

        fragment.onActionClick = {
            val intent = Intent(this, BankInputActivity::class.java)
            inputActivityResult?.launch(intent)
        }

        return fragment
    }
}
