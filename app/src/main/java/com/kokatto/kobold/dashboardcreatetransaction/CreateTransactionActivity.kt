package com.kokatto.kobold.dashboardcreatetransaction

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.dashboardcreatetransaction.pageradapter.PagerAdapter
import dev.patrickgold.florisboard.setup.SetupActivity
import dev.patrickgold.florisboard.util.checkIfImeIsEnabled
import okhttp3.internal.notifyAll

class CreateTransactionActivity : AppCompatActivity(), PagerAdapter.Delegate {

    private var activeButton: Button? = null
    private var warningLayout: LinearLayout? = null
    private var viewPager: ViewPager2? = null

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

        var launchActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewPager?.adapter = PagerAdapter(supportFragmentManager, lifecycle, this)
            }
        }

        findViewById<CardView>(R.id.create_transaction_button).setOnClickListener {
            val intent = Intent(this, InputActivity::class.java)
            launchActivity.launch(intent)
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

    fun openDetailActivity(model: TransactionModel){
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_DATA, model)

        val launchActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

            }
        }

        launchActivity.launch(intent)
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
