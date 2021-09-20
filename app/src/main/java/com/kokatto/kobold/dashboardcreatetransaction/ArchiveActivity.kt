package com.kokatto.kobold.dashboardcreatetransaction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kokatto.kobold.R
import com.kokatto.kobold.dashboardcreatetransaction.pageradapter.PagerAdapter
import dev.patrickgold.florisboard.setup.SetupActivity
import dev.patrickgold.florisboard.util.checkIfImeIsEnabled

class ArchiveActivity : AppCompatActivity(), PagerAdapter.Delegate {
    private var activeButton: Button? = null
    private var warningLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)

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

        val createTransactionPagerAdapter = PagerAdapter(supportFragmentManager, lifecycle, this)
        val viewPager = findViewById<ViewPager2>(R.id.viewpager_layout)
        viewPager.adapter = createTransactionPagerAdapter

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = fragmentEnabledCount[position]
        }.attach()
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
}
