package com.kokatto.kobold.dashboardcreatetransaction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kokatto.kobold.R
import com.kokatto.kobold.dashboardcreatetransaction.pageradapter.CreateTransactionPagerAdapter

class CreateTransactionActivity : AppCompatActivity(), CreateTransactionPagerAdapter.Delegate {

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

        val createTransactionPagerAdapter = CreateTransactionPagerAdapter(supportFragmentManager, lifecycle, this)
        val viewPager = findViewById<ViewPager2>(R.id.viewpager_layout)
        viewPager.adapter = createTransactionPagerAdapter

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = fragmentEnabledCount[position]
        }.attach()
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
