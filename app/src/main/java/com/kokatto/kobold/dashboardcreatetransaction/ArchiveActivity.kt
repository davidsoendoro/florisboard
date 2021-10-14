package com.kokatto.kobold.dashboardcreatetransaction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter.POSITION_NONE
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.component.DashboardThemeActivity
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.dashboardcreatetransaction.pageradapter.PagerAdapter
import com.kokatto.kobold.extension.showSnackBar
import dev.patrickgold.florisboard.setup.SetupActivity
import dev.patrickgold.florisboard.util.checkIfImeIsEnabled
import okhttp3.internal.notifyAll

interface ArchiveActivityListener {
    fun openDetailActivity(dataModel: TransactionModel)
}

class ArchiveActivity : DashboardThemeActivity(), PagerAdapter.Delegate, ArchiveActivityListener {
    private var activeButton: Button? = null
    private var warningLayout: LinearLayout? = null
    private var viewPager: ViewPager2? = null
    private var tabLayout: TabLayout? = null
    private var pageAdapter: PagerAdapter? = null

    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)

        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            onBackPressed()
        }

        activeButton = findViewById(R.id.popup_keyboard_active_button)
        warningLayout = findViewById(R.id.layout_active_keyboard)

        findViewById<ImageView>(R.id.search_button).setOnClickListener {
            startActivity(Intent(this, SearchTransactionActivity::class.java))
        }

        activeButton!!.setOnClickListener {
            Intent(this, SetupActivity::class.java).apply {
                putExtra(SetupActivity.EXTRA_SHOW_SINGLE_STEP, SetupActivity.Step.FINISH)
                startActivity(this)
                warningLayout?.let { layout -> layout.visibility = View.GONE }
            }
        }

        if (checkIfImeIsEnabled(this)) {
            warningLayout?.let { layout -> layout.visibility = View.GONE }
        } else {
            warningLayout?.let { layout -> layout.visibility = View.VISIBLE }
        }

        pageAdapter = PagerAdapter(supportFragmentManager, lifecycle, this)
        viewPager = findViewById<ViewPager2>(R.id.viewpager_layout)
        viewPager?.adapter = pageAdapter

        tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->
            tab.text = fragmentEnabledCount[position]
        }.attach()

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when(result.resultCode) {
                ActivityConstantCode.RESULT_OK_UPDATED -> {
                    viewPager!!.refreshDrawableState()
                    showSnackBar(findViewById(R.id.root_layout), resources.getString(R.string.kobold_transaction_action_edit_success))
                }
                ActivityConstantCode.RESULT_OPEN_EDIT_COMPLETE -> {
                    val data: Intent? = result.data
                    val model = data?.getParcelableExtra<TransactionModel>(ActivityConstantCode.EXTRA_DATA)
                    val intent = Intent(this, InputActivity::class.java)
                    intent.putExtra(InputActivity.EXTRA_DATA, model)
                    intent.putExtra(InputActivity.MODE, InputActivity.EDIT_COMPLETE)
                    activityResultLauncher?.launch(intent)
                }
            }

        }
    }

    private companion object {
        const val COMPLETE = "Selesai"
        const val CANCELLED = "Dibatalkan"
        val fragmentEnabledCount = arrayListOf<String>().apply {
            this.add(COMPLETE)
            this.add(CANCELLED)
        }
    }

    override fun getItemCount(): Int =
        fragmentEnabledCount.size


    override fun createFragment(position: Int): Fragment {
        return when(fragmentEnabledCount[position]) {
            COMPLETE -> CompleteFragment()
            CANCELLED -> CancelledFragment()
            else -> throw Error("${fragmentEnabledCount[position]} Not implemented yet")
        }
    }


    override fun openDetailActivity(dataModel: TransactionModel) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_DATA, dataModel)
        activityResultLauncher?.launch(intent)
    }
}
