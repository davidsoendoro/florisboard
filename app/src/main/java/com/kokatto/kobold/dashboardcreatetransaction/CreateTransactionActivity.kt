package com.kokatto.kobold.dashboardcreatetransaction

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.dashboardcreatetransaction.pageradapter.PagerAdapter
import com.kokatto.kobold.extension.showSnackBar
import dev.patrickgold.florisboard.setup.SetupActivity
import dev.patrickgold.florisboard.util.checkIfImeIsEnabled

interface CreateTransactionActivityListener {
    fun openInputActivity()
    fun openDetailActivity(dataModel: TransactionModel)
}

class CreateTransactionActivity : AppCompatActivity(), PagerAdapter.Delegate, CreateTransactionActivityListener {

    private var activeButton: Button? = null
    private var warningLayout: LinearLayout? = null
    private var viewPager: ViewPager2? = null

    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_transaction)

        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            onBackPressed()
        }

        activeButton = findViewById(R.id.popup_keyboard_active_button)
        warningLayout = findViewById(R.id.layout_active_keyboard)

        findViewById<ImageView>(R.id.archive_button).setOnClickListener {
            startActivity(Intent(this, ArchiveActivity::class.java))
        }

        findViewById<ImageView>(R.id.search_button).setOnClickListener {
            startActivity(Intent(this, SearchTransactionActivity::class.java))
        }

        activeButton!!.setOnClickListener {
            Intent(this, SetupActivity::class.java).apply {
                putExtra(SetupActivity.EXTRA_SHOW_SINGLE_STEP, SetupActivity.Step.MAKE_DEFAULT)
                startActivity(this)
                warningLayout?.let { layout -> layout.visibility = View.GONE }
            }
        }

        if (checkIfImeIsEnabled(this)) {
            warningLayout?.let { layout -> layout.visibility = View.GONE }
        } else {
            warningLayout?.let { layout -> layout.visibility = View.VISIBLE }
        }

        viewPager = findViewById<ViewPager2>(R.id.viewpager_layout)
        viewPager?.adapter = PagerAdapter(supportFragmentManager, lifecycle, this)

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager!!, true) { tab, position ->
            tab.text = fragmentEnabledCount[position]
        }.attach()

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when(result.resultCode) {
                ActivityConstantCode.RESULT_OK_CREATED -> {
                    viewPager?.adapter = PagerAdapter(supportFragmentManager, lifecycle, this)
                    showSnackBar(findViewById(R.id.root_layout), resources.getString(R.string.kobold_transaction_action_save_success))
                }
                ActivityConstantCode.RESULT_OK_UPDATED -> {
                    viewPager?.adapter = PagerAdapter(supportFragmentManager, lifecycle, this)
                    showSnackBar(findViewById(R.id.root_layout), resources.getString(R.string.kobold_transaction_action_edit_success))
                }
                ActivityConstantCode.RESULT_OK_DELETED -> {
                    viewPager?.adapter = PagerAdapter(supportFragmentManager, lifecycle, this)
                    showSnackBar(findViewById(R.id.root_layout), resources.getString(R.string.template_delete_success))
                }
                ActivityConstantCode.STATUS_TO_PAID -> {
                    viewPager?.adapter = PagerAdapter(supportFragmentManager, lifecycle, this)
                    showSnackBar(findViewById(R.id.root_layout), resources.getString(R.string.kobold_transaction_paid_toast))
                }
                ActivityConstantCode.STATUS_TO_UNPAID -> {
                    viewPager?.adapter = PagerAdapter(supportFragmentManager, lifecycle, this)
                    showSnackBar(findViewById(R.id.root_layout), resources.getString(R.string.kobold_transaction_unpaid_toast))
                }
                ActivityConstantCode.STATUS_TO_SENT -> {
                    viewPager?.adapter = PagerAdapter(supportFragmentManager, lifecycle, this)
                    showSnackBar(findViewById(R.id.root_layout), resources.getString(R.string.kobold_transaction_sent_toast))
                }
                ActivityConstantCode.STATUS_TO_COMPLETE -> {
                    viewPager?.adapter = PagerAdapter(supportFragmentManager, lifecycle, this)
                    showSnackBar(findViewById(R.id.root_layout), resources.getString(R.string.kobold_transaction_finish_toast))
                }
                ActivityConstantCode.STATUS_TO_CANCEL -> {
                    viewPager?.adapter = PagerAdapter(supportFragmentManager, lifecycle, this)
                    showSnackBar(findViewById(R.id.root_layout), resources.getString(R.string.kobold_transaction_cancel_toast))
                }
                ActivityConstantCode.RESULT_OPEN_EDIT -> {
                    val data: Intent? = result.data
                    val model = data?.getParcelableExtra<TransactionModel>(ActivityConstantCode.EXTRA_DATA)
                    val intent = Intent(this, InputActivity::class.java)
                    intent.putExtra(InputActivity.EXTRA_DATA, model)
                    intent.putExtra(InputActivity.MODE, InputActivity.EDIT)
                    activityResultLauncher?.launch(intent)
                }
            }
        }

        findViewById<CardView>(R.id.create_transaction_button).setOnClickListener {
            val intent = Intent(this, InputActivity::class.java)
            activityResultLauncher?.launch(intent)
        }
    }

    override fun getItemCount(): Int = fragmentEnabledCount.size

    override fun createFragment(position: Int): Fragment {
        return when(fragmentEnabledCount[position]) {
            UNPROCESSED -> UnprocessedFragment()
            PAID -> PaidFragment()
            SENT -> SentFragment()
            else -> throw Error("${fragmentEnabledCount[position]} Not implemented yet")
        }
    }

    private companion object {
        const val UNPROCESSED = "Belum diproses"
        const val PAID = "Dibayar"
        const val SENT = "Dikirim"
        val fragmentEnabledCount = arrayListOf<String>().apply {
            this.add(UNPROCESSED)
            this.add(PAID)
            this.add(SENT)
        }
    }

    override fun openInputActivity() {
        val intent = Intent(this, InputActivity::class.java)
        activityResultLauncher?.launch(intent)
    }

    override fun openDetailActivity(dataModel: TransactionModel){
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_DATA, dataModel)
        activityResultLauncher?.launch(intent)
    }

    override fun onResume() {
        super.onResume()

        if (checkIfImeIsEnabled(this)) {
            warningLayout?.let { layout -> layout.visibility = View.GONE }
        } else {
            warningLayout?.let { layout -> layout.visibility = View.VISIBLE }
        }
    }
}
