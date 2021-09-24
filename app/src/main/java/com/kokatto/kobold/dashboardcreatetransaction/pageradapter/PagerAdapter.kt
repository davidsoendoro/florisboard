package com.kokatto.kobold.dashboardcreatetransaction.pageradapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter (
    fm: FragmentManager,
    lifecycle: Lifecycle,
    private val delegate: Delegate
) :
    FragmentStateAdapter(fm, lifecycle) {

    interface Delegate {
        fun getItemCount(): Int
        fun createFragment(position: Int): Fragment
    }

    override fun getItemCount() = delegate.getItemCount()

    override fun createFragment(position: Int) = delegate.createFragment(position)



}
