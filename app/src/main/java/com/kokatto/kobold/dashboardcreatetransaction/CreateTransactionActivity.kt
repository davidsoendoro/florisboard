package com.kokatto.kobold.dashboardcreatetransaction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kokatto.kobold.R
import com.kokatto.kobold.dashboardcreatetransaction.pageradapter.CreateTransactionPagerAdapter
import dev.patrickgold.florisboard.setup.SetupActivity
import dev.patrickgold.florisboard.util.checkIfImeIsEnabled

class CreateTransactionActivity : AppCompatActivity(), CreateTransactionPagerAdapter.Delegate {

    private var activeButton: Button? = null
    private var warningLayout: LinearLayout? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_transaction)

        activeButton = findViewById(R.id.popup_keyboard_active_button)
        warningLayout = findViewById(R.id.layout_active_keyboard)

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

        val createTransactionPagerAdapter = CreateTransactionPagerAdapter(supportFragmentManager, lifecycle, this)
        val viewPager = findViewById<ViewPager2>(R.id.viewpager_layout)
        viewPager.adapter = createTransactionPagerAdapter

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = fragmentEnabledCount[position]
        }.attach()

        findViewById<CardView>(R.id.create_transaction_button).setOnClickListener {
            startActivity(Intent(this, InputActivity::class.java))
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
}
